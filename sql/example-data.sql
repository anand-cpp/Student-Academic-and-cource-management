-- =============================================================================
-- Example data for the Student Academic & Course Management System
-- -----------------------------------------------------------------------------
-- Adds a coherent demo batch on top of sql/sample-data.sql:
--   +9 students, +4 faculty, +6 courses, +31 enrollments, +8 results,
--   and three more attendance dates for every enrollment.
--
-- This script only INSERTs/UPDATEs; it never deletes existing rows.
-- It is intended to be run once. Identifiers are fixed, so a second run will
-- stop at the first duplicate key instead of creating inconsistent records.
--
--   mysql -u root -p student_academic_management < sql/example-data.sql
-- =============================================================================

USE student_academic_management;

START TRANSACTION;

-- -----------------------------------------------------------------------------
-- Faculty
-- -----------------------------------------------------------------------------
INSERT INTO faculty (faculty_id, employee_id, full_name, email, phone, address,
                     date_of_birth, department, designation, specialization) VALUES
('FAC0003', 'EMP1003', 'Dr. Sameer Bose', 'sameer.bose@college.edu', '9876502003',
 'Block C, Campus', '1979-08-17', 'Information Technology', 'Professor', 'Networks'),
('FAC0004', 'EMP1004', 'Prof. Leena Thomas', 'leena.thomas@college.edu', '9876502004',
 'Block D, Campus', '1985-01-26', 'Electronics', 'Assistant Professor', 'Embedded Systems'),
('FAC0005', 'EMP1005', 'Dr. Farah Siddiqui', 'farah.siddiqui@college.edu', '9876502005',
 'Block B, Campus', '1981-05-03', 'Business Administration', 'Associate Professor', 'Finance'),
('FAC0006', 'EMP1006', 'Prof. Manoj Pillai', 'manoj.pillai@college.edu', '9876502006',
 'Block A, Campus', '1987-10-11', 'Computer Science', 'Assistant Professor', 'Operating Systems');

-- -----------------------------------------------------------------------------
-- Students
-- -----------------------------------------------------------------------------
INSERT INTO students (student_id, roll_number, full_name, email, phone, address,
                      date_of_birth, department, semester, admission_date) VALUES
('STU0004', 'ROLL1004', 'Riya Patel', 'riya.patel@student.college.edu', '9876501004',
 '14 Lake View, Pune', '2004-02-18', 'Information Technology', 3, '2024-08-01'),
('STU0005', 'ROLL1005', 'Arjun Nair', 'arjun.nair@student.college.edu', '9876501005',
 '22 Hill Road, Kochi', '2004-06-09', 'Information Technology', 3, '2024-08-01'),
('STU0006', 'ROLL1006', 'Meera Iyer', 'meera.iyer@student.college.edu', '9876501006',
 '8 Temple Street, Chennai', '2003-11-23', 'Computer Science', 5, '2023-08-01'),
('STU0007', 'ROLL1007', 'Rohan Das', 'rohan.das@student.college.edu', '9876501007',
 '5 Station Lane, Kolkata', '2004-01-30', 'Electronics', 3, '2024-08-01'),
('STU0008', 'ROLL1008', 'Sara Ali', 'sara.ali@student.college.edu', '9876501008',
 '31 Garden Avenue, Hyderabad', '2003-09-12', 'Business Administration', 5, '2023-08-01'),
('STU0009', 'ROLL1009', 'Dev Malhotra', 'dev.malhotra@student.college.edu', '9876501009',
 '12 Ring Road, Delhi', '2003-12-05', 'Computer Science', 5, '2023-08-01'),
('STU0010', 'ROLL1010', 'Ananya Rao', 'ananya.rao@student.college.edu', '9876501010',
 '19 Park Street, Bengaluru', '2004-03-27', 'Information Technology', 5, '2023-08-01'),
('STU0011', 'ROLL1011', 'Vikram Joshi', 'vikram.joshi@student.college.edu', '9876501011',
 '7 Market Road, Ahmedabad', '2004-07-14', 'Electronics', 3, '2024-08-01'),
('STU0012', 'ROLL1012', 'Nisha Gupta', 'nisha.gupta@student.college.edu', '9876501012',
 '3 River Side, Lucknow', '2004-04-21', 'Business Administration', 3, '2024-08-01');

-- -----------------------------------------------------------------------------
-- Courses (assigned to the faculty above; CS103 gets its missing teacher)
-- -----------------------------------------------------------------------------
SET @fac3 = (SELECT id FROM faculty WHERE faculty_id = 'FAC0003');
SET @fac4 = (SELECT id FROM faculty WHERE faculty_id = 'FAC0004');
SET @fac5 = (SELECT id FROM faculty WHERE faculty_id = 'FAC0005');
SET @fac6 = (SELECT id FROM faculty WHERE faculty_id = 'FAC0006');

INSERT INTO courses (course_code, course_name, department, semester, credits,
                     maximum_marks, passing_marks, assigned_faculty_id) VALUES
('CS201', 'Web Technologies', 'Computer Science', 5, 4, 100, 40, @fac3),
('CS202', 'Computer Networks', 'Computer Science', 5, 3, 100, 40, @fac3),
('EC201', 'Digital Electronics', 'Electronics', 3, 4, 100, 40, @fac4),
('BA201', 'Financial Accounting', 'Business Administration', 3, 3, 100, 40, @fac5),
('BA202', 'Marketing Management', 'Business Administration', 5, 3, 100, 40, @fac5),
('CS203', 'Software Engineering', 'Computer Science', 5, 4, 100, 40, @fac6);

UPDATE courses SET assigned_faculty_id = @fac6 WHERE course_code = 'CS103';

-- -----------------------------------------------------------------------------
-- Enrollment ids
-- -----------------------------------------------------------------------------
SET @s1  = (SELECT id FROM students WHERE student_id = 'STU0001');
SET @s2  = (SELECT id FROM students WHERE student_id = 'STU0002');
SET @s3  = (SELECT id FROM students WHERE student_id = 'STU0003');
SET @s4  = (SELECT id FROM students WHERE student_id = 'STU0004');
SET @s5  = (SELECT id FROM students WHERE student_id = 'STU0005');
SET @s6  = (SELECT id FROM students WHERE student_id = 'STU0006');
SET @s7  = (SELECT id FROM students WHERE student_id = 'STU0007');
SET @s8  = (SELECT id FROM students WHERE student_id = 'STU0008');
SET @s9  = (SELECT id FROM students WHERE student_id = 'STU0009');
SET @s10 = (SELECT id FROM students WHERE student_id = 'STU0010');
SET @s11 = (SELECT id FROM students WHERE student_id = 'STU0011');
SET @s12 = (SELECT id FROM students WHERE student_id = 'STU0012');

SET @c1 = (SELECT id FROM courses WHERE course_code = 'CS101');
SET @c2 = (SELECT id FROM courses WHERE course_code = 'CS102');
SET @c3 = (SELECT id FROM courses WHERE course_code = 'CS103');
SET @c4 = (SELECT id FROM courses WHERE course_code = 'CS201');
SET @c5 = (SELECT id FROM courses WHERE course_code = 'CS202');
SET @c6 = (SELECT id FROM courses WHERE course_code = 'EC201');
SET @c7 = (SELECT id FROM courses WHERE course_code = 'BA201');
SET @c8 = (SELECT id FROM courses WHERE course_code = 'BA202');
SET @c9 = (SELECT id FROM courses WHERE course_code = 'CS203');

-- Existing students take a few more courses.
INSERT INTO enrollments (student_id, course_id, enrollment_date, status) VALUES
(@s1, @c3, '2024-08-01', 'Active'),
(@s1, @c9, '2023-08-01', 'Active'),
(@s2, @c2, '2024-08-01', 'Active'),
(@s2, @c5, '2024-08-01', 'Active'),
(@s3, @c1, '2024-08-01', 'Active'),
(@s3, @c3, '2024-08-01', 'Active'),
(@s3, @c4, '2024-08-01', 'Active'),

-- New students.
(@s4, @c1, '2024-08-01', 'Completed'),
(@s4, @c2, '2024-08-01', 'Completed'),
(@s4, @c4, '2024-08-01', 'Active'),
(@s4, @c5, '2024-08-01', 'Active'),

(@s5, @c1, '2024-08-01', 'Completed'),
(@s5, @c4, '2024-08-01', 'Active'),
(@s5, @c9, '2024-08-01', 'Active'),

(@s6, @c2, '2023-08-01', 'Completed'),
(@s6, @c5, '2023-08-01', 'Completed'),
(@s6, @c9, '2023-08-01', 'Active'),

(@s7, @c6, '2024-08-01', 'Active'),
(@s7, @c1, '2024-08-01', 'Active'),

(@s8, @c7, '2023-08-01', 'Completed'),
(@s8, @c8, '2023-08-01', 'Active'),

(@s9, @c2, '2023-08-01', 'Completed'),
(@s9, @c5, '2023-08-01', 'Active'),
(@s9, @c9, '2023-08-01', 'Active'),

(@s10, @c1, '2023-08-01', 'Completed'),
(@s10, @c4, '2023-08-01', 'Active'),
(@s10, @c9, '2023-08-01', 'Active'),

(@s11, @c6, '2024-08-01', 'Dropped'),
(@s11, @c1, '2024-08-01', 'Active'),

(@s12, @c7, '2024-08-01', 'Active'),
(@s12, @c8, '2024-08-01', 'Active');

-- -----------------------------------------------------------------------------
-- Results for completed enrollments (percentage matches marks / 100)
-- -----------------------------------------------------------------------------
INSERT INTO results (enrollment_id, marks_obtained, percentage, grade)
SELECT e.id, 88.00, 88.00, 'B' FROM enrollments e
 WHERE e.student_id = @s4 AND e.course_id = @c1;
INSERT INTO results (enrollment_id, marks_obtained, percentage, grade)
SELECT e.id, 91.00, 91.00, 'A' FROM enrollments e
 WHERE e.student_id = @s4 AND e.course_id = @c2;
INSERT INTO results (enrollment_id, marks_obtained, percentage, grade)
SELECT e.id, 74.00, 74.00, 'C' FROM enrollments e
 WHERE e.student_id = @s5 AND e.course_id = @c1;
INSERT INTO results (enrollment_id, marks_obtained, percentage, grade)
SELECT e.id, 96.00, 96.00, 'A' FROM enrollments e
 WHERE e.student_id = @s6 AND e.course_id = @c2;
INSERT INTO results (enrollment_id, marks_obtained, percentage, grade)
SELECT e.id, 83.00, 83.00, 'B' FROM enrollments e
 WHERE e.student_id = @s6 AND e.course_id = @c5;
INSERT INTO results (enrollment_id, marks_obtained, percentage, grade)
SELECT e.id, 69.00, 69.00, 'D' FROM enrollments e
 WHERE e.student_id = @s8 AND e.course_id = @c7;
INSERT INTO results (enrollment_id, marks_obtained, percentage, grade)
SELECT e.id, 42.00, 42.00, 'F' FROM enrollments e
 WHERE e.student_id = @s9 AND e.course_id = @c2;
INSERT INTO results (enrollment_id, marks_obtained, percentage, grade)
SELECT e.id, 58.00, 58.00, 'E' FROM enrollments e
 WHERE e.student_id = @s10 AND e.course_id = @c1;

-- -----------------------------------------------------------------------------
-- Three more class dates for every enrollment. Statuses vary deterministically:
-- roughly 2 in 11 absences, 1 in 7 late, the rest present.
-- -----------------------------------------------------------------------------
INSERT INTO attendance (enrollment_id, class_date, status)
SELECT e.id, d.class_date,
       CASE
           WHEN MOD(e.id + DAY(d.class_date), 11) = 0 THEN 'ABSENT'
           WHEN MOD(e.id + DAY(d.class_date), 7) = 0 THEN 'LATE'
           ELSE 'PRESENT'
       END
  FROM enrollments e
  CROSS JOIN (
      SELECT '2024-08-12' AS class_date
      UNION ALL SELECT '2024-08-19'
      UNION ALL SELECT '2024-08-26'
  ) d
 WHERE NOT EXISTS (
     SELECT 1 FROM attendance a
      WHERE a.enrollment_id = e.id AND a.class_date = d.class_date
 );

COMMIT;

-- -----------------------------------------------------------------------------
-- Verification summary
-- -----------------------------------------------------------------------------
SELECT 'students' AS table_name, COUNT(*) AS rows_now FROM students
UNION ALL SELECT 'faculty', COUNT(*) FROM faculty
UNION ALL SELECT 'courses', COUNT(*) FROM courses
UNION ALL SELECT 'enrollments', COUNT(*) FROM enrollments
UNION ALL SELECT 'results', COUNT(*) FROM results
UNION ALL SELECT 'attendance', COUNT(*) FROM attendance;

SELECT status, COUNT(*) AS enrollments FROM enrollments GROUP BY status ORDER BY status;
SELECT grade, COUNT(*) AS results FROM results GROUP BY grade ORDER BY grade;
