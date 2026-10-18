-- Development seed data for Student Academic and Course Management System
-- H2 Database compatible
-- This file is only loaded in 'local' profile for development

-- Password hash for 'password123' using BCrypt
-- $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa

-- Clear existing data (for clean test runs)
DELETE FROM notifications;
DELETE FROM audit_logs;
DELETE FROM course_grading_policies;
DELETE FROM grading_policies;
DELETE FROM marks;
DELETE FROM assessments;
DELETE FROM attendance_records;
DELETE FROM attendance_sessions;
DELETE FROM enrollments;
DELETE FROM courses;
DELETE FROM faculty;
DELETE FROM students;
DELETE FROM departments;
DELETE FROM users;
DELETE FROM roles;

-- H2 doesn't use AUTO_INCREMENT reset, it handles it automatically with IDENTITY

-- =====================================================================
-- ROLES (re-insert after delete)
-- =====================================================================

INSERT INTO roles (name, description) VALUES
('ADMIN', 'System administrator with full access'),
('FACULTY', 'Faculty member who teaches courses'),
('STUDENT', 'Student enrolled in courses'),
('DEPARTMENT_HEAD', 'Head of an academic department'),
('REGISTRAR', 'Registrar office staff managing enrollments');

-- =====================================================================
-- USERS
-- =====================================================================

-- Admin user
INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES
('admin', 'admin@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'System Administrator', 1, TRUE, TRUE);

-- Faculty users
INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES
('prof.smith', 'smith@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Prof. John Smith', 2, TRUE, TRUE),
('prof.johnson', 'johnson@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Prof. Emily Johnson', 2, TRUE, TRUE),
('prof.williams', 'williams@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Prof. Michael Williams', 2, TRUE, TRUE),
('prof.brown', 'brown@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Prof. Sarah Brown', 2, TRUE, TRUE);

-- Department head
INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES
('dept.head.cs', 'cshead@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Dr. Robert Davis', 4, TRUE, TRUE);

-- Registrar
INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES
('registrar', 'registrar@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Ms. Jennifer Wilson', 5, TRUE, TRUE);

-- Student users
INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES
('student001', 'student001@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Alice Anderson', 3, TRUE, TRUE),
('student002', 'student002@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Bob Baker', 3, TRUE, TRUE),
('student003', 'student003@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Carol Clark', 3, TRUE, TRUE),
('student004', 'student004@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'David Davis', 3, TRUE, TRUE),
('student005', 'student005@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Eva Evans', 3, TRUE, TRUE);

-- =====================================================================
-- DEPARTMENTS
-- =====================================================================

INSERT INTO departments (code, name, description, is_active) VALUES
('CS', 'Computer Science', 'Department of Computer Science and Engineering', TRUE),
('MATH', 'Mathematics', 'Department of Mathematics and Statistics', TRUE),
('PHYS', 'Physics', 'Department of Physics', TRUE),
('ENG', 'English', 'Department of English Literature', TRUE);

-- Update department head (will be set after faculty creation)

-- =====================================================================
-- FACULTY
-- =====================================================================

INSERT INTO faculty (faculty_id, user_id, department_id, designation, specialization, qualification, hire_date, status, office_location, research_interests) VALUES
('FAC001', 2, 1, 'PROFESSOR', 'Artificial Intelligence, Machine Learning', 'Ph.D. Computer Science, Stanford University', '2015-08-15', 'ACTIVE', 'CS-301', 'Deep Learning, Neural Networks, Computer Vision'),
('FAC002', 3, 1, 'ASSOCIATE_PROFESSOR', 'Data Structures, Algorithms', 'Ph.D. Computer Science, MIT', '2018-01-10', 'ACTIVE', 'CS-302', 'Algorithm Design, Complexity Theory'),
('FAC003', 4, 2, 'PROFESSOR', 'Applied Mathematics', 'Ph.D. Mathematics, Princeton University', '2012-09-01', 'ACTIVE', 'MATH-201', 'Numerical Analysis, Optimization'),
('FAC004', 5, 3, 'ASSISTANT_PROFESSOR', 'Quantum Physics', 'Ph.D. Physics, Caltech', '2020-08-15', 'ACTIVE', 'PHYS-101', 'Quantum Computing, Condensed Matter');

-- Update department head for CS department
UPDATE departments SET head_faculty_id = 1 WHERE code = 'CS';

-- =====================================================================
-- STUDENTS
-- =====================================================================

INSERT INTO students (student_id, user_id, department_id, admission_year, current_semester, status, date_of_birth, gender, address, emergency_contact_name, emergency_contact_phone, blood_group) VALUES
('CS2023001', 8, 1, 2023, 3, 'ACTIVE', '2005-03-15', 'FEMALE', '123 Main St, Cityville', 'Robert Anderson', '555-0101', 'O+'),
('CS2023002', 9, 1, 2023, 3, 'ACTIVE', '2004-11-22', 'MALE', '456 Oak Ave, Townsville', 'Mary Baker', '555-0102', 'A+'),
('CS2022001', 10, 1, 2022, 5, 'ACTIVE', '2003-07-08', 'FEMALE', '789 Pine Rd, Villageton', 'James Clark', '555-0103', 'B+'),
('MATH2023001', 11, 2, 2023, 3, 'ACTIVE', '2005-01-30', 'MALE', '321 Elm St, Hamlet', 'Susan Davis', '555-0104', 'AB+'),
('PHYS2023001', 12, 3, 2023, 3, 'ACTIVE', '2004-09-12', 'FEMALE', '654 Maple Dr, Borough', 'Thomas Evans', '555-0105', 'O-');

-- =====================================================================
-- COURSES
-- =====================================================================

INSERT INTO courses (course_code, title, description, credits, department_id, semester, faculty_id, max_enrollment, prerequisites, status, academic_year) VALUES
('CS101', 'Introduction to Computer Science', 'Fundamentals of computer science including programming basics, algorithms, and data structures.', 3, 1, 1, 1, 60, NULL, 'ACTIVE', '2024-2025'),
('CS201', 'Data Structures and Algorithms', 'Advanced data structures: trees, graphs, hash tables. Algorithm design and analysis.', 4, 1, 3, 1, 50, 'CS101', 'ACTIVE', '2024-2025'),
('CS301', 'Machine Learning', 'Introduction to machine learning algorithms, supervised and unsupervised learning.', 4, 1, 5, 2, 40, 'CS201, MATH201', 'ACTIVE', '2024-2025'),
('CS401', 'Artificial Intelligence', 'AI fundamentals: search, knowledge representation, planning, and reasoning.', 3, 1, 7, 1, 30, 'CS301', 'ACTIVE', '2024-2025'),
('MATH101', 'Calculus I', 'Limits, derivatives, integrals, and applications of differential calculus.', 4, 2, 1, 3, 60, NULL, 'ACTIVE', '2024-2025'),
('MATH201', 'Linear Algebra', 'Vector spaces, linear transformations, eigenvalues, and eigenvectors.', 3, 2, 3, 3, 50, 'MATH101', 'ACTIVE', '2024-2025'),
('PHYS101', 'General Physics I', 'Mechanics, thermodynamics, and waves.', 4, 3, 1, 4, 50, NULL, 'ACTIVE', '2024-2025'),
('ENG101', 'English Composition', 'Academic writing, critical reading, and research skills.', 3, 4, 1, NULL, 40, NULL, 'ACTIVE', '2024-2025');

-- =====================================================================
-- ENROLLMENTS
-- =====================================================================

-- Alice (CS2023001) enrolled in CS101, CS201, MATH101
INSERT INTO enrollments (student_id, course_id, enrollment_date, status) VALUES
(1, 1, '2024-01-15', 'ENROLLED'),
(1, 2, '2024-01-15', 'ENROLLED'),
(1, 5, '2024-01-15', 'ENROLLED');

-- Bob (CS2023002) enrolled in CS101, CS201, MATH101
INSERT INTO enrollments (student_id, course_id, enrollment_date, status) VALUES
(2, 1, '2024-01-16', 'ENROLLED'),
(2, 2, '2024-01-16', 'ENROLLED'),
(2, 5, '2024-01-16', 'ENROLLED');

-- Carol (CS2022001) enrolled in CS301, CS401, MATH201
INSERT INTO enrollments (student_id, course_id, enrollment_date, status) VALUES
(3, 3, '2024-01-10', 'ENROLLED'),
(3, 4, '2024-01-10', 'ENROLLED'),
(3, 6, '2024-01-10', 'ENROLLED');

-- David (MATH2023001) enrolled in MATH101, MATH201, PHYS101
INSERT INTO enrollments (student_id, course_id, enrollment_date, status) VALUES
(4, 5, '2024-01-17', 'ENROLLED'),
(4, 6, '2024-01-17', 'ENROLLED'),
(4, 7, '2024-01-17', 'ENROLLED');

-- Eva (PHYS2023001) enrolled in PHYS101, MATH101, ENG101
INSERT INTO enrollments (student_id, course_id, enrollment_date, status) VALUES
(5, 7, '2024-01-18', 'ENROLLED'),
(5, 5, '2024-01-18', 'ENROLLED'),
(5, 8, '2024-01-18', 'ENROLLED');

-- =====================================================================
-- GRADING POLICIES
-- =====================================================================

INSERT INTO grading_policies (name, description, is_default, grade_boundaries) VALUES
('STANDARD', 'Standard university grading scale', TRUE, '{"A_PLUS": 90, "A": 80, "B_PLUS": 70, "B": 60, "C": 50, "D": 40, "F": 0}'),
('LENIENT', 'More lenient grading scale', FALSE, '{"A_PLUS": 85, "A": 75, "B_PLUS": 65, "B": 55, "C": 45, "D": 35, "F": 0}'),
('RIGOROUS', 'More rigorous grading scale', FALSE, '{"A_PLUS": 95, "A": 85, "B_PLUS": 75, "B": 65, "C": 55, "D": 45, "F": 0}');

-- =====================================================================
-- COURSE GRADING POLICIES
-- =====================================================================

INSERT INTO course_grading_policies (course_id, grading_policy_id, effective_from) VALUES
(1, 1, '2024-01-01'),
(2, 1, '2024-01-01'),
(3, 1, '2024-01-01'),
(4, 1, '2024-01-01'),
(5, 1, '2024-01-01'),
(6, 1, '2024-01-01'),
(7, 1, '2024-01-01'),
(8, 1, '2024-01-01');

-- =====================================================================
-- ATTENDANCE SESSIONS (sample data for CS101)
-- =====================================================================

INSERT INTO attendance_sessions (course_id, session_date, session_number, topic, duration_minutes, is_cancelled, created_by) VALUES
(1, '2024-01-22', 1, 'Course Introduction and Syllabus Overview', 60, FALSE, 2),
(1, '2024-01-24', 2, 'Variables, Data Types, and Operators', 60, FALSE, 2),
(1, '2024-01-29', 3, 'Control Structures: If-Else and Loops', 60, FALSE, 2),
(1, '2024-01-31', 4, 'Functions and Modular Programming', 60, FALSE, 2),
(1, '2024-02-05', 5, 'Arrays and ArrayLists', 60, FALSE, 2);

-- =====================================================================
-- ATTENDANCE RECORDS (sample data)
-- =====================================================================

-- Alice attended all sessions
INSERT INTO attendance_records (session_id, enrollment_id, status, recorded_by) VALUES
(1, 1, 'PRESENT', 2),
(2, 1, 'PRESENT', 2),
(3, 1, 'PRESENT', 2),
(4, 1, 'PRESENT', 2),
(5, 1, 'PRESENT', 2);

-- Bob attended 4 out of 5 sessions
INSERT INTO attendance_records (session_id, enrollment_id, status, recorded_by) VALUES
(1, 2, 'PRESENT', 2),
(2, 2, 'PRESENT', 2),
(3, 2, 'PRESENT', 2),
(4, 2, 'ABSENT', 2),
(5, 2, 'PRESENT', 2);

-- =====================================================================
-- ASSESSMENTS (sample data for CS101)
-- =====================================================================

INSERT INTO assessments (course_id, title, description, type, max_marks, weightage, assessment_date, due_date, is_published, created_by) VALUES
(1, 'Quiz 1: Basics', 'Quiz covering variables, data types, and operators', 'QUIZ', 20, 10, '2024-01-29', '2024-01-29 23:59:59', TRUE, 2),
(1, 'Assignment 1: Control Structures', 'Programming assignment on if-else and loops', 'ASSIGNMENT', 50, 20, '2024-02-05', '2024-02-05 23:59:59', TRUE, 2),
(1, 'Midterm Exam', 'Midterm covering weeks 1-6', 'EXAM', 100, 30, '2024-03-01', '2024-03-01 23:59:59', TRUE, 2),
(1, 'Final Exam', 'Comprehensive final exam', 'EXAM', 100, 40, '2024-04-15', '2024-04-15 23:59:59', FALSE, 2);

-- =====================================================================
-- MARKS (sample data for CS101)
-- =====================================================================

-- Alice's marks
INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, feedback, is_finalized) VALUES
(1, 1, 18, 2, '2024-01-30 10:00:00', 'Good understanding of basics', TRUE),
(2, 1, 45, 2, '2024-02-06 14:00:00', 'Well structured code', TRUE),
(3, 1, 85, 2, '2024-03-02 09:00:00', 'Strong performance', TRUE);

-- Bob's marks
INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, feedback, is_finalized) VALUES
(1, 2, 15, 2, '2024-01-30 10:00:00', 'Needs improvement on operators', TRUE),
(2, 2, 40, 2, '2024-02-06 14:00:00', 'Good effort', TRUE),
(3, 2, 70, 2, '2024-03-02 09:00:00', 'Fair understanding', TRUE);

-- =====================================================================
-- NOTIFICATIONS (sample data)
-- =====================================================================

INSERT INTO notifications (user_id, title, message, type, is_read) VALUES
(8, 'Welcome to CS101', 'You have been enrolled in Introduction to Computer Science', 'INFO', FALSE),
(8, 'Quiz 1 Published', 'Quiz 1: Basics is now available. Due: Jan 29', 'INFO', FALSE),
(9, 'Welcome to CS101', 'You have been enrolled in Introduction to Computer Science', 'INFO', FALSE),
(2, 'New Enrollment', 'Alice Anderson has enrolled in CS101', 'INFO', FALSE);