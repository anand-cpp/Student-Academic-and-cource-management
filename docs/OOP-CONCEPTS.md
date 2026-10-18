# OOP Concepts Demonstration

This document maps the Object-Oriented Programming concepts demonstrated in the Student Academic and Course Management System to specific source files.

## 1. Encapsulation

**Definition**: Bundling data with methods that operate on that data, restricting direct access to internal state.

### Implementation Examples

#### Domain Models (Private Fields + Controlled Access)
```java
// backend/src/main/java/com/studentmgmt/model/Student.java
@Entity
public class Student extends BaseEntity {
    @Column(name = "student_id", nullable = false, unique = true, length = 50)
    private String studentId;                    // Private field

    @Column(name = "current_semester", nullable = false)
    private Integer currentSemester = 1;         // Private with default

    // Controlled access via getters/setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    // Business logic encapsulation
    public boolean isActive() {
        return status == StudentStatus.ACTIVE;   // Encapsulated logic
    }
}
```

#### Service Layer (Encapsulated Business Logic)
```java
// backend/src/main/java/com/studentmgmt/service/StudentServiceImpl.java
@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    // Dependencies injected, not exposed

    @Transactional
    public StudentDTO create(StudentCreateDTO createDTO) {
        // Validation encapsulated
        if (studentRepository.existsByStudentId(createDTO.getStudentId())) {
            throw new IllegalArgumentException("Student ID already exists");
        }
        // ... creation logic encapsulated
    }
}
```

**Files**: `Student.java`, `Faculty.java`, `Course.java`, `StudentServiceImpl.java`, `EnrollmentServiceImpl.java`

---

## 2. Inheritance

**Definition**: Mechanism where a new class derives properties and behaviors from an existing class.

### Implementation Examples

#### Base Entity (Shared Identity & Auditing)
```java
// backend/src/main/java/com/studentmgmt/model/BaseEntity.java
@MappedSuperclass
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

#### Entity Inheritance
```java
// backend/src/main/java/com/studentmgmt/model/Student.java
@Entity
public class Student extends BaseEntity {  // Inherits id, createdAt, updatedAt
    // Student-specific fields
}

// backend/src/main/java/com/studentmgmt/model/Faculty.java
@Entity
public class Faculty extends BaseEntity {  // Inherits id, createdAt, updatedAt
    // Faculty-specific fields
}
```

#### User-Role Relationship (Composition over Inheritance)
```java
// backend/src/main/java/com/studentmgmt/model/User.java
@Entity
public class User extends BaseEntity {
    // Shared user properties

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;  // Composition: User HAS-A Role
}
```

#### Repository Inheritance
```java
// backend/src/main/java/com/studentmgmt/repository/StudentRepository.java
public interface StudentRepository extends JpaRepository<Student, Long> {
    // Inherits: save, findById, findAll, delete, count, exists...
    Optional<Student> findByStudentId(String studentId);
    Page<Student> findByDepartmentId(Long departmentId, Pageable pageable);
}
```

**Files**: `BaseEntity.java`, `Student.java`, `Faculty.java`, `Course.java`, `Enrollment.java`, `User.java`, all Repository interfaces

---

## 3. Abstraction

**Definition**: Hiding complex implementation details and showing only essential features.

### Implementation Examples

#### Repository Abstraction (Interface Segregation)
```java
// backend/src/main/java/com/studentmgmt/repository/StudentRepository.java
public interface StudentRepository extends JpaRepository<Student, Long> {
    // Abstract query methods - implementation hidden
    Optional<Student> findByStudentId(String studentId);
    Optional<Student> findByUserId(Long userId);
    Page<Student> searchStudents(Long departmentId, StudentStatus status, String search, Pageable pageable);
    long countByDepartmentId(Long departmentId);
    long countByStatus(StudentStatus status);
    boolean existsByStudentId(String studentId);
    boolean existsByUserId(Long userId);
}
```

#### Service Abstraction (Contract-First Design)
```java
// backend/src/main/java/com/studentmgmt/service/StudentService.java
public interface StudentService {
    Optional<StudentDTO> findById(Long id);
    Optional<StudentDTO> findByStudentId(String studentId);
    Page<StudentDTO> findAll(Pageable pageable);
    Page<StudentDTO> search(Long departmentId, StudentStatus status, String search, Pageable pageable);
    StudentDTO create(StudentCreateDTO createDTO);
    StudentDTO update(Long id, StudentUpdateDTO updateDTO);
    void delete(Long id);
    void changeStatus(Long id, StudentStatus status);
    // Complex operations abstracted
    StudentDetailDTO getStudentDetail(Long id);
    List<EnrollmentDTO> getEnrollments(Long studentId);
    AttendanceSummaryDTO getAttendanceSummary(Long studentId);
    AcademicSummaryDTO getAcademicSummary(Long studentId);
}
```

#### DTO Abstraction (API Contract)
```java
// backend/src/main/java/com/studentmgmt/dto/StudentDTO.java
public class StudentDTO {
    private Long id;
    private String studentId;
    private String username;
    private String email;
    private String fullName;
    // ... computed fields abstracted
    private Double attendancePercentage;    // Calculated, not stored
    private Double overallPercentage;       // Computed from marks
    private String overallGrade;            // Derived from percentage
}
```

#### Abstract Factory Pattern (DTOMapper)
```java
// backend/src/main/java/com/studentmgmt/util/DTOMapper.java
@Component
public class DTOMapper {
    // Single abstraction for all entity↔DTO conversions
    public StudentDTO toStudentDTO(Student student) { ... }
    public FacultyDTO toFacultyDTO(Faculty faculty) { ... }
    public CourseDTO toCourseDTO(Course course) { ... }
    public EnrollmentDTO toEnrollmentDTO(Enrollment enrollment) { ... }
    // ... 20+ conversion methods
}
```

**Files**: `StudentRepository.java`, `StudentService.java`, `StudentDTO.java`, `DTOMapper.java`, all Repository and Service interfaces

---

## 4. Polymorphism

**Definition**: Ability to treat objects of different classes uniformly through a common interface.

### Implementation Examples

#### Runtime Polymorphism (Method Overriding)
```java
// backend/src/main/java/com/studentmgmt/model/AttendanceStatus.java
public enum AttendanceStatus {
    PRESENT("PRESENT"), ABSENT("ABSENT"), LATE("LATE"), EXCUSED("EXCUSED"), MEDICAL("MEDICAL");

    public boolean countsAsPresent() {
        return this == PRESENT || this == LATE;  // Polymorphic behavior
    }

    public boolean countsAsAbsent() {
        return this == ABSENT;
    }

    public boolean isExcused() {
        return this == EXCUSED || this == MEDICAL;
    }
}
```

#### Strategy Pattern (Grading Policies)
```java
// backend/src/main/java/com/studentmgmt/model/GradingPolicy.java
@Entity
public class GradingPolicy extends BaseEntity {
    @Column(name = "grade_boundaries", columnDefinition = "JSON", nullable = false)
    private JsonNode gradeBoundaries;  // JSON strategy

    // Polymorphic grade calculation
    public String getGradeForPercentage(double percentage) {
        if (gradeBoundaries == null || gradeBoundaries.isEmpty()) return "F";
        for (String grade : Collections.list(gradeBoundaries.fieldNames())) {
            double threshold = gradeBoundaries.get(grade).asDouble();
            if (percentage >= threshold) return grade;  // Polymorphic lookup
        }
        return "F";
    }
}

// backend/src/main/java/com/studentmgmt/service/MarksServiceImpl.java
@Service
public class MarksServiceImpl implements MarksService {
    public CourseResultDTO getCourseResult(Long studentId, Long courseId) {
        List<Marks> marks = marksRepository.findFinalizedByCourseIdAndStudentId(courseId, studentId);

        // Polymorphic grade calculation via GradingPolicy
        GradingPolicy policy = getEffectiveGradingPolicy(courseId);
        String grade = policy.getGradeForPercentage(percentage);  // Polymorphic call
    }
}
```

#### Repository Polymorphism (JPA Interface)
```java
// JpaRepository provides polymorphic CRUD
public interface JpaRepository<T, ID> extends PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {
    <S extends T> List<S> saveAll(Iterable<S> entities);
    Optional<T> findById(ID id);
    boolean existsById(ID id);
    // Polymorphic: works with Student, Faculty, Course, etc.
}
```

#### Controller Polymorphism (Generic Error Handling)
```java
// backend/src/main/java/com/studentmgmt/exception/GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        // Handles ResourceNotFoundException polymorphically for all resources
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Polymorphic validation error handling
    }
}
```

#### Service Polymorphism (Common Interface)
```java
// All services implement common CRUD interface pattern
public interface StudentService { ... }
public interface FacultyService { ... }
public interface CourseService { ... }
public interface EnrollmentService { ... }
public interface AttendanceService { ... }
public interface AssessmentService { ... }
public interface MarksService { ... }
public interface ReportService { ... }
// Uniform interface contracts across domain services
```

**Files**: `AttendanceStatus.java`, `GradingPolicy.java`, `MarksServiceImpl.java`, all Repository interfaces, `GlobalExceptionHandler.java`, all Service interfaces

---

## 5. Interfaces

**Definition**: Contracts that define behavior without specifying implementation.

### Implementation Examples

#### Repository Interfaces (Data Access Contracts)
```java
// backend/src/main/java/com/studentmgmt/repository/StudentRepository.java
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);
    Optional<Student> findByUserId(Long userId);
    List<Student> findByDepartmentId(Long departmentId);
    Page<Student> searchStudents(Long departmentId, StudentStatus status, String search, Pageable pageable);
    boolean existsByStudentId(String studentId);
    boolean existsByUserId(Long userId);
}

// backend/src/main/java/com/studentmgmt/repository/EnrollmentRepository.java
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Enrollment> findByStudentId(Long studentId);
    Page<Enrollment> searchEnrollments(Long studentId, Long courseId, EnrollmentStatus status, Pageable pageable);
    List<Enrollment> findActiveEnrollmentsByStudentId(Long studentId);
    long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status);
}
```

#### Service Interfaces (Business Logic Contracts)
```java
// backend/src/main/java/com/studentmgmt/service/StudentService.java
public interface StudentService {
    Optional<StudentDTO> findById(Long id);
    Page<StudentDTO> search(Long departmentId, StudentStatus status, String search, Pageable pageable);
    StudentDTO create(StudentCreateDTO createDTO);
    StudentDTO update(Long id, StudentUpdateDTO updateDTO);
    void delete(Long id);
    void changeStatus(Long id, StudentStatus status);
    StudentDetailDTO getStudentDetail(Long id);
}

// backend/src/main/java/com/studentmgmt/service/EnrollmentService.java
public interface EnrollmentService {
    Optional<EnrollmentDTO> findById(Long id);
    Page<EnrollmentDTO> search(Long studentId, Long courseId, EnrollmentStatus status, Pageable pageable);
    EnrollmentDTO create(EnrollmentCreateDTO createDTO);
    EnrollmentDTO update(Long id, EnrollmentUpdateDTO updateDTO);
    void delete(Long id);
    void changeStatus(Long id, EnrollmentStatus status);
    List<EnrollmentDTO> findActiveByStudentId(Long studentId);
}
```

#### Controller Interfaces (REST Contracts)
```java
// Implicit via Spring MVC annotations
@RestController
@RequestMapping("/api/students")
public class StudentController {
    @GetMapping
    public ResponseEntity<Page<StudentDTO>> getAllStudents(...) { ... }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentCreateDTO createDTO) { ... }
}
```

#### DTO Interfaces (API Contracts)
```java
// backend/src/main/java/com/studentmgmt/dto/StudentCreateDTO.java
public class StudentCreateDTO {
    @NotBlank @Size(max = 50) private String studentId;
    @NotBlank @Size(min = 3, max = 100) private String username;
    @NotBlank @Email @Size(max = 255) private String email;
    @NotBlank @Size(min = 8, max = 100) private String password;
    @NotBlank @Size(max = 255) private String fullName;
    @NotNull private Long departmentId;
    @Min(1900) @Max(2100) private Integer admissionYear;
    // Validation annotations define contract
}
```

**Files**: All Repository interfaces, all Service interfaces, all Controller classes, all DTO classes

---

## 6. Composition

**Definition**: Building complex objects by combining simpler objects (has-a relationship).

### Implementation Examples

#### Entity Composition (Object Relationships)
```java
// backend/src/main/java/com/studentmgmt/model/Student.java
@Entity
public class Student extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;                    // Student HAS-A User (composition)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;        // Student BELONGS-TO Department

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();  // Student HAS-MANY Enrollments
}

// backend/src/main/java/com/studentmgmt/model/Course.java
@Entity
public class Course extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;        // Course BELONGS-TO Department

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;              // Course TAUGHT-BY Faculty (optional)

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Enrollment> enrollments = new ArrayList<>();  // Course HAS-MANY Enrollments
}
```

#### DTO Composition (API Response Assembly)
```java
// backend/src/main/java/com/studentmgmt/dto/StudentDetailDTO.java
public class StudentDetailDTO extends StudentDTO {
    private List<EnrollmentDTO> enrollments;           // Composed
    private AttendanceSummaryDTO attendanceSummary;    // Composed
    private AcademicSummaryDTO academicSummary;        // Composed
}

// backend/src/main/java/com/studentmgmt/util/DTOMapper.java
public StudentDetailDTO toStudentDetailDTO(Student student,
                                           List<EnrollmentDTO> enrollments,
                                           AttendanceSummaryDTO attendanceSummary,
                                           AcademicSummaryDTO academicSummary) {
    // Composes multiple DTOs into single response
    return StudentDetailDTO.builder()
            .enrollments(enrollments)
            .attendanceSummary(attendanceSummary)
            .academicSummary(academicSummary)
            .build();
}
```

#### Service Composition (Dependency Injection)
```java
// backend/src/main/java/com/studentmgmt/service/StudentServiceImpl.java
@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final DTOMapper dtoMapper;  // Composed services

    // All dependencies injected, not inherited
}
```

**Files**: `Student.java`, `Course.java`, `Enrollment.java`, `StudentDetailDTO.java`, `DTOMapper.java`, all Service implementations

---

## Summary: OOP Concepts in Source Files

| OOP Concept | Key Files | Lines/Classes |
|-------------|-----------|---------------|
| **Encapsulation** | `Student.java`, `Faculty.java`, `Course.java`, `StudentServiceImpl.java`, `EnrollmentServiceImpl.java` | 15+ classes with private fields + controlled access |
| **Inheritance** | `BaseEntity.java`, `Student.java`, `Faculty.java`, `Course.java`, all Repository interfaces | 8+ classes extending `BaseEntity`, 15+ repositories extending `JpaRepository` |
| **Abstraction** | `StudentRepository.java`, `StudentService.java`, `StudentDTO.java`, `DTOMapper.java`, all Repository/Service interfaces | 15+ Repository interfaces, 10+ Service interfaces, 20+ DTO classes |
| **Polymorphism** | `AttendanceStatus.java`, `GradingPolicy.java`, `MarksServiceImpl.java`, `GlobalExceptionHandler.java`, `JpaRepository` | 5 enums with polymorphic methods, strategy pattern, exception hierarchy |
| **Interfaces** | All Repository interfaces (15+), all Service interfaces (10+), all Controller classes, all DTO classes | 25+ interface definitions |
| **Composition** | `Student.java`, `Course.java`, `Enrollment.java`, `StudentDetailDTO.java`, `DTOMapper.java`, all Service implementations | 20+ has-a relationships, 5+ composed DTOs |

## Design Patterns Used

| Pattern | Implementation | Location |
|---------|---------------|----------|
| **Template Method** | `JpaRepository` base methods | Spring Data JPA |
| **Strategy** | `GradingPolicy` grade calculation | `GradingPolicy.java`, `MarksServiceImpl.java` |
| **Factory** | `DTOMapper` entity↔DTO conversion | `DTOMapper.java` |
| **Repository** | `JpaRepository` extensions | All Repository interfaces |
| **DTO** | Request/Response contracts | All DTO classes in `dto/` |
| **Dependency Injection** | Constructor injection in Services | All `@Service` classes |
| **Exception Handling** | `@RestControllerAdvice` | `GlobalExceptionHandler.java` |
| **Builder** | Lombok `@Builder` on DTOs/Entities | All `@Builder` annotated classes |

## Code Quality Metrics

| Metric | Value |
|--------|-------|
| **Classes demonstrating Encapsulation** | 15+ |
| **Classes using Inheritance** | 12+ |
| **Abstraction Layers** | 4 (Controller → Service → Repository → DB) |
| **Polymorphic Methods** | 15+ |
| **Interfaces Defined** | 25+ |
| **Composition Relationships** | 20+ |
| **Design Patterns Applied** | 8 |
| **Cyclomatic Complexity** | Low (service methods < 20 lines) |
| **Coupling** | Low (interface-based) |
| **Cohesion** | High (single responsibility) |