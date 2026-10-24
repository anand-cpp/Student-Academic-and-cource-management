-- =============================================================================
-- Student Academic & Course Management System
-- MySQL schema initialization script
-- Database: student_academic_management
-- =============================================================================
-- Run this script once to create the database and all required tables.
-- Example (MySQL client):
--   mysql -u root -p < sql/schema.sql
--
-- Identifier strategy:
--   Every table has a surrogate `id` (INT AUTO_INCREMENT PRIMARY KEY) used for
--   foreign-key references. In addition, students carry a unique business code
--   `student_id` (e.g. STU0001), faculty carry `faculty_id`/`employee_id`, and
--   courses carry `course_code` (e.g. CS101). These business codes are generated
--   by the application, not by MySQL.
-- =============================================================================

DROP DATABASE IF EXISTS student_academic_management;
CREATE DATABASE student_academic_management
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE student_academic_management;

-- -----------------------------------------------------------------------------
-- 1. students
-- -----------------------------------------------------------------------------
CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL UNIQUE COMMENT 'Application-generated business code, e.g. STU0001',
    roll_number VARCHAR(20) NOT NULL UNIQUE COMMENT 'Unique roll number',
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(30),
    address TEXT,
    date_of_birth DATE,
    department VARCHAR(100) NOT NULL,
    semester TINYINT NOT NULL,
    admission_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_students_semester CHECK (semester >= 1 AND semester <= 12),
    INDEX idx_students_department (department),
    INDEX idx_students_email (email)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 2. faculty
-- -----------------------------------------------------------------------------
CREATE TABLE faculty (
    id INT AUTO_INCREMENT PRIMARY KEY,
    faculty_id VARCHAR(20) NOT NULL UNIQUE COMMENT 'Application-generated business code, e.g. FAC0001',
    employee_id VARCHAR(20) NOT NULL UNIQUE COMMENT 'Unique employee id',
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(30),
    address TEXT,
    date_of_birth DATE,
    department VARCHAR(100) NOT NULL,
    designation VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_faculty_department (department)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 3. courses
-- -----------------------------------------------------------------------------
CREATE TABLE courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL UNIQUE COMMENT 'Unique course code, e.g. CS101',
    course_name VARCHAR(150) NOT NULL,
    department VARCHAR(100) NOT NULL,
    semester TINYINT NOT NULL,
    credits INT NOT NULL,
    maximum_marks DECIMAL(6,2) NOT NULL,
    passing_marks DECIMAL(6,2) NOT NULL,
    assigned_faculty_id INT NULL COMMENT 'FK to faculty; SET NULL when faculty leaves',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_course_semester CHECK (semester >= 1 AND semester <= 12),
    CONSTRAINT chk_course_credits CHECK (credits > 0),
    CONSTRAINT chk_course_marks CHECK (passing_marks >= 0 AND passing_marks <= maximum_marks),
    CONSTRAINT fk_courses_faculty FOREIGN KEY (assigned_faculty_id)
        REFERENCES faculty(id) ON DELETE SET NULL,
    INDEX idx_courses_code (course_code),
    INDEX idx_courses_department (department),
    INDEX idx_courses_faculty (assigned_faculty_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 4. enrollments
-- -----------------------------------------------------------------------------
CREATE TABLE enrollments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL COMMENT 'FK to students.id',
    course_id INT NOT NULL COMMENT 'FK to courses.id',
    enrollment_date DATE NOT NULL,
    status ENUM('Active','Completed','Dropped') NOT NULL DEFAULT 'Active',
    CONSTRAINT fk_enrollments_student FOREIGN KEY (student_id)
        REFERENCES students(id) ON DELETE RESTRICT,
    CONSTRAINT fk_enrollments_course FOREIGN KEY (course_id)
        REFERENCES courses(id) ON DELETE RESTRICT,
    CONSTRAINT uk_enrollment_student_course_status UNIQUE (student_id, course_id, status),
    INDEX idx_enrollments_student (student_id),
    INDEX idx_enrollments_course (course_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 5. attendance
-- -----------------------------------------------------------------------------
-- Rule (documented, applied consistently):
--   Attendance % = (Present + Late) / Total recorded sessions * 100.
--   Late counts as attended (student was in the session). Only Absent reduces %.
--   Future dates are never stored as absences.
-- -----------------------------------------------------------------------------
CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT NOT NULL COMMENT 'FK to enrollments.id',
    class_date DATE NOT NULL,
    status ENUM('PRESENT','ABSENT','LATE') NOT NULL DEFAULT 'PRESENT',
    CONSTRAINT fk_attendance_enrollment FOREIGN KEY (enrollment_id)
        REFERENCES enrollments(id) ON DELETE RESTRICT,
    CONSTRAINT uk_attendance_enrollment_date UNIQUE (enrollment_id, class_date),
    INDEX idx_attendance_enrollment (enrollment_id),
    INDEX idx_attendance_date (class_date)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 6. results
-- -----------------------------------------------------------------------------
-- One final result per enrollment (UNIQUE on enrollment_id).
-- Grading scale (example, easy to modify in service.GradeService):
--   A: 90-100% | B: 80-89.99% | C: 70-79.99% | D: 60-69.99% |
--   E: 50-59.99% | F: below 50%
-- -----------------------------------------------------------------------------
CREATE TABLE results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT NOT NULL UNIQUE COMMENT 'FK to enrollments.id; one final result per enrollment',
    marks_obtained DECIMAL(6,2) NOT NULL,
    percentage DECIMAL(5,2) NOT NULL,
    grade CHAR(1) NOT NULL,
    CONSTRAINT fk_results_enrollment FOREIGN KEY (enrollment_id)
        REFERENCES enrollments(id) ON DELETE RESTRICT,
    CONSTRAINT chk_marks_range CHECK (marks_obtained >= 0),
    CONSTRAINT chk_percentage_range CHECK (percentage >= 0 AND percentage <= 100),
    INDEX idx_results_enrollment (enrollment_id)
) ENGINE=InnoDB;

-- =============================================================================
-- Foreign-key deletion policy summary:
--   RESTRICT : students, courses, enrollments -> protects academic history;
--              a student/course/enrollment cannot be deleted while dependent
--              attendance/results/enrollments exist.
--   SET NULL : courses.assigned_faculty_id -> when a faculty member leaves,
--              courses they taught remain, just unassigned (deliberate exception).
-- =============================================================================
