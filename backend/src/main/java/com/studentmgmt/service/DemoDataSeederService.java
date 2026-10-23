package com.studentmgmt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Profile({"local", "dev", "test"})
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeederService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    /**
     * Safely, idempotently, and transactionally resets and seeds all demo data
     * for all 5 roles in an isolated development environment.
     */
    @Transactional
    public Map<String, Object> resetAndSeedDemoData() {
        log.info("Initiating safe transactional demo data reset and reseeding...");

        // 1. Break cyclic foreign key between departments and faculty
        jdbcTemplate.execute("UPDATE departments SET head_faculty_id = NULL");

        // 2. Clear records in strict dependency order
        jdbcTemplate.execute("DELETE FROM notifications");
        jdbcTemplate.execute("DELETE FROM audit_logs");
        jdbcTemplate.execute("DELETE FROM course_grading_policies");
        jdbcTemplate.execute("DELETE FROM marks");
        jdbcTemplate.execute("DELETE FROM assessments");
        jdbcTemplate.execute("DELETE FROM attendance_records");
        jdbcTemplate.execute("DELETE FROM attendance_sessions");
        jdbcTemplate.execute("DELETE FROM enrollments");
        jdbcTemplate.execute("DELETE FROM courses");
        jdbcTemplate.execute("DELETE FROM students");
        jdbcTemplate.execute("DELETE FROM faculty");
        jdbcTemplate.execute("DELETE FROM departments");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM grading_policies");
        jdbcTemplate.execute("DELETE FROM roles");

        // 3. Reset primary key identity generators (H2 compatible)
        resetIdentity("roles");
        resetIdentity("users");
        resetIdentity("departments");
        resetIdentity("faculty");
        resetIdentity("students");
        resetIdentity("courses");
        resetIdentity("enrollments");
        resetIdentity("attendance_sessions");
        resetIdentity("attendance_records");
        resetIdentity("assessments");
        resetIdentity("marks");
        resetIdentity("grading_policies");
        resetIdentity("notifications");
        resetIdentity("audit_logs");

        // 4. Seed Roles (IDs: 1 to 5)
        jdbcTemplate.update("INSERT INTO roles (id, name, description) VALUES (1, 'ADMIN', 'System administrator with full access')");
        jdbcTemplate.update("INSERT INTO roles (id, name, description) VALUES (2, 'FACULTY', 'Faculty member who teaches courses')");
        jdbcTemplate.update("INSERT INTO roles (id, name, description) VALUES (3, 'STUDENT', 'Student enrolled in courses')");
        jdbcTemplate.update("INSERT INTO roles (id, name, description) VALUES (4, 'DEPARTMENT_HEAD', 'Head of an academic department')");
        jdbcTemplate.update("INSERT INTO roles (id, name, description) VALUES (5, 'REGISTRAR', 'Registrar office staff managing enrollments')");
        syncIdentity("roles", 5);

        // 5. Seed Users with BCrypt hash for 'password123'
        String pwHash = passwordEncoder.encode("password123");

        // Admin (ID: 1)
        jdbcTemplate.update("INSERT INTO users (id, username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (1, ?, ?, ?, ?, 1, TRUE, TRUE)",
                "admin", "admin@university.edu", pwHash, "System Administrator");

        // Faculty (IDs: 2, 3, 4, 5)
        jdbcTemplate.update("INSERT INTO users (id, username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (2, ?, ?, ?, ?, 2, TRUE, TRUE)",
                "prof.smith", "smith@university.edu", pwHash, "Prof. John Smith");
        jdbcTemplate.update("INSERT INTO users (id, username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (3, ?, ?, ?, ?, 2, TRUE, TRUE)",
                "prof.johnson", "johnson@university.edu", pwHash, "Prof. Emily Johnson");
        jdbcTemplate.update("INSERT INTO users (id, username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (4, ?, ?, ?, ?, 2, TRUE, TRUE)",
                "prof.williams", "williams@university.edu", pwHash, "Prof. Michael Williams");
        jdbcTemplate.update("INSERT INTO users (id, username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (5, ?, ?, ?, ?, 2, TRUE, TRUE)",
                "prof.brown", "brown@university.edu", pwHash, "Prof. Sarah Brown");

        // Department Head (ID: 6)
        jdbcTemplate.update("INSERT INTO users (id, username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (6, ?, ?, ?, ?, 4, TRUE, TRUE)",
                "dept.head.cs", "cshead@university.edu", pwHash, "Dr. Robert Davis");

        // Registrar (ID: 7)
        jdbcTemplate.update("INSERT INTO users (id, username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (7, ?, ?, ?, ?, 5, TRUE, TRUE)",
                "registrar", "registrar@university.edu", pwHash, "Ms. Jennifer Wilson");

        // Students (IDs: 8, 9, 10, 11, 12)
        jdbcTemplate.update("INSERT INTO users (id, username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (8, ?, ?, ?, ?, 3, TRUE, TRUE)",
                "student001", "student001@university.edu", pwHash, "Alice Anderson");
        jdbcTemplate.update("INSERT INTO users (id, username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (9, ?, ?, ?, ?, 3, TRUE, TRUE)",
                "student002", "student002@university.edu", pwHash, "Bob Baker");
        jdbcTemplate.update("INSERT INTO users (id, username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (10, ?, ?, ?, ?, 3, TRUE, TRUE)",
                "student003", "student003@university.edu", pwHash, "Carol Clark");
        jdbcTemplate.update("INSERT INTO users (id, username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (11, ?, ?, ?, ?, 3, TRUE, TRUE)",
                "student004", "student004@university.edu", pwHash, "David Davis");
        jdbcTemplate.update("INSERT INTO users (id, username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (12, ?, ?, ?, ?, 3, TRUE, TRUE)",
                "student005", "student005@university.edu", pwHash, "Eva Evans");
        syncIdentity("users", 12);

        // 6. Seed Departments (IDs: 1 to 4)
        jdbcTemplate.update("INSERT INTO departments (id, code, name, description, is_active) VALUES (1, 'CS', 'Computer Science', 'Department of Computer Science and Engineering', TRUE)");
        jdbcTemplate.update("INSERT INTO departments (id, code, name, description, is_active) VALUES (2, 'MATH', 'Mathematics', 'Department of Mathematics and Statistics', TRUE)");
        jdbcTemplate.update("INSERT INTO departments (id, code, name, description, is_active) VALUES (3, 'PHYS', 'Physics', 'Department of Physics', TRUE)");
        jdbcTemplate.update("INSERT INTO departments (id, code, name, description, is_active) VALUES (4, 'ENG', 'English', 'Department of English Literature', TRUE)");
        syncIdentity("departments", 4);

        // 7. Seed Faculty Members (IDs: 1 to 4)
        jdbcTemplate.update("INSERT INTO faculty (id, faculty_id, user_id, department_id, designation, specialization, qualification, hire_date, status, office_location) VALUES (1, 'FAC001', 2, 1, 'PROFESSOR', 'Artificial Intelligence', 'Ph.D. Stanford', '2015-08-15', 'ACTIVE', 'CS-301')");
        jdbcTemplate.update("INSERT INTO faculty (id, faculty_id, user_id, department_id, designation, specialization, qualification, hire_date, status, office_location) VALUES (2, 'FAC002', 3, 1, 'ASSOCIATE_PROFESSOR', 'Data Structures', 'Ph.D. MIT', '2018-01-10', 'ACTIVE', 'CS-302')");
        jdbcTemplate.update("INSERT INTO faculty (id, faculty_id, user_id, department_id, designation, specialization, qualification, hire_date, status, office_location) VALUES (3, 'FAC003', 4, 2, 'PROFESSOR', 'Applied Mathematics', 'Ph.D. Princeton', '2012-09-01', 'ACTIVE', 'MATH-201')");
        jdbcTemplate.update("INSERT INTO faculty (id, faculty_id, user_id, department_id, designation, specialization, qualification, hire_date, status, office_location) VALUES (4, 'FAC004', 5, 3, 'ASSISTANT_PROFESSOR', 'Quantum Physics', 'Ph.D. Caltech', '2020-08-15', 'ACTIVE', 'PHYS-101')");
        syncIdentity("faculty", 4);

        // Set Head of Department for CS
        jdbcTemplate.update("UPDATE departments SET head_faculty_id = 1 WHERE id = 1");

        // 8. Seed Students (IDs: 1 to 5)
        jdbcTemplate.update("INSERT INTO students (id, student_id, user_id, department_id, admission_year, current_semester, status, gender, emergency_contact_name, emergency_contact_phone, blood_group) VALUES (1, 'CS2023001', 8, 1, 2023, 3, 'ACTIVE', 'FEMALE', 'Robert Anderson', '555-0101', 'O+')");
        jdbcTemplate.update("INSERT INTO students (id, student_id, user_id, department_id, admission_year, current_semester, status, gender, emergency_contact_name, emergency_contact_phone, blood_group) VALUES (2, 'CS2023002', 9, 1, 2023, 3, 'ACTIVE', 'MALE', 'Mary Baker', '555-0102', 'A+')");
        jdbcTemplate.update("INSERT INTO students (id, student_id, user_id, department_id, admission_year, current_semester, status, gender, emergency_contact_name, emergency_contact_phone, blood_group) VALUES (3, 'CS2022001', 10, 1, 2022, 5, 'ACTIVE', 'FEMALE', 'James Clark', '555-0103', 'B+')");
        jdbcTemplate.update("INSERT INTO students (id, student_id, user_id, department_id, admission_year, current_semester, status, gender, emergency_contact_name, emergency_contact_phone, blood_group) VALUES (4, 'MATH2023001', 11, 2, 2023, 3, 'ACTIVE', 'MALE', 'Susan Davis', '555-0104', 'AB+')");
        jdbcTemplate.update("INSERT INTO students (id, student_id, user_id, department_id, admission_year, current_semester, status, gender, emergency_contact_name, emergency_contact_phone, blood_group) VALUES (5, 'PHYS2023001', 12, 3, 2023, 3, 'ACTIVE', 'FEMALE', 'Thomas Evans', '555-0105', 'O-')");
        syncIdentity("students", 5);

        // 9. Seed Courses (IDs: 1 to 8)
        jdbcTemplate.update("INSERT INTO courses (id, course_code, title, description, credits, department_id, semester, faculty_id, max_enrollment, status, academic_year) VALUES (1, 'CS101', 'Introduction to Computer Science', 'Core programming fundamentals and algorithms', 3, 1, 1, 1, 60, 'ACTIVE', '2024-2025')");
        jdbcTemplate.update("INSERT INTO courses (id, course_code, title, description, credits, department_id, semester, faculty_id, max_enrollment, prerequisites, status, academic_year) VALUES (2, 'CS201', 'Data Structures and Algorithms', 'Advanced trees, graphs, and algorithmic complexity', 4, 1, 3, 1, 50, 'CS101', 'ACTIVE', '2024-2025')");
        jdbcTemplate.update("INSERT INTO courses (id, course_code, title, description, credits, department_id, semester, faculty_id, max_enrollment, prerequisites, status, academic_year) VALUES (3, 'CS301', 'Machine Learning', 'Supervised and unsupervised models, neural networks', 4, 1, 5, 2, 40, 'CS201, MATH201', 'ACTIVE', '2024-2025')");
        jdbcTemplate.update("INSERT INTO courses (id, course_code, title, description, credits, department_id, semester, faculty_id, max_enrollment, prerequisites, status, academic_year) VALUES (4, 'CS401', 'Artificial Intelligence', 'Knowledge representation, planning, heuristic search', 3, 1, 7, 1, 30, 'CS301', 'ACTIVE', '2024-2025')");
        jdbcTemplate.update("INSERT INTO courses (id, course_code, title, description, credits, department_id, semester, faculty_id, max_enrollment, status, academic_year) VALUES (5, 'MATH101', 'Calculus I', 'Differential and integral calculus with applications', 4, 2, 1, 3, 60, 'ACTIVE', '2024-2025')");
        jdbcTemplate.update("INSERT INTO courses (id, course_code, title, description, credits, department_id, semester, faculty_id, max_enrollment, prerequisites, status, academic_year) VALUES (6, 'MATH201', 'Linear Algebra', 'Vector spaces, matrices, eigenvalues and eigenvectors', 3, 2, 3, 3, 50, 'MATH101', 'ACTIVE', '2024-2025')");
        jdbcTemplate.update("INSERT INTO courses (id, course_code, title, description, credits, department_id, semester, faculty_id, max_enrollment, status, academic_year) VALUES (7, 'PHYS101', 'General Physics I', 'Classical mechanics, thermodynamics, wave motion', 4, 3, 1, 4, 50, 'ACTIVE', '2024-2025')");
        jdbcTemplate.update("INSERT INTO courses (id, course_code, title, description, credits, department_id, semester, max_enrollment, status, academic_year) VALUES (8, 'ENG101', 'English Composition', 'Rhetorical analysis and academic technical writing', 3, 4, 1, 40, 'ACTIVE', '2024-2025')");
        syncIdentity("courses", 8);

        // 10. Seed Enrollments (IDs: 1 to 15)
        jdbcTemplate.update("INSERT INTO enrollments (id, student_id, course_id, enrollment_date, status) VALUES (1, 1, 1, '2024-01-15', 'ENROLLED')");
        jdbcTemplate.update("INSERT INTO enrollments (id, student_id, course_id, enrollment_date, status) VALUES (2, 1, 2, '2024-01-15', 'ENROLLED')");
        jdbcTemplate.update("INSERT INTO enrollments (id, student_id, course_id, enrollment_date, status) VALUES (3, 1, 5, '2024-01-15', 'ENROLLED')");
        jdbcTemplate.update("INSERT INTO enrollments (id, student_id, course_id, enrollment_date, status) VALUES (4, 2, 1, '2024-01-16', 'ENROLLED')");
        jdbcTemplate.update("INSERT INTO enrollments (id, student_id, course_id, enrollment_date, status) VALUES (5, 2, 2, '2024-01-16', 'ENROLLED')");
        jdbcTemplate.update("INSERT INTO enrollments (id, student_id, course_id, enrollment_date, status) VALUES (6, 2, 5, '2024-01-16', 'ENROLLED')");
        jdbcTemplate.update("INSERT INTO enrollments (id, student_id, course_id, enrollment_date, status) VALUES (7, 3, 3, '2024-01-10', 'ENROLLED')");
        jdbcTemplate.update("INSERT INTO enrollments (id, student_id, course_id, enrollment_date, status) VALUES (8, 3, 4, '2024-01-10', 'ENROLLED')");
        jdbcTemplate.update("INSERT INTO enrollments (id, student_id, course_id, enrollment_date, status) VALUES (9, 3, 6, '2024-01-10', 'ENROLLED')");
        jdbcTemplate.update("INSERT INTO enrollments (id, student_id, course_id, enrollment_date, status) VALUES (10, 4, 5, '2024-01-17', 'ENROLLED')");
        jdbcTemplate.update("INSERT INTO enrollments (id, student_id, course_id, enrollment_date, status) VALUES (11, 4, 6, '2024-01-17', 'ENROLLED')");
        jdbcTemplate.update("INSERT INTO enrollments (id, student_id, course_id, enrollment_date, status) VALUES (12, 4, 7, '2024-01-17', 'ENROLLED')");
        jdbcTemplate.update("INSERT INTO enrollments (id, student_id, course_id, enrollment_date, status) VALUES (13, 5, 7, '2024-01-18', 'ENROLLED')");
        jdbcTemplate.update("INSERT INTO enrollments (id, student_id, course_id, enrollment_date, status) VALUES (14, 5, 5, '2024-01-18', 'ENROLLED')");
        jdbcTemplate.update("INSERT INTO enrollments (id, student_id, course_id, enrollment_date, status) VALUES (15, 5, 8, '2024-01-18', 'ENROLLED')");
        syncIdentity("enrollments", 15);

        // 11. Seed Grading Policies
        jdbcTemplate.update("INSERT INTO grading_policies (id, name, description, is_default, grade_boundaries) VALUES (1, 'STANDARD', 'Standard university 4.0 grading scale', TRUE, '{\"A_PLUS\": 90, \"A\": 80, \"B_PLUS\": 70, \"B\": 60, \"C\": 50, \"D\": 40, \"F\": 0}')");
        jdbcTemplate.update("INSERT INTO grading_policies (id, name, description, is_default, grade_boundaries) VALUES (2, 'RIGOROUS', 'Honors division grading scale', FALSE, '{\"A_PLUS\": 95, \"A\": 85, \"B_PLUS\": 75, \"B\": 65, \"C\": 55, \"D\": 45, \"F\": 0}')");
        syncIdentity("grading_policies", 2);

        for (int c = 1; c <= 8; c++) {
            jdbcTemplate.update("INSERT INTO course_grading_policies (course_id, grading_policy_id, effective_from) VALUES (?, 1, '2024-01-01')", c);
        }

        // 12. Seed Attendance Sessions & Records (CS101)
        for (int s = 1; s <= 5; s++) {
            String topic = switch (s) {
                case 1 -> "Course Introduction and Tooling Setup";
                case 2 -> "Data Types, Variables, Operators";
                case 3 -> "Control Flow and Conditionals";
                case 4 -> "Functions and Recursion";
                default -> "Linear Data Structures: Arrays & Lists";
            };
            jdbcTemplate.update("INSERT INTO attendance_sessions (id, course_id, session_date, session_number, topic, duration_minutes, is_cancelled, created_by) VALUES (?, 1, DATEADD('DAY', ? * 2, DATE '2024-01-20'), ?, ?, 60, FALSE, 2)",
                    s, s, s, topic);
        }
        syncIdentity("attendance_sessions", 5);

        // Attendance Records for Alice (enrollment 1) & Bob (enrollment 4)
        for (int s = 1; s <= 5; s++) {
            jdbcTemplate.update("INSERT INTO attendance_records (session_id, enrollment_id, status, recorded_by) VALUES (?, 1, 'PRESENT', 2)", s);
            String bobStatus = (s == 4) ? "ABSENT" : "PRESENT";
            jdbcTemplate.update("INSERT INTO attendance_records (session_id, enrollment_id, status, recorded_by) VALUES (?, 4, ?, 2)", s, bobStatus);
        }

        // 13. Seed Assessments & Marks (CS101)
        jdbcTemplate.update("INSERT INTO assessments (id, course_id, title, description, type, max_marks, weightage, assessment_date, due_date, is_published, created_by) VALUES (1, 1, 'Quiz 1: Basics', 'Syntax, data types, logic operators', 'QUIZ', 20, 10, '2024-01-29', '2024-01-29 23:59:59', TRUE, 2)");
        jdbcTemplate.update("INSERT INTO assessments (id, course_id, title, description, type, max_marks, weightage, assessment_date, due_date, is_published, created_by) VALUES (2, 1, 'Assignment 1: Algorithms', 'Control flow and loops assignment', 'ASSIGNMENT', 50, 20, '2024-02-05', '2024-02-05 23:59:59', TRUE, 2)");
        jdbcTemplate.update("INSERT INTO assessments (id, course_id, title, description, type, max_marks, weightage, assessment_date, due_date, is_published, created_by) VALUES (3, 1, 'Midterm Examination', 'Comprehensive exam on weeks 1-6', 'EXAM', 100, 30, '2024-03-01', '2024-03-01 23:59:59', TRUE, 2)");
        jdbcTemplate.update("INSERT INTO assessments (id, course_id, title, description, type, max_marks, weightage, assessment_date, due_date, is_published, created_by) VALUES (4, 1, 'Final Term Project', 'Full-stack course management project', 'PROJECT', 100, 40, '2024-04-15', '2024-04-15 23:59:59', FALSE, 2)");
        syncIdentity("assessments", 4);

        // Marks for Alice (enrollment 1)
        jdbcTemplate.update("INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, feedback, is_finalized) VALUES (1, 1, 18, 2, '2024-01-30 10:00:00', 'Excellent understanding of basic syntax', TRUE)");
        jdbcTemplate.update("INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, feedback, is_finalized) VALUES (2, 1, 45, 2, '2024-02-06 14:00:00', 'Clean, well-structured modular code', TRUE)");
        jdbcTemplate.update("INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, feedback, is_finalized) VALUES (3, 1, 85, 2, '2024-03-02 09:00:00', 'Top score in cohort', TRUE)");

        // Marks for Bob (enrollment 4)
        jdbcTemplate.update("INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, feedback, is_finalized) VALUES (1, 4, 15, 2, '2024-01-30 10:00:00', 'Good attempt, review operator precedence', TRUE)");
        jdbcTemplate.update("INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, feedback, is_finalized) VALUES (2, 4, 40, 2, '2024-02-06 14:00:00', 'Solid algorithmic logic', TRUE)");
        jdbcTemplate.update("INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, feedback, is_finalized) VALUES (3, 4, 70, 2, '2024-03-02 09:00:00', 'Satisfactory midterm score', TRUE)");

        // 14. Notifications
        jdbcTemplate.update("INSERT INTO notifications (user_id, title, message, type, is_read) VALUES (8, 'Welcome to CS101', 'You have been enrolled in Introduction to Computer Science', 'INFO', FALSE)");
        jdbcTemplate.update("INSERT INTO notifications (user_id, title, message, type, is_read) VALUES (8, 'Quiz 1 Published', 'Quiz 1: Basics is available on your student portal.', 'INFO', FALSE)");
        jdbcTemplate.update("INSERT INTO notifications (user_id, title, message, type, is_read) VALUES (2, 'Enrollment Alert', 'Alice Anderson matriculated into CS101 section.', 'INFO', FALSE)");

        Map<String, Object> summary = new HashMap<>();
        summary.put("status", "SUCCESS");
        summary.put("users", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class));
        summary.put("departments", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM departments", Integer.class));
        summary.put("faculty", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM faculty", Integer.class));
        summary.put("students", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM students", Integer.class));
        summary.put("courses", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM courses", Integer.class));
        summary.put("enrollments", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM enrollments", Integer.class));
        summary.put("assessments", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM assessments", Integer.class));
        summary.put("marks", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM marks", Integer.class));
        summary.put("attendanceSessions", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM attendance_sessions", Integer.class));

        log.info("Demo data reset & reseeding completed successfully: {}", summary);
        return summary;
    }

    private void resetIdentity(String tableName) {
        try {
            jdbcTemplate.execute("ALTER TABLE " + tableName + " ALTER COLUMN id RESTART WITH 1");
        } catch (Exception e) {
            log.debug("Could not reset identity on table {}: {}", tableName, e.getMessage());
        }
    }

    private void syncIdentity(String tableName, int nextId) {
        try {
            jdbcTemplate.execute("ALTER TABLE " + tableName + " ALTER COLUMN id RESTART WITH " + (nextId + 1));
        } catch (Exception e) {
            log.debug("Could not sync identity on table {}: {}", tableName, e.getMessage());
        }
    }
}
