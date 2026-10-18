-- Student Academic and Course Management System Database Schema
-- H2 Database compatible
-- Version: 1.0.0

-- =====================================================================
-- USERS AND ROLES
-- =====================================================================

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    avatar_url VARCHAR(500),
    role_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMP,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role_id);
CREATE INDEX idx_users_active ON users(is_active);

-- =====================================================================
-- DEPARTMENTS (created before faculty due to FK from faculty to departments)
-- =====================================================================

CREATE TABLE IF NOT EXISTS departments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    head_faculty_id BIGINT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE INDEX idx_departments_code ON departments(code);
CREATE INDEX idx_departments_active ON departments(is_active);

-- =====================================================================
-- FACULTY (must be after departments due to FK to departments)
-- =====================================================================

CREATE TABLE IF NOT EXISTS faculty (
    id BIGINT NOT NULL AUTO_INCREMENT,
    faculty_id VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL UNIQUE,
    department_id BIGINT NOT NULL,
    designation VARCHAR(30) NOT NULL,
    specialization VARCHAR(255),
    qualification TEXT,
    hire_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    office_location VARCHAR(255),
    research_interests TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT
);

CREATE INDEX idx_faculty_faculty_id ON faculty(faculty_id);
CREATE INDEX idx_faculty_department ON faculty(department_id);
CREATE INDEX idx_faculty_status ON faculty(status);
CREATE INDEX idx_faculty_designation ON faculty(designation);

-- =====================================================================
-- DEPARTMENTS FOREIGN KEY (added after faculty table exists)
-- =====================================================================

ALTER TABLE departments ADD CONSTRAINT fk_departments_head_faculty
    FOREIGN KEY (head_faculty_id) REFERENCES faculty(id) ON DELETE SET NULL;

-- =====================================================================
-- STUDENTS
-- =====================================================================

CREATE TABLE IF NOT EXISTS students (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL UNIQUE,
    department_id BIGINT NOT NULL,
    admission_year INT NOT NULL,
    current_semester SMALLINT NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    date_of_birth DATE,
    gender VARCHAR(30),
    address TEXT,
    emergency_contact_name VARCHAR(255),
    emergency_contact_phone VARCHAR(20),
    blood_group VARCHAR(5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT
);

CREATE INDEX idx_students_student_id ON students(student_id);
CREATE INDEX idx_students_department ON students(department_id);
CREATE INDEX idx_students_status ON students(status);
CREATE INDEX idx_students_admission_year ON students(admission_year);
CREATE INDEX idx_students_semester ON students(current_semester);

-- =====================================================================
-- COURSES
-- =====================================================================

CREATE TABLE IF NOT EXISTS courses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    credits SMALLINT NOT NULL DEFAULT 3,
    department_id BIGINT NOT NULL,
    semester SMALLINT NOT NULL,
    faculty_id BIGINT,
    max_enrollment INT NOT NULL DEFAULT 60,
    prerequisites TEXT,
    syllabus_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    academic_year VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT,
    FOREIGN KEY (faculty_id) REFERENCES faculty(id) ON DELETE SET NULL
);

CREATE INDEX idx_courses_code ON courses(course_code);
CREATE INDEX idx_courses_department ON courses(department_id);
CREATE INDEX idx_courses_semester ON courses(semester);
CREATE INDEX idx_courses_faculty ON courses(faculty_id);
CREATE INDEX idx_courses_status ON courses(status);
CREATE INDEX idx_courses_academic_year ON courses(academic_year);

-- =====================================================================
-- ENROLLMENTS
-- =====================================================================

CREATE TABLE IF NOT EXISTS enrollments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    enrollment_date DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ENROLLED',
    grade VARCHAR(5),
    grade_points DECIMAL(3,2),
    completion_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    UNIQUE (student_id, course_id)
);

CREATE INDEX idx_enrollments_student ON enrollments(student_id);
CREATE INDEX idx_enrollments_course ON enrollments(course_id);
CREATE INDEX idx_enrollments_status ON enrollments(status);
CREATE INDEX idx_enrollments_date ON enrollments(enrollment_date);

-- =====================================================================
-- ATTENDANCE SESSIONS
-- =====================================================================

CREATE TABLE IF NOT EXISTS attendance_sessions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    session_date DATE NOT NULL,
    session_number INT NOT NULL,
    topic VARCHAR(255),
    duration_minutes INT NOT NULL DEFAULT 60,
    is_cancelled BOOLEAN NOT NULL DEFAULT FALSE,
    cancellation_reason VARCHAR(255),
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
    UNIQUE (course_id, session_date, session_number)
);

CREATE INDEX idx_sessions_course ON attendance_sessions(course_id);
CREATE INDEX idx_sessions_date ON attendance_sessions(session_date);
CREATE INDEX idx_sessions_cancelled ON attendance_sessions(is_cancelled);

-- =====================================================================
-- ATTENDANCE RECORDS
-- =====================================================================

CREATE TABLE IF NOT EXISTS attendance_records (
    id BIGINT NOT NULL AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    enrollment_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ABSENT',
    remarks VARCHAR(500),
    recorded_by BIGINT NOT NULL,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (session_id) REFERENCES attendance_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES users(id) ON DELETE RESTRICT,
    UNIQUE (session_id, enrollment_id)
);

CREATE INDEX idx_attendance_session ON attendance_records(session_id);
CREATE INDEX idx_attendance_enrollment ON attendance_records(enrollment_id);
CREATE INDEX idx_attendance_status ON attendance_records(status);

-- =====================================================================
-- ASSESSMENTS
-- =====================================================================

CREATE TABLE IF NOT EXISTS assessments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,
    max_marks DECIMAL(5,2) NOT NULL,
    weightage DECIMAL(5,2) NOT NULL,
    assessment_date DATE NOT NULL,
    due_date TIMESTAMP,
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_weightage CHECK (weightage >= 0 AND weightage <= 100),
    CONSTRAINT chk_max_marks CHECK (max_marks > 0)
);

CREATE INDEX idx_assessments_course ON assessments(course_id);
CREATE INDEX idx_assessments_type ON assessments(type);
CREATE INDEX idx_assessments_date ON assessments(assessment_date);
CREATE INDEX idx_assessments_published ON assessments(is_published);

-- =====================================================================
-- MARKS / RESULTS
-- =====================================================================

CREATE TABLE IF NOT EXISTS marks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    assessment_id BIGINT NOT NULL,
    enrollment_id BIGINT NOT NULL,
    obtained_marks DECIMAL(5,2) NOT NULL DEFAULT 0,
    graded_by BIGINT,
    graded_at TIMESTAMP,
    feedback TEXT,
    is_finalized BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (assessment_id) REFERENCES assessments(id) ON DELETE CASCADE,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE,
    FOREIGN KEY (graded_by) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE (assessment_id, enrollment_id),
    CONSTRAINT chk_obtained_marks CHECK (obtained_marks >= 0)
);

CREATE INDEX idx_marks_assessment ON marks(assessment_id);
CREATE INDEX idx_marks_enrollment ON marks(enrollment_id);
CREATE INDEX idx_marks_finalized ON marks(is_finalized);

-- =====================================================================
-- GRADING POLICIES
-- =====================================================================

CREATE TABLE IF NOT EXISTS grading_policies (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    grade_boundaries JSON NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE INDEX idx_grading_policies_default ON grading_policies(is_default);

-- =====================================================================
-- COURSE GRADING POLICY MAPPING
-- =====================================================================

CREATE TABLE IF NOT EXISTS course_grading_policies (
    id BIGINT NOT NULL AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    grading_policy_id BIGINT NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (grading_policy_id) REFERENCES grading_policies(id) ON DELETE RESTRICT,
    UNIQUE (course_id, grading_policy_id, effective_from)
);

CREATE INDEX idx_cgp_course ON course_grading_policies(course_id);
CREATE INDEX idx_cgp_policy ON course_grading_policies(grading_policy_id);

-- =====================================================================
-- AUDIT LOG
-- =====================================================================

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    old_values JSON,
    new_values JSON,
    performed_by BIGINT,
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (performed_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_performed_by ON audit_logs(performed_by);
CREATE INDEX idx_audit_performed_at ON audit_logs(performed_at);
CREATE INDEX idx_audit_action ON audit_logs(action);

-- =====================================================================
-- NOTIFICATIONS
-- =====================================================================

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'INFO',
    reference_type VARCHAR(100),
    reference_id BIGINT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_read ON notifications(is_read);
CREATE INDEX idx_notifications_created ON notifications(created_at);