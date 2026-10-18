# Testing Documentation

## Overview

The Student Academic and Course Management System implements a comprehensive testing strategy covering unit tests, integration tests, and end-to-end tests.

## Test Pyramid

```
        ┌─────────────┐
        │   E2E Tests │  ← Few, high confidence
        ├─────────────┤
        │Integration   │  ← Moderate count
        │  Tests       │
        ├─────────────┤
        │  Unit Tests  │  ← Many, fast, isolated
        └─────────────┘
```

## Backend Testing (Spring Boot)

### Test Dependencies
```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>1.20.1</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>mysql</artifactId>
        <version>1.20.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Unit Tests

#### Service Layer Tests
```java
// backend/src/test/java/com/studentmgmt/service/StudentServiceImplTest.java
@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private DTOMapper dtoMapper;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    void create_ShouldCreateStudent_WhenValidData() {
        // Given
        StudentCreateDTO createDTO = createValidStudentDTO();
        Department department = createDepartment();
        Role studentRole = createStudentRole();
        User savedUser = createSavedUser();
        Student savedStudent = createSavedStudent();

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(roleRepository.findByUserRole(UserRole.STUDENT)).thenReturn(Optional.of(studentRole));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);
        when(dtoMapper.toStudentDTO(savedStudent)).thenReturn(createStudentDTO());

        // When
        StudentDTO result = studentService.create(createDTO);

        // Then
        assertNotNull(result);
        assertEquals("STU2023001", result.getStudentId());
        assertEquals("Alice Johnson", result.getFullName());
        verify(userRepository).save(any(User.class));
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void create_ShouldThrowException_WhenStudentIdExists() {
        // Given
        StudentCreateDTO createDTO = createValidStudentDTO();
        when(studentRepository.existsByStudentId("STU2023001")).thenReturn(true);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> studentService.create(createDTO));
    }

    @Test
    void update_ShouldUpdateStudent_WhenValidData() {
        // Given
        Student student = createExistingStudent();
        StudentUpdateDTO updateDTO = createUpdateDTO();
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(student.getUser());
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(dtoMapper.toStudentDTO(student)).thenReturn(createStudentDTO());

        // When
        StudentDTO result = studentService.update(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
        verify(studentRepository).save(any(Student.class));
    }
}
```

#### Domain Model Tests
```java
// backend/src/test/java/com/studentmgmt/model/StudentTest.java
class StudentTest {

    @Test
    void isActive_ShouldReturnTrue_WhenStatusIsActive() {
        Student student = Student.builder()
                .status(StudentStatus.ACTIVE)
                .build();

        assertTrue(student.isActive());
    }

    @Test
    void isActive_ShouldReturnFalse_WhenStatusIsInactive() {
        Student student = Student.builder()
                .status(StudentStatus.INACTIVE)
                .build();

        assertFalse(student.isActive());
    }

    @Test
    void getFullName_ShouldReturnUserFullName() {
        User user = User.builder().fullName("Alice Johnson").build();
        Student student = Student.builder().user(user).build();

        assertEquals("Alice Johnson", student.getFullName());
    }
}
```

#### Enum Logic Tests
```java
// backend/src/test/java/com/studentmgmt/model/AttendanceStatusTest.java
class AttendanceStatusTest {

    @ParameterizedTest
    @EnumSource(value = AttendanceStatus.class, names = {"PRESENT", "LATE"})
    void countsAsPresent_ShouldReturnTrue_ForPresentAndLate(AttendanceStatus status) {
        assertTrue(status.countsAsPresent());
    }

    @ParameterizedTest
    @EnumSource(value = AttendanceStatus.class, names = {"ABSENT"})
    void countsAsPresent_ShouldReturnFalse_ForAbsent(AttendanceStatus status) {
        assertFalse(status.countsAsPresent());
    }

    @Test
    void isExcused_ShouldReturnTrue_ForExcusedAndMedical() {
        assertTrue(AttendanceStatus.EXCUSED.isExcused());
        assertTrue(AttendanceStatus.MEDICAL.isExcused());
    }
}
```

#### Grading Policy Tests
```java
// backend/src/test/java/com/studentmgmt/model/GradingPolicyTest.java
class GradingPolicyTest {

    @Test
    void getGradeForPercentage_ShouldReturnAPlus_For95Percent() {
        GradingPolicy policy = GradingPolicy.builder()
                .gradeBoundaries(createGradeBoundaries())
                .build();

        assertEquals("A+", policy.getGradeForPercentage(95.0));
    }

    @Test
    void getGradeForPercentage_ShouldReturnF_For30Percent() {
        GradingPolicy policy = GradingPolicy.builder()
                .gradeBoundaries(createGradeBoundaries())
                .build();

        assertEquals("F", policy.getGradeForPercentage(30.0));
    }
}
```

### Integration Tests

#### Repository Tests (Testcontainers)
```java
// backend/src/test/java/com/studentmgmt/repository/StudentRepositoryIntegrationTest.java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class StudentRepositoryIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("student_academic_test_db")
            .withUsername("root")
            .withPassword("test");

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        // Clean database
        studentRepository.deleteAll();
        departmentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByStudentId_ShouldReturnStudent_WhenExists() {
        // Given
        Department dept = departmentRepository.save(createDepartment());
        Role role = roleRepository.save(createStudentRole());
        User user = userRepository.save(createUser(role));
        Student student = studentRepository.save(createStudent(user, dept));

        // When
        Optional<Student> found = studentRepository.findByStudentId("STU2023001");

        // Then
        assertTrue(found.isPresent());
        assertEquals("Alice Johnson", found.get().getFullName());
    }

    @Test
    void searchStudents_ShouldFilterByDepartmentAndStatus() {
        // Given
        Department csDept = departmentRepository.save(createDepartment("CS", "Computer Science"));
        Department eeDept = departmentRepository.save(createDepartment("EE", "Electrical Engineering"));
        Role role = roleRepository.save(createStudentRole());

        Student student1 = studentRepository.save(createStudent(createUser(role), csDept));
        Student student2 = studentRepository.save(createStudent(createUser(role), eeDept));
        student2.setStatus(StudentStatus.GRADUATED);
        studentRepository.save(student2);

        // When
        Page<Student> csActive = studentRepository.searchStudents(csDept.getId(), StudentStatus.ACTIVE, null, Pageable.unpaged());

        // Then
        assertEquals(1, csActive.getTotalElements());
        assertEquals("STU2023001", csActive.getContent().get(0).getStudentId());
    }

    @Test
    void existsByStudentId_ShouldReturnTrue_WhenExists() {
        // Given
        Department dept = departmentRepository.save(createDepartment());
        Role role = roleRepository.save(createStudentRole());
        User user = userRepository.save(createUser(role));
        studentRepository.save(createStudent(user, dept));

        // When/Then
        assertTrue(studentRepository.existsByStudentId("STU2023001"));
        assertFalse(studentRepository.existsByStudentId("NONEXISTENT"));
    }
}
```

#### Service Integration Tests
```java
// backend/src/test/java/com/studentmgmt/service/EnrollmentServiceIntegrationTest.java
@SpringBootTest
@Testcontainers
@Transactional
class EnrollmentServiceIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("student_academic_test_db")
            .withUsername("root")
            .withPassword("test");

    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void create_ShouldEnrollStudent_WhenValid() {
        // Given
        Department dept = departmentRepository.save(createDepartment());
        Student student = studentRepository.save(createStudent(createUser(createStudentRole()), dept));
        Course course = courseRepository.save(createCourse(dept, createFaculty()));

        EnrollmentCreateDTO createDTO = EnrollmentCreateDTO.builder()
                .studentId(student.getId())
                .courseId(course.getId())
                .enrollmentDate(LocalDate.now())
                .build();

        // When
        EnrollmentDTO result = enrollmentService.create(createDTO);

        // Then
        assertNotNull(result);
        assertEquals(EnrollmentStatus.ENROLLED, result.getStatus());
        assertEquals(student.getStudentId(), result.getStudentCode());
    }

    @Test
    void create_ShouldThrowException_WhenAlreadyEnrolled() {
        // Given
        Department dept = departmentRepository.save(createDepartment());
        Student student = studentRepository.save(createStudent(createUser(createStudentRole()), dept));
        Course course = courseRepository.save(createCourse(dept, createFaculty()));

        // Enroll once
        EnrollmentCreateDTO createDTO = EnrollmentCreateDTO.builder()
                .studentId(student.getId())
                .courseId(course.getId())
                .build();
        enrollmentService.create(createDTO);

        // When/Then - Try to enroll again
        assertThrows(BusinessRuleViolationException.class, () -> enrollmentService.create(createDTO));
    }

    @Test
    void create_ShouldThrowException_WhenCourseFull() {
        // Given
        Department dept = departmentRepository.save(createDepartment());
        Course course = courseRepository.save(Course.builder()
                .courseCode("FULL101")
                .title("Full Course")
                .credits(3)
                .department(dept)
                .semester(1)
                .maxEnrollment(1)
                .status(CourseStatus.ACTIVE)
                .academicYear("2024-2025")
                .build());

        Student student1 = studentRepository.save(createStudent(createUser(createStudentRole()), dept));
        Student student2 = studentRepository.save(createStudent(createUser(createStudentRole()), dept));

        // Enroll first student
        enrollmentService.create(EnrollmentCreateDTO.builder()
                .studentId(student1.getId())
                .courseId(course.getId())
                .build());

        // When/Then - Second student should fail
        assertThrows(BusinessRuleViolationException.class, () ->
                enrollmentService.create(EnrollmentCreateDTO.builder()
                        .studentId(student2.getId())
                        .courseId(course.getId())
                        .build())
        );
    }
}
```

### Controller Tests (WebMvcTest)
```java
// backend/src/test/java/com/studentmgmt/controller/StudentControllerTest.java
@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllStudents_ShouldReturnPage_WhenAuthenticated() throws Exception {
        // Given
        Page<StudentDTO> page = new PageImpl<>(List.of(createStudentDTO()));
        when(studentService.search(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/students")
                        .with(csrf())
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].studentId").value("STU2023001"));
    }

    @Test
    void createStudent_ShouldReturnCreated_WhenValid() throws Exception {
        // Given
        StudentCreateDTO createDTO = createValidStudentDTO();
        when(studentService.create(any(StudentCreateDTO.class))).thenReturn(createStudentDTO());

        // When/Then
        mockMvc.perform(post("/api/students")
                        .with(csrf())
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId").value("STU2023001"));
    }

    @Test
    void createStudent_ShouldReturnBadRequest_WhenInvalid() throws Exception {
        // Given
        String invalidJson = "{\"studentId\": \"\"}";

        // When/Then
        mockMvc.perform(post("/api/students")
                        .with(csrf())
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
```

### Security Tests
```java
// backend/src/test/java/com/studentmgmt/security/SecurityConfigTest.java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicEndpoints_ShouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().isOk());  // Login page accessible
    }

    @Test
    void protectedEndpoints_ShouldRequireAuth() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void roleBasedAccess_ShouldWork() throws Exception {
        // Admin can access all
        mockMvc.perform(get("/api/students")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());

        // Student cannot access admin endpoints
        mockMvc.perform(get("/api/students")
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }
}
```

---

## Frontend Testing (React + TypeScript)

### Test Dependencies
```json
// frontend/package.json
{
  "devDependencies": {
    "@testing-library/react": "^14.0.0",
    "@testing-library/jest-dom": "^6.0.0",
    "@testing-library/user-event": "^14.0.0",
    "jest": "^29.0.0",
    "jest-environment-jsdom": "^29.0.0",
    "msw": "^2.0.0"
  }
}
```

### Component Tests
```tsx
// frontend/src/components/ui/Button.test.tsx
import { render, screen, fireEvent } from '@testing-library/react';
import { Button } from './Button';

describe('Button', () => {
    it('renders children correctly', () => {
        render(<Button>Click me</Button>);
        expect(screen.getByRole('button', { name: 'Click me' })).toBeInTheDocument();
    });

    it('shows loading spinner when loading', () => {
        render(<Button loading>Click me</Button>);
        expect(screen.getByRole('status')).toBeInTheDocument();
    });

    it('calls onClick when clicked', () => {
        const handleClick = jest.fn();
        render(<Button onClick={handleClick}>Click me</Button>);
        fireEvent.click(screen.getByRole('button'));
        expect(handleClick).toHaveBeenCalledTimes(1);
    });

    it('applies variant classes correctly', () => {
        render(<Button variant="danger">Delete</Button>);
        expect(screen.getByRole('button')).toHaveClass('bg-red-600');
    });

    it('applies size classes correctly', () => {
        render(<Button size="lg">Large</Button>);
        expect(screen.getByRole('button')).toHaveClass('px-6 py-3 text-base');
    });
});
```

### Hook Tests
```tsx
// frontend/src/hooks/useApi.test.ts
import { renderHook, act } from '@testing-library/react';
import { useApi } from './useApi';

describe('useApi', () => {
    it('fetches data on mount', async () => {
        const mockApiCall = jest.fn().mockResolvedValue({ data: 'test' });

        const { result } = renderHook(() => useApi(mockApiCall));

        expect(result.current.loading).toBe(true);

        await act(async () => {
            await new Promise(resolve => setTimeout(resolve, 0));
        });

        expect(result.current.loading).toBe(false);
        expect(result.current.data).toEqual({ data: 'test' });
    });

    it('handles errors correctly', async () => {
        const mockApiCall = jest.fn().mockRejectedValue(new Error('API Error'));

        const { result } = renderHook(() => useApi(mockApiCall));

        await act(async () => {
            await new Promise(resolve => setTimeout(resolve, 0));
        });

        expect(result.current.error).toBe('API Error');
    });
});
```

### Service Tests (MSW)
```tsx
// frontend/src/services/students.test.ts
import { studentsApi } from './students';
import { http, HttpResponse } from 'msw';
import { setupServer } from 'msw/node';

const server = setupServer(
    http.get('/api/students', () => {
        return HttpResponse.json({
            content: [
                { id: 1, studentId: 'STU001', fullName: 'Alice Johnson', email: 'alice@example.com' }
            ],
            totalElements: 1,
            totalPages: 1,
            page: 0,
            size: 20
        });
    }),
    http.post('/api/students', async ({ request }) => {
        const body = await request.json();
        return HttpResponse.json({ id: 2, ...body }, { status: 201 });
    })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('studentsApi', () => {
    it('fetches students with pagination', async () => {
        const result = await studentsApi.getAll({ page: 0, size: 10 });
        expect(result.data.content).toHaveLength(1);
        expect(result.data.content[0].studentId).toBe('STU001');
    });

    it('creates a new student', async () => {
        const newStudent = { studentId: 'STU002', fullName: 'Bob Smith', email: 'bob@example.com' };
        const result = await studentsApi.create(newStudent);
        expect(result.data.id).toBe(2);
        expect(result.data.fullName).toBe('Bob Smith');
    });
});
```

### E2E Tests (Playwright)
```typescript
// frontend/e2e/students.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Students Management', () => {
    test.beforeEach(async ({ page }) => {
        await page.goto('/login');
        await page.fill('input[name="usernameOrEmail"]', 'admin');
        await page.fill('input[name="password"]', 'password123');
        await page.click('button[type="submit"]');
        await expect(page).toHaveURL('/');
    });

    test('should display students list', async ({ page }) => {
        await page.goto('/students');
        await expect(page.locator('h1')).toContainText('Students');
        await expect(page.locator('table')).toBeVisible();
    });

    test('should create a new student', async ({ page }) => {
        await page.goto('/students');
        await page.click('text=Add Student');
        await page.fill('input[name="studentId"]', 'STU2024001');
        await page.fill('input[name="username"]', 'newstudent');
        await page.fill('input[name="email"]', 'new@test.com');
        await page.fill('input[name="password"]', 'password123');
        await page.fill('input[name="fullName"]', 'Test Student');
        await page.selectOption('select[name="departmentId"]', '1');
        await page.click('button[type="submit"]');

        await expect(page.locator('text=STU2024001')).toBeVisible();
    });
});
```

---

## Test Commands

### Backend
```bash
cd backend

# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=StudentServiceImplTest

# Run with coverage
./mvnw test jacoco:report

# Run integration tests only
./mvnw test -Dtest=*IntegrationTest

# Skip tests
./mvnw package -DskipTests
```

### Frontend
```bash
cd frontend

# Run unit tests
npm test

# Run with coverage
npm test -- --coverage

# Run in watch mode
npm test -- --watch

# Run E2E tests
npm run test:e2e

# Run E2E with UI
npm run test:e2e -- --ui
```

---

## Test Data Management

### Test Data Builders
```java
// backend/src/test/java/com/studentmgmt/test/TestDataBuilder.java
public class TestDataBuilder {

    public static User createUser(Role role) {
        return User.builder()
                .username("testuser")
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .fullName("Test User")
                .role(role)
                .isActive(true)
                .build();
    }

    public static Department createDepartment(String code, String name) {
        return Department.builder()
                .code(code)
                .name(name)
                .description("Test Department")
                .isActive(true)
                .build();
    }

    public static Student createStudent(User user, Department department) {
        return Student.builder()
                .studentId("STU" + System.currentTimeMillis())
                .user(user)
                .department(department)
                .admissionYear(2024)
                .currentSemester(1)
                .status(StudentStatus.ACTIVE)
                .build();
    }

    public static Course createCourse(Department department, Faculty faculty) {
        return Course.builder()
                .courseCode("CS" + System.currentTimeMillis())
                .title("Test Course")
                .credits(3)
                .department(department)
                .semester(1)
                .faculty(faculty)
                .maxEnrollment(30)
                .academicYear("2024-2025")
                .build();
    }
}
```

### Test Profiles
```yaml
# backend/src/main/resources/application-test.yml
spring:
  datasource:
    url: jdbc:tc:mysql:8.0:///student_academic_test_db
    username: root
    password: test
    driver-class-name: org.testcontainers.jdbc.ContainerDatabaseDriver
  sql:
    init:
      mode: always
      schema-locations: classpath:db/migration/schema.sql
      data-locations: classpath:db/migration/data.sql
```

---

## Continuous Integration

### GitHub Actions Workflow
```yaml
# .github/workflows/ci.yml
name: CI

on: [push, pull_request]

jobs:
  backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Build Backend
        run: cd backend && ./mvnw verify

  frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
      - name: Install & Test
        run: cd frontend && npm ci && npm test -- --coverage

  docker:
    needs: [backend, frontend]
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Build Backend Image
        run: cd backend && docker build -t student-mgmt-backend .
      - name: Build Frontend Image
        run: cd frontend && docker build -t student-mgmt-frontend .
```

---

## Quality Gates

### Coverage Thresholds
```xml
<!-- backend/pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.70</minimum>
                    </limit>
                    <limit>
                        <counter>BRANCH</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.60</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

### Frontend Coverage
```json
// frontend/jest.config.js
module.exports = {
    coverageThreshold: {
        global: {
            branches: 60,
            functions: 70,
            lines: 70,
            statements: 70
        }
    }
}
```

---

## Mutation Testing (Optional)
```bash
# Backend (PIT)
./mvnw org.pitest:pitest-maven:mutationCoverage

# Frontend (Stryker)
npx stryker run
```

---

## Performance Testing

### Backend Load Testing
```bash
# Using k6
k6 run --vus 50 --duration 30s load-test.js
```

```javascript
// load-test.js
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 50 },
        { duration: '1m', target: 100 },
        { duration: '30s', target: 0 }
    ]
};

export default function() {
    const res = http.get('http://localhost:8080/api/students', {
        headers: { 'Authorization': 'Bearer token' }
    });
    check(res, { 'status is 200': (r) => r.status === 200 });
    sleep(1);
}
```

---

## Test Reports

### Viewing Reports
```bash
# Backend
open backend/target/site/jacoco/index.html

# Frontend
open frontend/coverage/lcov-report/index.html
```

### CI Artifacts
- JUnit XML reports: `backend/target/surefire-reports/`
- Coverage: `backend/target/site/jacoco/`, `frontend/coverage/`
- E2E videos/screenshots: `frontend/test-results/`