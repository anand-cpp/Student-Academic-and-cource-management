package com.studentmgmt.security;

import com.studentmgmt.model.Enrollment;
import com.studentmgmt.model.Faculty;
import com.studentmgmt.model.Student;
import com.studentmgmt.repository.EnrollmentRepository;
import com.studentmgmt.repository.FacultyRepository;
import com.studentmgmt.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("securityService")
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * Checks if the authenticated user has staff or admin privileges.
     */
    public boolean isStaffOrAdmin(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            String role = authority.getAuthority();
            if ("ROLE_ADMIN".equals(role) || "ROLE_FACULTY".equals(role) ||
                "ROLE_DEPARTMENT_HEAD".equals(role) || "ROLE_REGISTRAR".equals(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Enforces object-level authorization for Student entities by numerical ID.
     */
    public boolean isCurrentStudent(Long studentId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        if (isStaffOrAdmin(auth)) return true;

        if (studentId == null) return false;
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) return false;

        Student student = studentOpt.get();
        return student.getUser() != null && auth.getName().equalsIgnoreCase(student.getUser().getUsername());
    }

    /**
     * Enforces object-level authorization for Student entities by string code (e.g. CS2023001).
     */
    public boolean isCurrentStudentByCode(String studentCode, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        if (isStaffOrAdmin(auth)) return true;

        if (studentCode == null) return false;
        Optional<Student> studentOpt = studentRepository.findByStudentId(studentCode);
        if (studentOpt.isEmpty()) return false;

        Student student = studentOpt.get();
        return student.getUser() != null && auth.getName().equalsIgnoreCase(student.getUser().getUsername());
    }

    /**
     * Enforces that a Student can only access an Enrollment belonging to their own record.
     */
    public boolean isEnrolledStudent(Long enrollmentId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        if (isStaffOrAdmin(auth)) return true;

        if (enrollmentId == null) return false;
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(enrollmentId);
        if (enrollmentOpt.isEmpty()) return false;

        Enrollment enrollment = enrollmentOpt.get();
        return enrollment.getStudent() != null &&
               enrollment.getStudent().getUser() != null &&
               auth.getName().equalsIgnoreCase(enrollment.getStudent().getUser().getUsername());
    }

    /**
     * Enforces that a Faculty member can only modify course records they teach.
     */
    public boolean isCurrentFaculty(Long facultyId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            String role = authority.getAuthority();
            if ("ROLE_ADMIN".equals(role) || "ROLE_DEPARTMENT_HEAD".equals(role)) {
                return true;
            }
        }

        if (facultyId == null) return false;
        Optional<Faculty> facultyOpt = facultyRepository.findById(facultyId);
        if (facultyOpt.isEmpty()) return false;

        Faculty faculty = facultyOpt.get();
        return faculty.getUser() != null && auth.getName().equalsIgnoreCase(faculty.getUser().getUsername());
    }
}
