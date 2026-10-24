-- =============================================================================
-- OPTIONAL sample data
-- =============================================================================
-- This script is OPTIONAL and clearly separated. Do NOT run it on production.
-- It inserts a small, self-consistent set of records so you can explore the UI.
-- Run only against a fresh/empty database:
--   mysql -u root -p student_academic_management < sql/sample-data.sql
-- =============================================================================
USE student_academic_management;

-- Faculty
INSERT INTO faculty (faculty_id, employee_id, full_name, email, phone, address,
                     date_of_birth, department, designation, specialization) VALUES
('FAC0001', 'EMP1001', 'Dr. Anita Sharma', 'anita.sharma@college.edu', '+91-9876543210',
 '12, College Road, Delhi', '1980-05-12', 'Computer Science', 'Professor', 'Databases'),
('FAC0002', 'EMP1002', 'Prof. Rajiv Mehta', 'rajiv.mehta@college.edu', '+91-9876543211',
 '14, College Road, Delhi', '1975-11-20', 'Computer Science', 'Associate Professor', 'Algorithms');

-- Students
INSERT INTO students (student_id, roll_number, full_name, email, phone, address,
                      date_of_birth, department, semester, admission_date) VALUES
('STU0001', 'ROLL1001', 'Aarav Singh', 'aarav.singh@student.college.edu', '+91-9123456780',
 '45, MG Road, Delhi', '2002-03-15', 'Computer Science', 3, '2021-08-01'),
('STU0002', 'ROLL1002', 'Ishita Verma', 'ishita.verma@student.college.edu', '+91-9123456781',
 '47, MG Road, Delhi', '2001-07-22', 'Computer Science', 3, '2021-08-01'),
('STU0003', 'ROLL1003', 'Kabir Khan', 'kabir.khan@student.college.edu', '+91-9123456782',
 '49, MG Road, Delhi', '2002-01-10', 'Computer Science', 3, '2021-08-01');

-- Courses
INSERT INTO courses (course_code, course_name, department, semester, credits,
                     maximum_marks, passing_marks, assigned_faculty_id) VALUES
('CS101', 'Database Systems', 'Computer Science', 3, 4, 100, 40, 1),
('CS102', 'Data Structures', 'Computer Science', 3, 4, 100, 40, 2),
('CS103', 'Operating Systems', 'Computer Science', 3, 3, 100, 40, NULL);

-- Enrollments
INSERT INTO enrollments (student_id, course_id, enrollment_date, status) VALUES
(1, 1, '2024-08-01', 'Active'),
(1, 2, '2024-08-01', 'Active'),
(2, 1, '2024-08-01', 'Active'),
(2, 3, '2024-08-01', 'Active'),
(3, 2, '2024-08-01', 'Active');

-- Attendance (class date 2024-08-05)
INSERT INTO attendance (enrollment_id, class_date, status) VALUES
(1, '2024-08-05', 'PRESENT'),
(2, '2024-08-05', 'PRESENT'),
(3, '2024-08-05', 'ABSENT'),
(4, '2024-08-05', 'LATE'),
(5, '2024-08-05', 'PRESENT');

-- Results
INSERT INTO results (enrollment_id, marks_obtained, percentage, grade) VALUES
(1, 85, 85.00, 'B'),
(2, 78, 78.00, 'C'),
(3, 92, 92.00, 'A');
