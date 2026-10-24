# Testing — Student Academic & Course Management System

## Environment

- OS: Windows 11
- JDK: openjdk 21.0.10 (javac 21.0.10)
- MySQL server: **NOT installed** in this environment (no `mysql` client, nothing
  listening on port 3306)
- JDBC driver: `lib/mysql-connector-j-8.3.0.jar` (MySQL Connector/J 8.3.0,
  driver class `com.mysql.cj.jdbc.Driver`)

## Tests executed in this environment

| # | Test | Result | Evidence |
|---|------|--------|----------|
| 1 | Full project compiles with `javac` (JDK 21 + JDBC driver on classpath, no build tool) | ✅ Passed | `javac -d build -cp "lib/mysql-connector-j-8.3.0.jar" <all sources>` → EXIT 0 |
| 2 | Application launches (MainFrame opens Dashboard window) | ✅ Passed | `java -cp "build;lib/mysql-connector-j-8.3.0.jar" app.Main` → window opened, closed cleanly |
| 3 | All classes compile into `build/` | ✅ Passed | 50+ `.class` files generated across `app`, `model`, `dao`, `service`, `db`, `ui`, `util` |

## Tests requiring a live MySQL server (could not be executed here)

The following acceptance criteria are **implemented** in code and verified by
inspection/compilation, but could not be run end-to-end because no MySQL server
is available in this environment:

| # | Criterion | Implementation |
|---|-----------|----------------|
| 2 | Database connection works with valid config | `DatabaseConnection.getConnection()` + header status indicator |
| 3 | Connection failures produce understandable messages | `AppException("DB_CONNECTION_FAILED", ...)` with user-friendly guidance |
| 4 | CRUD for students/faculty/courses | DAO insert/update/delete + service validation + Swing UI |
| 5 | Data persists after close/reopen | MySQL InnoDB persistent tables via JDBC |
| 6 | Duplicate identifiers/enrollments rejected | DB UNIQUE constraints + service pre-checks |
| 7 | Invalid inputs rejected with messages | `InputValidator` (required, email, phone, marks range, dates, etc.) |
| 8 | Enrollment relationships correct | FKs + `EnrollmentService` validates student & course exist |
| 9 | Attendance entered/updated/calculated | Transactional save; `AttendanceService` percentage rule |
| 10 | Marks/percentages/grades correct | `BigDecimal` arithmetic + `GradeService` scale |
| 11 | Invalid marks cannot be stored | `InputValidator.marks` rejects negative / > maximum |
| 12 | Reports reflect actual DB records | `ReportService` queries MySQL for every report |
| 13 | Referential integrity maintained | FK RESTRICT on students/courses/enrollments; SET NULL on faculty |
| 14 | Transactions roll back on failure | `commit()`/`rollback()` in Enrollment, Attendance, Result services |
| 15 | Dashboard stats use real data | `ReportService.totalStudents()`, etc., query MySQL |
| 16 | UI responsive during DB ops | All blocking calls wrapped in `SwingWorker` |
| 17 | Five OOP concepts implemented | Encapsulation, Inheritance, Abstraction, Polymorphism, Classes/Objects |
| 18 | Compiles & runs with JDK + JDBC only | Verified (see tests 1–3) |

## Recommended manual test cases (run against a local MySQL instance)

1. **Add student** → appears in table → search by name finds it → edit details → save.
2. **Duplicate student ID / roll number / email** → second add rejected with "already exists".
3. **Delete student with an enrollment** → blocked (FK RESTRICT) with a clear message.
4. **Enter marks above course maximum** → rejected with validation message.
5. **Enroll same student in same course twice (Active)** → second enrollment rejected.
6. **Mark attendance for course + date** → save → mark the same date again → updated, not duplicated.
7. **Delete a course that has enrollments** → blocked (FK RESTRICT).
8. **Stop MySQL server → use app** → header shows "Database disconnected", no crash.
9. **Close and reopen app** → previously saved records still present.
10. **Run each report** → tables populated from MySQL, not hardcoded.

## Notes

- Because MySQL was unavailable, no live SQL integration tests were executed.
- No results are claimed as "passed" unless actually executed above.
- To complete validation, install MySQL, run `sql/schema.sql`, configure
  `config/db.properties`, and run the manual test cases listed.
