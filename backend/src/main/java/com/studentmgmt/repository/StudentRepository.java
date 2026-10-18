package com.studentmgmt.repository;

import com.studentmgmt.model.Student;
import com.studentmgmt.model.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentId(String studentId);

    Optional<Student> findByUserId(Long userId);

    List<Student> findByDepartmentId(Long departmentId);

    List<Student> findByStatus(StudentStatus status);

    List<Student> findByAdmissionYear(Integer admissionYear);

    List<Student> findByCurrentSemester(Integer semester);

    Page<Student> findByDepartmentId(Long departmentId, Pageable pageable);

    Page<Student> findByStatus(StudentStatus status, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE " +
           "(:departmentId IS NULL OR s.department.id = :departmentId) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:search IS NULL OR LOWER(s.studentId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.user.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Student> searchStudents(
            @Param("departmentId") Long departmentId,
            @Param("status") StudentStatus status,
            @Param("search") String search,
            Pageable pageable);

    long countByDepartmentId(Long departmentId);

    long countByStatus(StudentStatus status);

    boolean existsByStudentId(String studentId);

    boolean existsByUserId(Long userId);
}