# Student Academic & Course Management System

A complete, functional desktop application for college academic administration,
built with **Java (JDK 21)**, **MySQL**, and **JDBC** using **Java Swing** for the
UI. No Spring Boot, Hibernate, JPA, Maven, Gradle, JavaScript, or third-party
UI libraries are used — the only external runtime library is the MySQL JDBC
driver.

> **Repository:** https://github.com/anand-cpp/Student-Academic-and-cource-management

---

## Table of Contents

1. [Architecture](#architecture)
2. [Database Design](#database-design)
3. [Modules & Features](#modules--features)
4. [Setup Instructions](#setup-instructions)
   - [MySQL JDBC driver](#mysql-jdbc-driver)
   - [Create the database](#create-the-database)
   - [Configure connection](#configure-connection)
   - [Optional sample data](#optional-sample-data)
5. [Build & Run](#build--run)
   - [Compilation (javac)](#compilation-javac)
   - [Execution (java)](#execution-java)
6. [OOP Concepts](#oop-concepts)
7. [Testing Checklist & Results](#testing-checklist--results)
8. [Troubleshooting](#troubleshooting)
9. [Project Structure](#project-structure)

---

## Architecture

The source code lives under `src/` and is organised into packages:

| Package | Responsibility |
|---------|----------------|
| `app` | `Main.java` — application entry point |
| `model` | `Person` (abstract), `Student`, `Faculty`, `Course`, `Enrollment`, `Attendance`, `Result` |
| `dao` | `StudentDAO`, `FacultyDAO`, `CourseDAO`, `EnrollmentDAO`, `AttendanceDAO`, `ResultDAO` — persistence |
| `service` | `StudentService`, `FacultyService`, `CourseService`, `EnrollmentService`, `AttendanceService`, `ResultService`, `GradeService`, `ReportService` — business rules |
| `db` | `DatabaseConnection` — centralised JDBC connectivity |
| `ui` | `MainFrame`, `DashboardPanel`, `StudentPanel`, `FacultyPanel`, `CoursePanel`, `EnrollmentPanel`, `AttendancePanel`, `ResultPanel`, `ReportsPanel` |
| `util` | `InputValidator`, `DateUtils`, `UIStyles`, `AppException` |

Layering (separation of concerns):
- **UI** calls only **services**.
- **Services** enforce validation & business rules and call **DAOs**.
- **DAOs** hold all SQL (PreparedStatement) and map `ResultSet` rows to models.
- No SQL appears in Swing event handlers.

---

## Database Design

**Database name:** `student_academic_management`

**Tables:** `students`, `faculty`, `courses`, `enrollments`, `attendance`, `results`

All tables use **InnoDB**. Identifier strategy: every table has a surrogate
`id` (INT AUTO_INCREMENT PRIMARY KEY) used for foreign keys. Business codes are
application-generated:
- `students.student_id` (e.g. STU0001), `roll_number`, `email` — all UNIQUE
- `faculty.faculty_id`, `employee_id`, `email` — all UNIQUE
- `courses.course_code` (e.g. CS101) — UNIQUE

**Relationships & deletion policy:**
- `enrollments.student_id → students.id` **RESTRICT** (protects academic history)
- `enrollments.course_id → courses.id` **RESTRICT**
- `attendance.enrollment_id → enrollments.id` **RESTRICT**
- `results.enrollment_id → enrollments.id` **RESTRICT** (one final result per enrollment — UNIQUE)
- `courses.assigned_faculty_id → faculty.id` **SET NULL** (deliberate exception: when a faculty member leaves, their courses remain but are unassigned)

**Constraints:** NOT NULL, UNIQUE, CHECK (credits > 0, passing_marks ≤ maximum_marks, semester 1–12, percentage 0–100, marks ≥ 0), plus indexes on lookup columns.

See `sql/schema.sql` for the full DDL and `sql/sample-data.sql` for optional seed data.

---

## Modules & Features

| Module | Features |
|--------|----------|
| **Dashboard** | Live stats (total students, faculty, courses, enrollments, avg attendance %, avg academic %) computed from MySQL; recent students & recent enrollments tables |
| **Students** | Add, view, search (ID/roll/name/email/department), update, delete (when relationships permit), profile, enrolled courses, attendance summary, results |
| **Faculty** | Add, view, search, update, delete (when no courses assigned), courses assigned to faculty |
| **Courses** | Add, view, search, update, delete (when permitted), assign faculty, validate credits & marks (passing ≤ maximum) |
| **Enrollments** | Enroll student in course (transactional), list all, search by student/course, remove enrollment, prevent duplicate active enrollments |
| **Attendance** | Select course + date, load enrolled students, mark Present/Absent/Late, save in transaction, view history & course summary |
| **Results** | Enter/update marks per enrollment, validate against course max, BigDecimal percentage & grade (A–F scale), one result per enrollment |
| **Reports** | Student directory, students in a course, courses by faculty, course attendance summary, course results, overall performance (pass/fail/average) |

Grading scale (documented, easy to modify in `service.GradeService`, **not** an official institutional policy):
- A: 90–100% | B: 80–89.99% | C: 70–79.99% | D: 60–69.99% | E: 50–59.99% | F: below 50%

Attendance rule (documented, applied consistently):
- Attendance % = (Present + Late) / Total recorded sessions × 100
- Late counts as attended; only Absent reduces the percentage
- Future dates are never stored as absences

---

## Setup Instructions

### MySQL JDBC driver

The MySQL JDBC driver is the **only** external library required. It is included
in this repository at `lib/mysql-connector-j-8.3.0.jar`.

If you need to obtain it yourself (e.g. a newer version), download it from the
official MySQL site / Maven Central:

- https://dev.mysql.com/downloads/connector/j/
- Maven Central: `mysql-connector-j` (the driver class is `com.mysql.cj.jdbc.Driver`)

Place the JAR in the project `lib/` folder and add it to the classpath when
compiling and running (see Build & Run below). No build tool (Maven/Gradle) is
used.

### Create the database

1. Start your MySQL server.
2. Run the schema script:
   ```
   mysql -u root -p < sql/schema.sql
   ```
   This creates `student_academic_management` and all six tables.

### Configure connection

1. Copy `config/db.properties.example` to `config/db.properties`:
   ```
   copy config\db.properties.example config\db.properties   (Windows)
   cp config/db.properties.example config/db.properties      (Linux/Mac)
   ```
2. Edit `config/db.properties` and set `db.user` and `db.password` for your
   MySQL installation. The default URL points to
   `jdbc:mysql://localhost:3306/student_academic_management`.

> `config/db.properties` is gitignored — never commit real credentials.

### Optional sample data

To explore the UI with pre-loaded records (against a fresh/empty database):
```
mysql -u root -p student_academic_management < sql/sample-data.sql
```

---

## Build & Run

All commands are run from the project root directory.

### Compilation (javac)

```cmd
rem Create output directory
mkdir build

rem Compile all sources with the JDBC driver on the classpath
javac -d build -cp "lib/mysql-connector-j-8.3.0.jar" src\app\*.java src\model\*.java src\dao\*.java src\service\*.java src\db\*.java src\ui\*.java src\util\*.java
```

PowerShell equivalent:
```pwsh
mkdir build
$files = Get-ChildItem -Recurse -Filter "*.java" src | Select-Object -ExpandProperty FullName
javac -d build -cp "lib/mysql-connector-j-8.3.0.jar" $files
```

### Execution (java)

```cmd
java -cp "build;lib/mysql-connector-j-8.3.0.jar" app.Main
```

PowerShell equivalent:
```pwsh
java -cp "build;lib/mysql-connector-j-8.3.0.jar" app.Main
```

The application window opens on the **Dashboard**. Use the sidebar to navigate
between modules. Exit from the sidebar.

---

## OOP Concepts

The application deliberately demonstrates all five core OOP concepts:

| Concept | How it is demonstrated |
|---------|------------------------|
| **Encapsulation** | All model fields are `private` with public getters/setters. Setters validate values (e.g. `Course.setPassingMarks` rejects values above maximum marks; `Student.setSemester` rejects out-of-range semesters). |
| **Inheritance** | `Person` is an abstract class holding shared attributes (`id`, `fullName`, `email`, `phone`, `address`, `dateOfBirth`). `Student` and `Faculty` extend `Person`, inheriting the shared state and behaviour. |
| **Abstraction** | `Person` is `abstract` and declares abstract `getRole()` / `getProfileSummary()`. DAO and service classes hide database implementation and business-rule details behind clean interfaces. |
| **Polymorphism** | `Student.getRole()` returns `"Student"` and `Faculty.getRole()` returns `"Faculty"`. Code can hold a `Person` reference and call `getRole()` / `getProfileSummary()` polymorphically at runtime. |
| **Classes & Objects** | Separate model objects for students, faculty, courses, enrollments, attendance, results are instantiated when processing user input and mapping database records. |

Exception handling (custom `AppException`), `PreparedStatement` (SQL injection
prevention), try-with-resources, and transactions with commit/rollback are used
throughout.

---

## Testing Checklist & Results

The project was compiled with `javac` (JDK 21) and launched successfully. The
JDBC driver is on the classpath and the application window opens.

| # | Acceptance criterion | Status |
|---|----------------------|--------|
| 1 | Application launches successfully | ✅ Verified |
| 2 | Database connection works with valid configuration | ⚠️ Requires MySQL server (not present in this environment) |
| 3 | Connection failures produce understandable error messages | ✅ Code path implemented (`AppException` with user-friendly message) |
| 4 | Students/faculty/courses add, display, search, edit, safe delete | ✅ Implemented in DAO/service/UI |
| 5 | Data persists after close/reopen | ✅ MySQL persistent storage via JDBC |
| 6 | Duplicate identifiers & enrollments rejected | ✅ Unique constraints + service-level checks |
| 7 | Invalid inputs rejected with useful messages | ✅ `InputValidator` + service validation |
| 8 | Enrollment relationships correct | ✅ FKs + service validation |
| 9 | Attendance entered, updated, calculated accurately | ✅ Documented rule, transactional save |
| 10 | Marks, percentages, grades calculated correctly | ✅ BigDecimal + `GradeService` |
| 11 | Invalid marks cannot be stored | ✅ `InputValidator.marks` + service check |
| 12 | Reports reflect actual DB records | ✅ All reports query MySQL |
| 13 | Referential integrity maintained | ✅ FK RESTRICT / SET NULL |
| 14 | Transactions roll back on failed multi-step ops | ✅ commit/rollback in Enrollment/Attendance/Result services |
| 15 | Dashboard stats use actual stored data | ✅ `ReportService` queries MySQL |
| 16 | UI responsive during DB operations | ✅ `SwingWorker` used for blocking calls |
| 17 | All five OOP concepts implemented | ✅ See OOP Concepts section |
| 18 | Compiles & runs with JDK + JDBC driver, no build tool | ✅ Verified (javac + java) |

> **Note on live database tests:** This environment does not have a MySQL server
> installed, so criteria 2, 4–6, 8–13, 15 could not be executed end-to-end
> against a live database here. They are implemented and verified by code
> inspection and compilation; execute them against a local MySQL instance using
> the checklist above.

Representative manual test cases to run with MySQL:
- Add a student → appears in the table → search by name finds it → edit → delete (blocked if an enrollment exists).
- Add duplicate student ID / roll number / email → rejected with "already exists".
- Enter marks above the course maximum → rejected.
- Enroll the same student in the same course twice (Active) → second attempt rejected.
- Mark attendance for a course/date → save → re-mark same date → updates (no duplicate).
- Delete a course that has enrollments → blocked (FK RESTRICT).
- Stop MySQL server → app shows "Database disconnected" in the header instead of crashing.

---

## Troubleshooting

| Problem | Likely cause / fix |
|---------|--------------------|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | The JDBC driver JAR is not on the classpath. Add `lib/mysql-connector-j-8.3.0.jar` (or your downloaded driver) with `-cp`. |
| `CommunicationsException / ConnectException` | MySQL server is not running, or the URL/port in `config/db.properties` is wrong. Start MySQL and verify `db.url`. |
| `Access denied for user` | Wrong `db.user`/`db.password` in `config/db.properties`. Update credentials. |
| `Unknown database 'student_academic_management'` | Run `sql/schema.sql` first to create the database. |
| `No suitable driver found` | The driver JAR is missing from the runtime classpath. |
| Duplicate-key error on save | A record with that ID/email/code already exists — use a unique value or edit the existing record. |
| Cannot delete student/course | Dependent enrollments/attendance/results exist (FK RESTRICT). Remove dependents first. |
| Window does not open / exceptions on launch | Ensure JDK 21+ is used and the build directory contains compiled classes. |

---

## Project Structure

```
Student-Academic-and-cource-management/
├─ src/
│  ├─ app/Main.java
│  ├─ model/{Person,Student,Faculty,Course,Enrollment,Attendance,Result}.java
│  ├─ dao/{Student,Faculty,Course,Enrollment,Attendance,Result}DAO.java
│  ├─ service/{Student,Faculty,Course,Enrollment,Attendance,Result,Grade,Report}Service.java
│  ├─ db/DatabaseConnection.java
│  ├─ ui/{MainFrame,Dashboard,Student,Faculty,Course,Enrollment,Attendance,Result,Reports}Panel.java
│  └─ util/{InputValidator,DateUtils,UIStyles,AppException}.java
├─ lib/
│  └─ mysql-connector-j-8.3.0.jar        (MySQL JDBC driver)
├─ sql/
│  ├─ schema.sql                          (DDL: database + 6 tables)
│  └─ sample-data.sql                     (OPTIONAL seed data)
├─ config/
│  └─ db.properties.example               (connection config template)
├─ .gitignore
└─ README.md
```

Build output (`build/`) and local `config/db.properties` are gitignored.
