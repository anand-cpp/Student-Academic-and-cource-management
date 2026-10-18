package com.studentmgmt.repository;

import com.studentmgmt.model.Faculty;
import com.studentmgmt.model.FacultyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    Optional<Faculty> findByFacultyId(String facultyId);

    Optional<Faculty> findByUserId(Long userId);

    List<Faculty> findByDepartmentId(Long departmentId);

    List<Faculty> findByStatus(FacultyStatus status);

    List<Faculty> findByDesignation(String designation);

    Page<Faculty> findByDepartmentId(Long departmentId, Pageable pageable);

    Page<Faculty> findByStatus(FacultyStatus status, Pageable pageable);

    @Query("SELECT f FROM Faculty f WHERE " +
           "(:departmentId IS NULL OR f.department.id = :departmentId) AND " +
           "(:status IS NULL OR f.status = :status) AND " +
           "(:search IS NULL OR LOWER(f.facultyId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(f.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(f.user.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Faculty> searchFaculty(
            @Param("departmentId") Long departmentId,
            @Param("status") FacultyStatus status,
            @Param("search") String search,
            Pageable pageable);

    long countByDepartmentId(Long departmentId);

    long countByStatus(FacultyStatus status);

    boolean existsByFacultyId(String facultyId);

    boolean existsByUserId(Long userId);
}