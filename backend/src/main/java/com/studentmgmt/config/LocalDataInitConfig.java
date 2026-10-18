package com.studentmgmt.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@Profile("local")
public class LocalDataInitConfig implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(LocalDataInitConfig.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting local data initialization...");
        
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        // Clear existing data
        log.info("Clearing existing data...");
        jdbcTemplate.execute("DELETE FROM notifications");
        jdbcTemplate.execute("DELETE FROM audit_logs");
        jdbcTemplate.execute("DELETE FROM course_grading_policies");
        jdbcTemplate.execute("DELETE FROM grading_policies");
        jdbcTemplate.execute("DELETE FROM marks");
        jdbcTemplate.execute("DELETE FROM assessments");
        jdbcTemplate.execute("DELETE FROM attendance_records");
        jdbcTemplate.execute("DELETE FROM attendance_sessions");
        jdbcTemplate.execute("DELETE FROM enrollments");
        jdbcTemplate.execute("DELETE FROM courses");
        jdbcTemplate.execute("DELETE FROM faculty");
        jdbcTemplate.execute("DELETE FROM students");
        jdbcTemplate.execute("DELETE FROM departments");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM roles");
        
        // Insert roles
        log.info("Inserting roles...");
        jdbcTemplate.update("INSERT INTO roles (name, description) VALUES (?, ?)", "ADMIN", "System administrator with full access");
        jdbcTemplate.update("INSERT INTO roles (name, description) VALUES (?, ?)", "FACULTY", "Faculty member who teaches courses");
        jdbcTemplate.update("INSERT INTO roles (name, description) VALUES (?, ?)", "STUDENT", "Student enrolled in courses");
        jdbcTemplate.update("INSERT INTO roles (name, description) VALUES (?, ?)", "DEPARTMENT_HEAD", "Head of an academic department");
        jdbcTemplate.update("INSERT INTO roles (name, description) VALUES (?, ?)", "REGISTRAR", "Registrar office staff managing enrollments");
        
        // Get role IDs
        Integer adminRoleId = jdbcTemplate.queryForObject("SELECT id FROM roles WHERE name = 'ADMIN'", Integer.class);
        Integer facultyRoleId = jdbcTemplate.queryForObject("SELECT id FROM roles WHERE name = 'FACULTY'", Integer.class);
        Integer studentRoleId = jdbcTemplate.queryForObject("SELECT id FROM roles WHERE name = 'STUDENT'", Integer.class);
        Integer deptHeadRoleId = jdbcTemplate.queryForObject("SELECT id FROM roles WHERE name = 'DEPARTMENT_HEAD'", Integer.class);
        Integer registrarRoleId = jdbcTemplate.queryForObject("SELECT id FROM roles WHERE name = 'REGISTRAR'", Integer.class);
        
        log.info("Role IDs - ADMIN: {}, FACULTY: {}, STUDENT: {}, DEPARTMENT_HEAD: {}, REGISTRAR: {}", 
            adminRoleId, facultyRoleId, studentRoleId, deptHeadRoleId, registrarRoleId);
        
        // Insert users
        log.info("Inserting users...");
        String pwHash = passwordEncoder.encode("password123");
        log.info("Generated new hash for password123: {}", pwHash);
        
        jdbcTemplate.update("INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (?, ?, ?, ?, ?, ?, ?)", 
            "admin", "admin@university.edu", pwHash, "System Administrator", adminRoleId, true, true);
        jdbcTemplate.update("INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (?, ?, ?, ?, ?, ?, ?)", 
            "prof.smith", "smith@university.edu", pwHash, "Prof. John Smith", facultyRoleId, true, true);
        jdbcTemplate.update("INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (?, ?, ?, ?, ?, ?, ?)", 
            "prof.johnson", "johnson@university.edu", pwHash, "Prof. Emily Johnson", facultyRoleId, true, true);
        jdbcTemplate.update("INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (?, ?, ?, ?, ?, ?, ?)", 
            "prof.williams", "williams@university.edu", pwHash, "Prof. Michael Williams", facultyRoleId, true, true);
        jdbcTemplate.update("INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (?, ?, ?, ?, ?, ?, ?)", 
            "prof.brown", "brown@university.edu", pwHash, "Prof. Sarah Brown", facultyRoleId, true, true);
        jdbcTemplate.update("INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (?, ?, ?, ?, ?, ?, ?)", 
            "dept.head.cs", "cshead@university.edu", pwHash, "Dr. Robert Davis", deptHeadRoleId, true, true);
        jdbcTemplate.update("INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (?, ?, ?, ?, ?, ?, ?)", 
            "registrar", "registrar@university.edu", pwHash, "Ms. Jennifer Wilson", registrarRoleId, true, true);
        jdbcTemplate.update("INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (?, ?, ?, ?, ?, ?, ?)", 
            "student001", "student001@university.edu", pwHash, "Alice Anderson", studentRoleId, true, true);
        jdbcTemplate.update("INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (?, ?, ?, ?, ?, ?, ?)", 
            "student002", "student002@university.edu", pwHash, "Bob Baker", studentRoleId, true, true);
        jdbcTemplate.update("INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (?, ?, ?, ?, ?, ?, ?)", 
            "student003", "student003@university.edu", pwHash, "Carol Clark", studentRoleId, true, true);
        jdbcTemplate.update("INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (?, ?, ?, ?, ?, ?, ?)", 
            "student004", "student004@university.edu", pwHash, "David Davis", studentRoleId, true, true);
        jdbcTemplate.update("INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, email_verified) VALUES (?, ?, ?, ?, ?, ?, ?)", 
            "student005", "student005@university.edu", pwHash, "Eva Evans", studentRoleId, true, true);
        
        // Insert departments
        log.info("Inserting departments...");
        jdbcTemplate.update("INSERT INTO departments (code, name, description, is_active) VALUES (?, ?, ?, ?)", "CS", "Computer Science", "Department of Computer Science and Engineering", true);
        jdbcTemplate.update("INSERT INTO departments (code, name, description, is_active) VALUES (?, ?, ?, ?)", "MATH", "Mathematics", "Department of Mathematics and Statistics", true);
        jdbcTemplate.update("INSERT INTO departments (code, name, description, is_active) VALUES (?, ?, ?, ?)", "PHYS", "Physics", "Department of Physics", true);
        jdbcTemplate.update("INSERT INTO departments (code, name, description, is_active) VALUES (?, ?, ?, ?)", "ENG", "English", "Department of English Literature", true);
        
        log.info("Local data initialization completed successfully!");
        
        // Verify data was inserted
        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        log.info("Total users in database: {}", userCount);
        Integer adminCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE username = 'admin'", Integer.class);
        log.info("Admin users in database: {}", adminCount);
        if (adminCount > 0) {
            String dbHash = jdbcTemplate.queryForObject("SELECT password_hash FROM users WHERE username = 'admin'", String.class);
            log.info("Admin password hash: {}", dbHash);
            log.info("Password match test: {}", passwordEncoder.matches("password123", pwHash));
        }
    }
}