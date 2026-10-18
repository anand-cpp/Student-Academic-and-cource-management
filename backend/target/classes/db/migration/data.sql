-- Development seed data for Student Academic and Course Management System
-- This file is only loaded in 'test' profile or manually for development

-- Password hash for 'password123' using BCrypt
-- $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa

SET FOREIGN_KEY_CHECKS = 0;

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

-- Reset auto increment
ALTER TABLE roles AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE departments AUTO_INCREMENT = 1;
ALTER TABLE students AUTO_INCREMENT = 1;
ALTER TABLE faculty AUTO_INCREMENT = 1;
ALTER TABLE courses AUTO_INCREMENT = 1;
ALTER TABLE enrollments AUTO_INCREMENT = 1;
ALTER TABLE attendance_sessions AUTO_INCREMENT = 1;
ALTER TABLE attendance_records AUTO_INCREMENT = 1;
ALTER TABLE assessments AUTO_INCREMENT = 1;
ALTER TABLE marks AUTO_INCREMENT = 1;
ALTER TABLE grading_policies AUTO_INCREMENT = 1;
ALTER TABLE course_grading_policies AUTO_INCREMENT = 1;
ALTER TABLE audit_logs AUTO_INCREMENT = 1;
ALTER TABLE notifications AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- ROLES (re-insert after delete)
-- =====================================================================

INSERT INTO roles (name, description) VALUES
('ADMIN', 'System administrator with full access'),
('FACULTY', 'Teaching faculty member'),
('STUDENT', 'Enrolled student'),
('DEPARTMENT_HEAD', 'Department head with administrative privileges'),
('REGISTRAR', 'Registrar office staff');

-- =====================================================================
-- USERS
-- =====================================================================

-- Admin user
INSERT INTO users (username, email, password_hash, full_name, phone, role_id, is_active, email_verified) VALUES
('admin', 'admin@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'System Administrator', '+1-555-0100', 1, TRUE, TRUE);

-- Faculty users
INSERT INTO users (username, email, password_hash, full_name, phone, role_id, is_active, email_verified) VALUES
('prof.smith', 'smith@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Prof. John Smith', '+1-555-0101', 2, TRUE, TRUE),
('dr.jones', 'jones@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Dr. Emily Jones', '+1-555-0102', 2, TRUE, TRUE),
('prof.wilson', 'wilson@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Prof. Michael Wilson', '+1-555-0103', 2, TRUE, TRUE),
('dr.brown', 'brown@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Dr. Sarah Brown', '+1-555-0104', 2, TRUE, TRUE),
('prof.davis', 'davis@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Prof. Robert Davis', '+1-555-0105', 2, TRUE, TRUE);

-- Student users
INSERT INTO users (username, email, password_hash, full_name, phone, role_id, is_active, email_verified) VALUES
('student001', 'alice.johnson@student.university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Alice Johnson', '+1-555-1001', 3, TRUE, TRUE),
('student002', 'bob.williams@student.university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Bob Williams', '+1-555-1002', 3, TRUE, TRUE),
('student003', 'carol.davis@student.university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Carol Davis', '+1-555-1003', 3, TRUE, TRUE),
('student004', 'david.miller@student.university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'David Miller', '+1-555-1004', 3, TRUE, TRUE),
('student005', 'eve.wilson@student.university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Eve Wilson', '+1-555-1005', 3, TRUE, TRUE),
('student006', 'frank.moore@student.university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Frank Moore', '+1-555-1006', 3, TRUE, TRUE),
('student007', 'grace.taylor@student.university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Grace Taylor', '+1-555-1007', 3, TRUE, TRUE),
('student008', 'henry.anderson@student.university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Henry Anderson', '+1-555-1008', 3, TRUE, TRUE),
('student009', 'iris.thomas@student.university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Iris Thomas', '+1-555-1009', 3, TRUE, TRUE),
('student010', 'jack.jackson@student.university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Jack Jackson', '+1-555-1010', 3, TRUE, TRUE);

-- Department head
INSERT INTO users (username, email, password_hash, full_name, phone, role_id, is_active, email_verified) VALUES
('depthead.cs', 'cshead@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Prof. Alan Turing', '+1-555-0110', 4, TRUE, TRUE);

-- Registrar
INSERT INTO users (username, email, password_hash, full_name, phone, role_id, is_active, email_verified) VALUES
('registrar', 'registrar@university.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Ms. Patricia Clerk', '+1-555-0120', 5, TRUE, TRUE);

-- =====================================================================
-- DEPARTMENTS
-- =====================================================================

INSERT INTO departments (code, name, description, head_faculty_id, is_active) VALUES
('CS', 'Computer Science', 'Department of Computer Science and Engineering', 1, TRUE),
('EE', 'Electrical Engineering', 'Department of Electrical and Electronics Engineering', NULL, TRUE),
('ME', 'Mechanical Engineering', 'Department of Mechanical Engineering', NULL, TRUE),
('MATH', 'Mathematics', 'Department of Mathematics and Statistics', NULL, TRUE),
('PHYS', 'Physics', 'Department of Physics', NULL, TRUE);

-- Update faculty head for CS department
UPDATE departments SET head_faculty_id = 1 WHERE code = 'CS';

-- =====================================================================
-- FACULTY
-- =====================================================================

INSERT INTO faculty (faculty_id, user_id, department_id, designation, specialization, qualification, hire_date, status, office_location, research_interests) VALUES
('FAC001', 2, 1, 'PROFESSOR', 'Artificial Intelligence, Machine Learning', 'Ph.D. Computer Science, Stanford University', '2010-08-15', 'ACTIVE', 'CS-301', 'Deep Learning, Neural Networks, Computer Vision'),
('FAC002', 3, 1, 'ASSOCIATE_PROFESSOR', 'Database Systems, Data Mining', 'Ph.D. Computer Science, MIT', '2014-01-10', 'ACTIVE', 'CS-302', 'Big Data Analytics, Distributed Databases'),
('FAC003', 4, 1, 'ASSISTANT_PROFESSOR', 'Software Engineering, DevOps', 'Ph.D. Computer Science, UC Berkeley', '2018-08-20', 'ACTIVE', 'CS-303', 'Microservices, Cloud Computing, CI/CD'),
('FAC004', 5, 2, 'PROFESSOR', 'Power Systems, Renewable Energy', 'Ph.D. Electrical Engineering, Georgia Tech', '2008-09-01', 'ACTIVE', 'EE-201', 'Smart Grids, Solar Power Integration'),
('FAC005', 6, 2, 'ASSOCIATE_PROFESSOR', 'Control Systems, Robotics', 'Ph.D. Electrical Engineering, Caltech', '2015-02-15', 'ACTIVE', 'EE-202', 'Autonomous Systems, PID Control'),
('FAC006', 7, 3, 'ASSISTANT_PROFESSOR', 'Thermodynamics, Fluid Mechanics', 'Ph.D. Mechanical Engineering, University of Michigan', '2019-08-01', 'ACTIVE', 'ME-101', 'Heat Transfer, Computational Fluid Dynamics');

-- =====================================================================
-- STUDENTS
-- =====================================================================

INSERT INTO students (student_id, user_id, department_id, admission_year, current_semester, status, date_of_birth, gender, address, emergency_contact_name, emergency_contact_phone, blood_group) VALUES
('STU2023001', 8, 1, 2023, 3, 'ACTIVE', '2005-03-15', 'FEMALE', '123 University Ave, Campus City', 'Mary Johnson', '+1-555-2001', 'O+'),
('STU2023002', 9, 1, 2023, 3, 'ACTIVE', '2004-11-22', 'MALE', '456 College St, Campus City', 'Robert Williams', '+1-555-2002', 'A+'),
('STU2023003', 10, 1, 2023, 3, 'ACTIVE', '2005-07-08', 'FEMALE', '789 Campus Dr, Campus City', 'James Davis', '+1-555-2003', 'B+'),
('STU2023004', 11, 1, 2023, 3, 'ACTIVE', '2004-09-30', 'MALE', '321 Scholar Ln, Campus City', 'Linda Miller', '+1-555-2004', 'AB+'),
('STU2023005', 12, 1, 2023, 3, 'ACTIVE', '2005-01-18', 'FEMALE', '654 Learning Rd, Campus City', 'Thomas Wilson', '+1-555-2005', 'O-'),
('STU2023006', 13, 2, 2023, 3, 'ACTIVE', '2004-05-25', 'MALE', '987 Tech Blvd, Campus City', 'Susan Moore', '+1-555-2006', 'A-'),
('STU2023007', 14, 2, 2023, 3, 'ACTIVE', '2005-08-12', 'FEMALE', '147 Innovation Way, Campus City', 'David Taylor', '+1-555-2007', 'B-'),
('STU2023008', 15, 3, 2023, 3, 'ACTIVE', '2004-12-03', 'MALE', '258 Engineering Pl, Campus City', 'Karen Anderson', '+1-555-2008', 'AB-'),
('STU2023009', 16, 3, 2023, 3, 'ACTIVE', '2005-04-17', 'FEMALE', '369 Mechanics St, Campus City', 'Michael Thomas', '+1-555-2009', 'O+'),
('STU2023010', 17, 4, 2023, 3, 'ACTIVE', '2004-10-09', 'MALE', '741 Calculus Ct, Campus City', 'Elizabeth Jackson', '+1-555-2010', 'A+');

-- =====================================================================
-- COURSES
-- =====================================================================

INSERT INTO courses (course_code, title, description, credits, department_id, semester, faculty_id, max_enrollment, prerequisites, syllabus_url, status, academic_year) VALUES
('CS101', 'Introduction to Programming', 'Fundamentals of programming using Python. Variables, control structures, functions, data structures, and basic algorithms.', 4, 1, 1, 1, 60, 'None', 'https://syllabus.university.edu/CS101', 'ACTIVE', '2024-2025'),
('CS102', 'Data Structures and Algorithms', 'Core data structures: arrays, linked lists, stacks, queues, trees, graphs. Algorithm analysis, sorting, searching, and graph algorithms.', 4, 1, 2, 1, 50, 'CS101', 'https://syllabus.university.edu/CS102', 'ACTIVE', '2024-2025'),
('CS201', 'Database Management Systems', 'Relational model, SQL, normalization, transaction management, indexing, query optimization, and NoSQL overview.', 3, 1, 3, 2, 45, 'CS101', 'https://syllabus.university.edu/CS201', 'ACTIVE', '2024-2025'),
('CS202', 'Operating Systems', 'Process management, memory management, file systems, concurrency, deadlocks, and virtualization.', 3, 1, 3, 3, 40, 'CS102', 'https://syllabus.university.edu/CS202', 'ACTIVE', '2024-2025'),
('CS301', 'Machine Learning', 'Supervised and unsupervised learning, neural networks, deep learning, model evaluation, and practical applications.', 4, 1, 5, 1, 35, 'CS201, MATH201', 'https://syllabus.university.edu/CS301', 'ACTIVE', '2024-2025'),
('CS302', 'Software Engineering', 'Software development lifecycle, agile methodologies, testing, design patterns, architecture, and project management.', 3, 1, 5, 3, 40, 'CS201', 'https://syllabus.university.edu/CS302', 'ACTIVE', '2024-2025'),
('EE101', 'Circuit Analysis', 'DC and AC circuit analysis, network theorems, phasors, resonance, and transient analysis.', 4, 2, 1, 4, 50, 'PHYS101, MATH101', 'https://syllabus.university.edu/EE101', 'ACTIVE', '2024-2025'),
('EE201', 'Signals and Systems', 'Continuous and discrete-time signals, Fourier analysis, Laplace transform, Z-transform, and system analysis.', 3, 2, 3, 4, 40, 'EE101, MATH201', 'https://syllabus.university.edu/EE201', 'ACTIVE', '2024-2025'),
('ME101', 'Engineering Mechanics', 'Statics and dynamics of particles and rigid bodies, equilibrium, friction, centroids, and moments of inertia.', 3, 3, 1, 6, 55, 'PHYS101, MATH101', 'https://syllabus.university.edu/ME101', 'ACTIVE', '2024-2025'),
('MATH201', 'Discrete Mathematics', 'Logic, sets, relations, functions, combinatorics, graph theory, and proof techniques.', 3, 4, 2, NULL, 60, 'MATH101', 'https://syllabus.university.edu/MATH201', 'ACTIVE', '2024-2025');

-- =====================================================================
-- ENROLLMENTS
-- =====================================================================

-- CS Students enrolled in CS courses (Semester 3)
INSERT INTO enrollments (student_id, course_id, enrollment_date, status) VALUES
-- Alice Johnson (STU2023001)
(1, 3, '2024-01-15', 'ENROLLED'), -- CS201
(1, 4, '2024-01-15', 'ENROLLED'), -- CS202
-- Bob Williams (STU2023002)
(2, 3, '2024-01-15', 'ENROLLED'),
(2, 4, '2024-01-15', 'ENROLLED'),
-- Carol Davis (STU2023003)
(3, 3, '2024-01-15', 'ENROLLED'),
(3, 4, '2024-01-15', 'ENROLLED'),
-- David Miller (STU2023004)
(4, 3, '2024-01-15', 'ENROLLED'),
(4, 4, '2024-01-15', 'ENROLLED'),
-- Eve Wilson (STU2023005)
(5, 3, '2024-01-15', 'ENROLLED'),
(5, 4, '2024-01-15', 'ENROLLED'),

-- EE Students
(6, 8, '2024-01-15', 'ENROLLED'), -- EE201
-- ME Students
(7, 9, '2024-01-15', 'ENROLLED'), -- ME101
(8, 9, '2024-01-15', 'ENROLLED'),
-- Math Students
(9, 10, '2024-01-15', 'ENROLLED'), -- MATH201
(10, 10, '2024-01-15', 'ENROLLED');

-- =====================================================================
-- ATTENDANCE SESSIONS
-- =====================================================================

-- CS201 sessions (course_id = 3)
INSERT INTO attendance_sessions (course_id, session_date, session_number, topic, duration_minutes, created_by) VALUES
(3, '2024-01-22', 1, 'Introduction to Databases', 60, 2),
(3, '2024-01-24', 2, 'Relational Model', 60, 2),
(3, '2024-01-29', 3, 'SQL Basics - SELECT', 60, 2),
(3, '2024-01-31', 4, 'SQL - JOINs and Subqueries', 60, 2),
(3, '2024-02-05', 5, 'Normalization - 1NF, 2NF', 60, 2),
(3, '2024-02-07', 6, 'Normalization - 3NF, BCNF', 60, 2),
(3, '2024-02-12', 7, 'Transaction Management', 60, 2),
(3, '2024-02-14', 8, 'Concurrency Control', 60, 2);

-- CS202 sessions (course_id = 4)
INSERT INTO attendance_sessions (course_id, session_date, session_number, topic, duration_minutes, created_by) VALUES
(4, '2024-01-23', 1, 'OS Introduction', 60, 4),
(4, '2024-01-25', 2, 'Processes and Threads', 60, 4),
(4, '2024-01-30', 3, 'CPU Scheduling', 60, 4),
(4, '2024-02-01', 4, 'Process Synchronization', 60, 4),
(4, '2024-02-06', 5, 'Deadlocks', 60, 4),
(4, '2024-02-08', 6, 'Memory Management', 60, 4),
(4, '2024-02-13', 7, 'Virtual Memory', 60, 4),
(4, '2024-02-15', 8, 'File Systems', 60, 4);

-- =====================================================================
-- ATTENDANCE RECORDS
-- =====================================================================

-- Alice Johnson (enrollment_id = 1) - CS201 attendance
INSERT INTO attendance_records (session_id, enrollment_id, status, recorded_by) VALUES
(1, 1, 'PRESENT', 2), (2, 1, 'PRESENT', 2), (3, 1, 'PRESENT', 2), (4, 1, 'PRESENT', 2),
(5, 1, 'PRESENT', 2), (6, 1, 'EXCUSED', 2), (7, 1, 'PRESENT', 2), (8, 1, 'PRESENT', 2);

-- Bob Williams (enrollment_id = 2) - CS201 attendance
INSERT INTO attendance_records (session_id, enrollment_id, status, recorded_by) VALUES
(1, 2, 'PRESENT', 2), (2, 2, 'PRESENT', 2), (3, 2, 'ABSENT', 2), (4, 2, 'PRESENT', 2),
(5, 2, 'PRESENT', 2), (6, 2, 'PRESENT', 2), (7, 2, 'LATE', 2), (8, 2, 'PRESENT', 2);

-- Carol Davis (enrollment_id = 3) - CS201 attendance
INSERT INTO attendance_records (session_id, enrollment_id, status, recorded_by) VALUES
(1, 3, 'PRESENT', 2), (2, 3, 'PRESENT', 2), (3, 3, 'PRESENT', 2), (4, 3, 'PRESENT', 2),
(5, 3, 'PRESENT', 2), (6, 3, 'PRESENT', 2), (7, 3, 'PRESENT', 2), (8, 3, 'PRESENT', 2);

-- David Miller (enrollment_id = 4) - CS201 attendance
INSERT INTO attendance_records (session_id, enrollment_id, status, recorded_by) VALUES
(1, 4, 'PRESENT', 2), (2, 4, 'LATE', 2), (3, 4, 'PRESENT', 2), (4, 4, 'ABSENT', 2),
(5, 4, 'PRESENT', 2), (6, 4, 'PRESENT', 2), (7, 4, 'PRESENT', 2), (8, 4, 'MEDICAL', 2);

-- Eve Wilson (enrollment_id = 5) - CS201 attendance
INSERT INTO attendance_records (session_id, enrollment_id, status, recorded_by) VALUES
(1, 5, 'PRESENT', 2), (2, 5, 'PRESENT', 2), (3, 5, 'PRESENT', 2), (4, 5, 'PRESENT', 2),
(5, 5, 'PRESENT', 2), (6, 5, 'PRESENT', 2), (7, 5, 'PRESENT', 2), (8, 5, 'PRESENT', 2);

-- CS202 attendance (enrollment_ids 6-10)
-- Alice (enrollment_id = 6)
INSERT INTO attendance_records (session_id, enrollment_id, status, recorded_by) VALUES
(9, 6, 'PRESENT', 4), (10, 6, 'PRESENT', 4), (11, 6, 'PRESENT', 4), (12, 6, 'PRESENT', 4),
(13, 6, 'PRESENT', 4), (14, 6, 'PRESENT', 4), (15, 6, 'PRESENT', 4), (16, 6, 'PRESENT', 4);

-- Bob (enrollment_id = 7)
INSERT INTO attendance_records (session_id, enrollment_id, status, recorded_by) VALUES
(9, 7, 'PRESENT', 4), (10, 7, 'ABSENT', 4), (11, 7, 'PRESENT', 4), (12, 7, 'PRESENT', 4),
(13, 7, 'PRESENT', 4), (14, 7, 'LATE', 4), (15, 7, 'PRESENT', 4), (16, 7, 'PRESENT', 4);

-- Carol (enrollment_id = 8)
INSERT INTO attendance_records (session_id, enrollment_id, status, recorded_by) VALUES
(9, 8, 'PRESENT', 4), (10, 8, 'PRESENT', 4), (11, 8, 'PRESENT', 4), (12, 8, 'PRESENT', 4),
(13, 8, 'PRESENT', 4), (14, 8, 'PRESENT', 4), (15, 8, 'PRESENT', 4), (16, 8, 'PRESENT', 4);

-- David (enrollment_id = 9)
INSERT INTO attendance_records (session_id, enrollment_id, status, recorded_by) VALUES
(9, 9, 'PRESENT', 4), (10, 9, 'PRESENT', 4), (11, 9, 'ABSENT', 4), (12, 9, 'PRESENT', 4),
(13, 9, 'PRESENT', 4), (14, 9, 'PRESENT', 4), (15, 9, 'PRESENT', 4), (16, 9, 'PRESENT', 4);

-- Eve (enrollment_id = 10)
INSERT INTO attendance_records (session_id, enrollment_id, status, recorded_by) VALUES
(9, 10, 'PRESENT', 4), (10, 10, 'PRESENT', 4), (11, 10, 'PRESENT', 4), (12, 10, 'PRESENT', 4),
(13, 10, 'PRESENT', 4), (14, 10, 'PRESENT', 4), (15, 10, 'PRESENT', 4), (16, 10, 'EXCUSED', 4);

-- =====================================================================
-- ASSESSMENTS
-- =====================================================================

-- CS201 Assessments (course_id = 3)
INSERT INTO assessments (course_id, title, description, type, max_marks, weightage, assessment_date, due_date, is_published, created_by) VALUES
(3, 'Quiz 1: SQL Basics', 'Basic SELECT, WHERE, ORDER BY', 'QUIZ', 20, 10, '2024-02-01', '2024-02-01 23:59:59', TRUE, 2),
(3, 'Midterm Exam', 'Relational model, SQL, Normalization (1NF-3NF)', 'MIDTERM', 100, 30, '2024-02-15', '2024-02-15 23:59:59', TRUE, 2),
(3, 'Assignment: ER Diagram', 'Design ER diagram for university system', 'ASSIGNMENT', 50, 15, '2024-03-01', '2024-03-08 23:59:59', TRUE, 2),
(3, 'Final Exam', 'Comprehensive: All topics', 'FINAL', 100, 45, '2024-04-20', '2024-04-20 23:59:59', TRUE, 2);

-- CS202 Assessments (course_id = 4)
INSERT INTO assessments (course_id, title, description, type, max_marks, weightage, assessment_date, due_date, is_published, created_by) VALUES
(4, 'Quiz 1: Process Concepts', 'Process states, PCB, context switching', 'QUIZ', 20, 10, '2024-02-01', '2024-02-01 23:59:59', TRUE, 4),
(4, 'Midterm Exam', 'Processes, scheduling, synchronization', 'MIDTERM', 100, 30, '2024-02-16', '2024-02-16 23:59:59', TRUE, 4),
(4, 'Project: Shell Implementation', 'Build a simple Unix shell', 'PROJECT', 50, 20, '2024-03-15', '2024-03-29 23:59:59', TRUE, 4),
(4, 'Final Exam', 'Comprehensive: All topics', 'FINAL', 100, 40, '2024-04-21', '2024-04-21 23:59:59', TRUE, 4);

-- =====================================================================
-- MARKS
-- =====================================================================

-- CS201 Marks (assessment_ids 1-4)
-- Alice (enrollment_id = 1)
INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, is_finalized) VALUES
(1, 1, 18.5, 2, '2024-02-02 10:00:00', TRUE),
(2, 1, 85.0, 2, '2024-02-16 14:00:00', TRUE),
(3, 1, 45.0, 2, '2024-03-10 16:00:00', TRUE),
(4, 1, 92.0, 2, '2024-04-22 10:00:00', TRUE);

-- Bob (enrollment_id = 2)
INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, is_finalized) VALUES
(1, 2, 15.0, 2, '2024-02-02 10:00:00', TRUE),
(2, 2, 72.0, 2, '2024-02-16 14:00:00', TRUE),
(3, 2, 40.0, 2, '2024-03-10 16:00:00', TRUE),
(4, 2, 78.0, 2, '2024-04-22 10:00:00', TRUE);

-- Carol (enrollment_id = 3)
INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, is_finalized) VALUES
(1, 3, 20.0, 2, '2024-02-02 10:00:00', TRUE),
(2, 3, 95.0, 2, '2024-02-16 14:00:00', TRUE),
(3, 3, 48.0, 2, '2024-03-10 16:00:00', TRUE),
(4, 3, 96.0, 2, '2024-04-22 10:00:00', TRUE);

-- David (enrollment_id = 4)
INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, is_finalized) VALUES
(1, 4, 12.0, 2, '2024-02-02 10:00:00', TRUE),
(2, 4, 65.0, 2, '2024-02-16 14:00:00', TRUE),
(3, 4, 35.0, 2, '2024-03-10 16:00:00', TRUE),
(4, 4, 70.0, 2, '2024-04-22 10:00:00', TRUE);

-- Eve (enrollment_id = 5)
INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, is_finalized) VALUES
(1, 5, 19.0, 2, '2024-02-02 10:00:00', TRUE),
(2, 5, 88.0, 2, '2024-02-16 14:00:00', TRUE),
(3, 5, 47.0, 2, '2024-03-10 16:00:00', TRUE),
(4, 5, 90.0, 2, '2024-04-22 10:00:00', TRUE);

-- CS202 Marks (assessment_ids 5-8)
-- Alice (enrollment_id = 6)
INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, is_finalized) VALUES
(5, 6, 18.0, 4, '2024-02-02 10:00:00', TRUE),
(6, 6, 82.0, 4, '2024-02-17 14:00:00', TRUE),
(7, 6, 46.0, 4, '2024-04-01 16:00:00', TRUE),
(8, 6, 88.0, 4, '2024-04-23 10:00:00', TRUE);

-- Bob (enrollment_id = 7)
INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, is_finalized) VALUES
(5, 7, 14.0, 4, '2024-02-02 10:00:00', TRUE),
(6, 7, 70.0, 4, '2024-02-17 14:00:00', TRUE),
(7, 7, 42.0, 4, '2024-04-01 16:00:00', TRUE),
(8, 7, 75.0, 4, '2024-04-23 10:00:00', TRUE);

-- Carol (enrollment_id = 8)
INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, is_finalized) VALUES
(5, 8, 19.0, 4, '2024-02-02 10:00:00', TRUE),
(6, 8, 92.0, 4, '2024-02-17 14:00:00', TRUE),
(7, 8, 48.0, 4, '2024-04-01 16:00:00', TRUE),
(8, 8, 94.0, 4, '2024-04-23 10:00:00', TRUE);

-- David (enrollment_id = 9)
INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, is_finalized) VALUES
(5, 9, 16.0, 4, '2024-02-02 10:00:00', TRUE),
(6, 9, 68.0, 4, '2024-02-17 14:00:00', TRUE),
(7, 9, 38.0, 4, '2024-04-01 16:00:00', TRUE),
(8, 9, 72.0, 4, '2024-04-23 10:00:00', TRUE);

-- Eve (enrollment_id = 10)
INSERT INTO marks (assessment_id, enrollment_id, obtained_marks, graded_by, graded_at, is_finalized) VALUES
(5, 10, 17.0, 4, '2024-02-02 10:00:00', TRUE),
(6, 10, 85.0, 4, '2024-02-17 14:00:00', TRUE),
(7, 10, 44.0, 4, '2024-04-01 16:00:00', TRUE),
(8, 10, 89.0, 4, '2024-04-23 10:00:00', TRUE);

-- =====================================================================
-- GRADING POLICIES
-- =====================================================================

INSERT INTO grading_policies (name, description, is_default, grade_boundaries) VALUES
('STANDARD', 'Standard university grading scale', TRUE, '{"A+": 90, "A": 85, "A-": 80, "B+": 75, "B": 70, "B-": 65, "C+": 60, "C": 55, "C-": 50, "D": 40, "F": 0}'),
('LENIENT', 'Lenient grading for introductory courses', FALSE, '{"A+": 85, "A": 80, "A-": 75, "B+": 70, "B": 65, "B-": 60, "C+": 55, "C": 50, "C-": 45, "D": 35, "F": 0}'),
('STRICT', 'Strict grading for advanced courses', FALSE, '{"A+": 95, "A": 90, "A-": 85, "B+": 80, "B": 75, "B-": 70, "C+": 65, "C": 60, "C-": 55, "D": 50, "F": 0}');

-- Map courses to grading policies
INSERT INTO course_grading_policies (course_id, grading_policy_id, effective_from) VALUES
(3, 1, '2024-01-01'), -- CS201 -> STANDARD
(4, 1, '2024-01-01'); -- CS202 -> STANDARD

-- =====================================================================
-- NOTIFICATIONS (sample)
-- =====================================================================

INSERT INTO notifications (user_id, title, message, type, reference_type, reference_id, is_read) VALUES
(8, 'Welcome to CS201', 'You have been enrolled in Database Management Systems', 'INFO', 'ENROLLMENT', 1, FALSE),
(9, 'Welcome to CS201', 'You have been enrolled in Database Management Systems', 'INFO', 'ENROLLMENT', 2, FALSE),
(2, 'New Enrollment', 'Alice Johnson enrolled in CS201', 'INFO', 'ENROLLMENT', 1, TRUE),
(8, 'Quiz 1 Graded', 'Your Quiz 1: SQL Basics has been graded: 18.5/20', 'SUCCESS', 'ASSESSMENT', 1, FALSE);

SET FOREIGN_KEY_CHECKS = 1;