# Student Academic & Course Management System — Diagrams

This file contains all system diagrams for the **Student Academic & Course Management System**.
All diagrams are written in **Mermaid** syntax and can be rendered in GitHub, GitLab, Notion, Obsidian, draw.io, or [mermaid.live](https://mermaid.live).

---

## 1. System Architecture (Layered)

```mermaid
graph TD
    subgraph UI["Presentation Layer (Java Swing)"]
        UI1[MainFrame - Window & Navigation]
        UI2[DashboardPanel - Live Stats]
        UI3[StudentPanel - CRUD]
        UI4[FacultyPanel - CRUD]
        UI5[CoursePanel - CRUD]
        UI6[EnrollmentPanel - Enroll/Remove]
        UI7[AttendancePanel - Mark/Save]
        UI8[ResultPanel - Marks/Grade]
        UI9[ReportsPanel - 6 Reports]
    end

    subgraph Service["Business Service Layer"]
        S1[StudentService - Validation & Rules]
        S2[FacultyService]
        S3[CourseService]
        S4[EnrollmentService - Transactions]
        S5[AttendanceService - Percentage Rule]
        S6[ResultService - BigDecimal + Grading]
        S7[ReportService - SQL Aggregations]
    end

    subgraph DAO["Data Access Object Layer (JDBC)"]
        D1[StudentDAO]
        D2[FacultyDAO]
        D3[CourseDAO]
        D4[EnrollmentDAO]
        D5[AttendanceDAO]
        D6[ResultDAO]
    end

    subgraph DB["Persistence Layer (MySQL 8.4 / InnoDB)"]
        DB1[(students)]
        DB2[(faculty)]
        DB3[(courses)]
        DB4[(enrollments)]
        DB5[(attendance)]
        DB6[(results)]
    end

    UI --> Service
    Service --> DAO
    DAO --> DB

    UI1 -.-> UI2 & UI3 & UI4 & UI5 & UI6 & UI7 & UI8 & UI9
```

---

## 2. Package / Module Dependency Diagram

```mermaid
graph TD
    app[app.Main] --> ui[ui.MainFrame]
    ui --> BasePanel[ui.BasePanel]
    ui --> Dash[ui.DashboardPanel]
    ui --> Stu[ui.StudentPanel]
    ui --> Fac[ui.FacultyPanel]
    ui --> Cou[ui.CoursePanel]
    ui --> Enr[ui.EnrollmentPanel]
    ui --> Att[ui.AttendancePanel]
    ui --> Res[ui.ResultPanel]
    ui --> Rep[ui.ReportsPanel]

    BasePanel --> Comps[ui.components.*]
    Comps --> RB[RoundedButton]
    Comps --> RP[RoundedPanel]
    Comps --> FF[FormField]
    Comps --> SC[StatCard]
    Comps --> SI[SidebarItem]
    Comps --> TP[ToastPanel]
    Comps --> ES[EmptyState]
    Comps --> SB[StatusBadge]
    Comps --> TR[TableRenderer]

    Stu --> SS[service.StudentService]
    Fac --> FS[service.FacultyService]
    Cou --> CS[service.CourseService]
    Enr --> ES2[service.EnrollmentService]
    Att --> AS[service.AttendanceService]
    Res --> RS[service.ResultService]
    Rep --> RP2[service.ReportService]

    SS --> SD[dao.StudentDAO]
    FS --> FD[dao.FacultyDAO]
    CS --> CD[dao.CourseDAO]
    ES2 --> ED[dao.EnrollmentDAO]
    AS --> AD[dao.AttendanceDAO]
    RS --> RD[dao.ResultDAO]
    RP2 --> RD

    SD --> DBConn[db.DatabaseConnection]
    FD --> DBConn
    CD --> DBConn
    ED --> DBConn
    AD --> DBConn
    RD --> DBConn
    RP2 --> DBConn

    DBConn --> JDBC[(MySQL JDBC Driver)]
    JDBC --> MySQL[(MySQL Server 8.4)]
```

---

## 3. Entity-Relationship (ER) Diagram — Database Schema

```mermaid
erDiagram
    students ||--o{ enrollments : "has"
    faculty ||--o{ courses : "assigns (SET NULL)"
    courses ||--o{ enrollments : "has"
    enrollments ||--|{ attendance : "has"
    enrollments ||--|{ results : "has (1:1)"

    students {
        int id PK
        varchar student_id UK
        varchar roll_number UK
        varchar full_name
        varchar email UK
        varchar phone
        text address
        date date_of_birth
        varchar department
        tinyint semester
        date admission_date
    }

    faculty {
        int id PK
        varchar faculty_id UK
        varchar employee_id UK
        varchar full_name
        varchar email UK
        varchar phone
        text address
        date date_of_birth
        varchar department
        varchar designation
        varchar specialization
    }

    courses {
        int id PK
        varchar course_code UK
        varchar course_name
        varchar department
        tinyint semester
        int credits
        decimal maximum_marks
        decimal passing_marks
        int assigned_faculty_id FK
    }

    enrollments {
        int id PK
        int student_id FK
        int course_id FK
        date enrollment_date
        enum status Active/Completed/Dropped
    }

    attendance {
        int id PK
        int enrollment_id FK
        date class_date
        enum status PRESENT/ABSENT/LATE
    }

    results {
        int id PK
        int enrollment_id FK UK
        decimal marks_obtained
        decimal percentage
        char grade
    }

    students ||--o{ results : "has (via enrollment)"
    courses ||--o{ results : "has (via enrollment)"
```

**Foreign-Key Deletion Policy:**
| Relationship | FK Action | Reason |
|-------------|-----------|--------|
| students → enrollments | `RESTRICT` | Protect academic history |
| courses → enrollments | `RESTRICT` | Protect academic history |
| enrollments → attendance | `RESTRICT` | Prevent accidental history loss |
| enrollments → results | `RESTRICT` | One final result per enrollment |
| faculty → courses | `SET NULL` | Deliberate exception: courses remain unassigned when faculty leaves |

---

## 4. OOP Class Diagram (UML)

```mermaid
classDiagram
    class Person {
        <<abstract>>
        - int id
        - String fullName
        - String email
        - String phone
        - String address
        - LocalDate dateOfBirth
        + Person(int, String, String, String, String, LocalDate)
        + abstract String getRole()
        + abstract String getProfileSummary()
        + setters/getters with validation
    }

    class Student {
        - String studentId
        - String rollNumber
        - String department
        - int semester
        - LocalDate admissionDate
        + String getRole() "Student"
        + String getProfileSummary()
    }

    class Faculty {
        - String facultyId
        - String employeeId
        - String department
        - String designation
        - String specialization
        + String getRole() "Faculty"
        + String getProfileSummary()
    }

    class Course {
        - int id
        - String courseCode
        - String courseName
        - String department
        - int semester
        - int credits
        - BigDecimal maximumMarks
        - BigDecimal passingMarks
        - Integer assignedFacultyId
        + boolean isPassing(BigDecimal marks)
    }

    class Enrollment {
        - int id
        - int studentId
        - int courseId
        - LocalDate enrollmentDate
        - String status
    }

    class Attendance {
        - int id
        - int enrollmentId
        - LocalDate classDate
        - String status PRESENT/ABSENT/LATE
        + boolean isPresent()
        + boolean isLate()
        + boolean isAttended() "PRESENT or LATE"
    }

    class Result {
        - int id
        - int enrollmentId
        - BigDecimal marksObtained
        - BigDecimal percentage
        - String grade
    }

    class GradeService {
        + String calculateGrade(BigDecimal percentage)
        + static BigDecimal passingThreshold()
    }

    class DatabaseConnection {
        + static Connection getConnection()
        + static void close()
    }

    class StudentDAO {
        + List<Student> findAll()
        + List<Student> search(String)
        + Student findById(int)
        + int insert(Student)
        + void update(Student)
        + void delete(int)
    }

    Person <|-- Student
    Person <|-- Faculty
    Student "1" -- "0..*" Enrollment : enrolls in
    Course "1" -- "0..*" Enrollment : has
    Enrollment "1" -- "0..1" Attendance : has
    Enrollment "1" -- "0..1" Result : has
    Course "1" -- "0..*" Result : via enrollment
    Student "1" -- "0..*" Result : via enrollment
    GradeService ..> Result : computes grade
    DAO ..> DatabaseConnection : obtains connection
```

**OOP Concept Mapping:**
| Concept | Implementation |
|---------|---------------|
| **Encapsulation** | All model fields `private`; validated getters/setters (`Student.setSemester`, `Course.setPassingMarks`) |
| **Inheritance** | `Person` → `Student`, `Person` → `Faculty` |
| **Abstraction** | `Person` is `abstract`; DAO/Service hide SQL & business rules |
| **Polymorphism** | `Student.getRole()` returns `"Student"`, `Faculty.getRole()` returns `"Faculty"`; `Person ref = new Student()` |
| **Classes & Objects** | 7 domain model objects instantiated from UI input and database `ResultSet` |

---

## 5. Workflow / Activity Diagrams

### 5.1 Student CRUD Workflow

```mermaid
flowchart TD
    A[Open Students Panel] --> B{Operation?}
    B -->|Add| C[Fill Form: ID, Roll, Name, Email, etc.]
    B -->|Search| D[Enter keyword → table refresh]
    B -->|Edit| E[Select table row → load form]
    B -->|Delete| F[Select row → confirm dialog]

    C --> G[Service.validate uniqueness: ID/Roll/Email]
    G -->|Duplicate| H[Show Error Toast]
    G -->|Valid| I[DAO.insert → generated ID]
    I --> J[Show Success Toast + refresh table]

    D --> K[DAO.search LIKE %keyword%]
    K --> L[Populate JTable zebra rows]

    E --> M[Update form fields]
    M --> N[Service.validate against other rows]
    N -->|Duplicate| H
    N -->|Valid| O[DAO.update by id]
    O --> P[Show Success + clear form]

    F --> Q[DAO.delete → FK RESTRICT check]
    Q -->|Blocked| R[Show Constraint Error]
    Q -->|Allowed| S[Show Success + refresh]

    style A fill:#e1f5fe
    style H fill:#ffebee
    style J fill:#e8f5e9
    style R fill:#ffebee
```

### 5.2 Enrollment Workflow (Transactional)

```mermaid
flowchart TD
    A[Open Enrollments Panel] --> B[Load Students & Courses into ComboBoxes]
    B --> C[Select Student + Course + Date]
    C --> D[Click Enroll]
    D --> E[Service.enroll in transaction]
    E --> F{Student exists?}
    F -->|No| G[Error: Student not found]
    F -->|Yes| H{Course exists?}
    H -->|No| I[Error: Course not found]
    H -->|Yes| J{Active duplicate?}
    J -->|Yes| K[Error: Already enrolled Active]
    J -->|No| L[DAO.insert enrollment]
    L --> M[conn.commit]
    M --> N[Show Success + refresh table]
    L -->|SQLException| O[conn.rollback]
    O --> P[Show Error Toast]

    style M fill:#e8f5e9
    style O fill:#ffebee
    style G fill:#ffebee
    style I fill:#ffebee
    style K fill:#ffebee
```

### 5.3 Attendance Entry Workflow

```mermaid
flowchart TD
    A[Open Attendance Panel] --> B[Select Course + Class Date]
    B --> C[Validate: date not future]
    C -->|Future| D[Error: Future date not allowed]
    C -->|Valid| E[Load enrollments for course]
    E --> F[Pre-fill existing attendance for date]
    F --> G[Mark each student: PRESENT / ABSENT / LATE]
    G --> H[Click Save Attendance]
    H --> I[Service.save in transaction]
    I --> J[DELETE existing rows for course/date]
    J --> K[INSERT batch attendance records]
    K --> L[conn.commit]
    L --> M[Show Success Toast]
    I -->|Exception| N[conn.rollback]
    N --> O[Show Error Toast]

    percentage[Attendance % Rule<br/>(Present + Late) / Total × 100]
    I -.-> percentage
    style percentage fill:#fff3e0
```

**Attendance Rule:** `Attendance % = (Present + Late) / Total Recorded Sessions × 100`  
Late counts as attended. Future dates are never stored as absences.

---

### 5.4 Marks & Result Entry Workflow

```mermaid
flowchart TD
    A[Open Results Panel] --> B[Load Enrollments into ComboBox]
    B --> C[Select Enrollment + Enter Marks]
    C --> D[Click Save Result]
    D --> E[Service.validate enrollment exists]
    E -->|No| F[Error: Enrollment not found]
    E -->|Yes| G[Validate marks: 0 ≤ marks ≤ course.maximum_marks]
    G -->|Invalid| H[Error: Marks out of range]
    G -->|Valid| I[Compute percentage = marks/max × 100 BigDecimal]
    I --> J[GradeService.calculateGrade percentage]
    J --> K[DAO.save result → one per enrollment]
    K --> L[conn.commit]
    L --> M[Show Success: Percentage + Grade]
    K -->|Duplicate| N[Error: Result already exists]

    grade_scale[Grading Scale:<br/>A: 90-100 \| B: 80-89.99 \|<br/>C: 70-79.99 \| D: 60-69.99 \|<br/>E: 50-59.99 \| F: <50]
    J -.-> grade_scale
    style grade_scale fill:#f3e5f5
    style M fill:#e8f5e9
    style N fill:#ffebee
```

---

## 6. Sequence Diagrams

### 6.1 Add Student Sequence

```mermaid
sequenceDiagram
    actor User
    participant UI as StudentPanel (Swing)
    participant SVC as StudentService
    participant DAO as StudentDAO
    participant DB as MySQL

    User->>UI: Fill form + click Save
    UI->>SVC: create(id, roll, name, email, ...)
    SVC->>SVC: validate fields (InputValidator)
    SVC->>DAO: findByStudentId(id)
    DAO->>DB: SELECT ... WHERE student_id=?
    DB-->>DAO: row or null
    DAO-->>SVC: existing or null
    alt Duplicate
        SVC-->>UI: AppException DUPLICATE
        UI-->>User: Error toast
    else Unique
        SVC->>DAO: insert(student)
        DAO->>DB: INSERT ... RETURN_GENERATED_KEYS
        DB-->>DAO: generated id
        DAO-->>SVC: newId
        SVC-->>UI: newId
        UI-->>User: Success toast + table refresh
    end
```

### 6.2 Mark Attendance Sequence

```mermaid
sequenceDiagram
    actor User
    participant UI as AttendancePanel
    participant SVC as AttendanceService
    participant DAO as AttendanceDAO
    participant DB as MySQL

    User->>UI: Select course, date, mark PRESENT/ABSENT/LATE, click Save
    UI->>SVC: save(courseId, date, map<enrollmentId, status>)
    SVC->>SVC: validate date not future
    SVC->>DAO: save(courseId, date, map, conn)
    DAO->>DB: DELETE FROM attendance WHERE course_id=? AND class_date=?
    DAO->>DB: INSERT INTO attendance (enrollment_id, class_date, status) batch
    DAO-->>SVC: commit
    alt Success
        SVC-->>UI: true
        UI-->>User: Success toast
    else Failure
        SVC->>DB: rollback
        SVC-->>UI: AppException
        UI-->>User: Error toast
    end
```

### 6.3 Save Result Sequence

```mermaid
sequenceDiagram
    actor User
    participant UI as ResultPanel
    participant SVC as ResultService
    participant DAO as ResultDAO
    participant CDAO as CourseDAO
    participant GS as GradeService
    participant DB as MySQL

    User->>UI: Select enrollment, enter marks, click Save
    UI->>SVC: save(enrollmentId, marksStr)
    SVC->>DAO: exists(enrollmentId)
    DAO->>DB: SELECT COUNT(*) FROM enrollments WHERE id=?
    DB-->>DAO: boolean
    alt Not found
        DAO-->>SVC: false
        SVC-->>UI: AppException NOT_FOUND
    else Found
        SVC->>DAO: getCourseId(enrollmentId)
        DAO->>DB: SELECT course_id FROM enrollments WHERE id=?
        DB-->>DAO: courseId
        SVC->>CDAO: getMaximumMarks(courseId)
        CDAO->>DB: SELECT maximum_marks FROM courses WHERE id=?
        DB-->>CDAO: maxMarks
        SVC->>SVC: validate marks ≤ maxMarks
        SVC->>SVC: percentage = marks/max × 100 (BigDecimal)
        SVC->>GS: calculateGrade(percentage)
        GS-->>SVC: grade letter
        SVC->>DAO: save(result, conn)
        DAO->>DB: INSERT INTO results ... ON DUPLICATE KEY UPDATE
        DAO->>DB: commit
        DAO-->>SVC: resultId
        SVC-->>UI: resultId
        UI-->>User: Success toast with % and grade
    end
```

---

## 7. State Machine Diagram — Enrollment Status

```mermaid
stateDiagram-v2
    [*] --> Active : enroll()
    Active --> Completed : mark completed
    Active --> Dropped : drop / withdraw
    Completed --> [*] : archived
    Dropped --> [*] : archived

    note right of Active
        Only one Active enrollment
        per student-course pair
        (UNIQUE constraint)
    end note

    note right of Completed
        Results & attendance
        become historical
    end note
```

### Attendance Status States

```mermaid
stateDiagram-v2
    [*] --> PRESENT : mark present
    [*] --> ABSENT : mark absent
    [*] --> LATE : mark late
    PRESENT --> ABSENT : re-mark
    ABSENT --> LATE : re-mark
    LATE --> PRESENT : re-mark

    note right of PRESENT
        Counts as attended
    end note
    note right of LATE
        Counts as attended<br/>(student was in session)
    end note
    note right of ABSENT
        Reduces percentage
    end note
```

---

## 8. Data Flow Diagram (DFD) — Level 1

```mermaid
graph TD
    User((Academic Staff)) -->|Inputs| P1[Student Module]
    User -->|Inputs| P2[Faculty Module]
    User -->|Inputs| P3[Course Module]
    User -->|Inputs| P4[Enrollment Module]
    User -->|Inputs| P5[Attendance Module]
    User -->|Inputs| P6[Results Module]
    User -->|Requests| P7[Reports Module]

    P1 --> DS[(MySQL students)]
    P2 --> DS2[(MySQL faculty)]
    P3 --> DS3[(MySQL courses)]
    P4 --> DS4[(MySQL enrollments)]
    P5 --> DS5[(MySQL attendance)]
    P6 --> DS6[(MySQL results)]

    DS --> P7
    DS2 --> P7
    DS3 --> P7
    DS4 --> P7
    DS5 --> P7
    DS6 --> P7

    P7 -->|Display| User
    P1 -->|Display| User
    P2 -->|Display| User
    P3 -->|Display| User
    P4 -->|Display| User
    P5 -->|Display| User
    P6 -->|Display| User
```

---

## 9. Deployment / Run Diagram

```mermaid
graph TD
    User((Academic Staff)) -->|Runs| App[java -cp build;lib/mysql-connector-j-8.3.0.jar app.Main]
    App -->|JDBC| MySQL[(MySQL Server 8.4<br/>localhost:3306)]
    MySQL -->|stores| DB[(student_academic_management DB)]
    App -->|reads| Config[config/db.properties<br/>db.url, db.user, db.password]
    
    subgraph Laptop["Local Laptop (Windows)"]
        JDK[JDK 21]
        App
        Config
    end
    
    subgraph External["External Dependency"]
        MySQL
        DriverJAR[mysql-connector-j-8.3.0.jar]
    end
    
    JDK --> App
    DriverJAR --> App
```

---

## 10. Dashboard Layout (Mock)

```
┌─────────────────────────────────────────────────────────────┐
│ Dashboard                          ● Connected to MySQL  [⟳] │
├─────────────┬─────────────┬─────────────┬───────────────────┤
│ Total Students │ Total Faculty │ Total Courses │ Avg Attendance │
│     1,234       │      56       │      28       │     87.50%     │
│   ● navy        │    ● navy-dark │    ● indigo   │     ● blue     │
├─────────────┴─────────────┴─────────────┴───────────────────┤
│ Recently Added Students           Recent Enrollments        │
│ ┌─────┬──────┬────────┬─────────┬────────┐ ┌─────┬──────┐  │
│ │ ID  │ Roll │ Name   │ Email   │ Dept   │ │ ID  │ Student│  │
│ └─────┴──────┴────────┴─────────┴────────┘ └─────┴──────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## 11. Project Directory Structure

```
Student-Academic-and-cource-management/
├── src/
│  ├── app/Main.java
│  ├── model/{Person,Student,Faculty,Course,Enrollment,Attendance,Result}.java
│  ├── dao/{Student,Faculty,Course,Enrollment,Attendance,Result}DAO.java
│  ├── service/{Student,Faculty,Course,Enrollment,Attendance,Result,Grade,Report}Service.java
│  ├── db/DatabaseConnection.java
│  ├── ui/{MainFrame,Dashboard,Student,Faculty,Course,Enrollment,Attendance,Result,Reports}Panel.java
│  ├── ui/components/{RoundedPanel,RoundedButton,FormField,StatCard,SidebarItem,ToastPanel,EmptyState,StatusBadge,TableRenderer}.java
│  └── util/{UIStyles,InputValidator,DateUtils,AppException}.java
├── lib/mysql-connector-j-8.3.0.jar
├── sql/schema.sql
├── sql/sample-data.sql
├── config/db.properties.example
├── README.md
├── TESTING.md
└── RUN_LOCALLY.bat
```

---

## 12. How to View These Diagrams

1. **Copy any `mermaid` code block** above.
2. Paste it into:
   - A `.md` file on GitHub / GitLab (renders natively)
   - [mermaid.live](https://mermaid.live) (standalone editor)
   - Obsidian, Notion, or draw.io (Mermaid import)
   - VS Code with the Mermaid Preview extension

---

*Generated for the Student Academic & Course Management System — JDK 21 + MySQL + JDBC + Java Swing*
