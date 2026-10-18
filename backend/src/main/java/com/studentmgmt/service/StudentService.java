package com.studentmgmt.service;

import com.studentmgmt.dto.StudentDTO;
import com.studentmgmt.dto.StudentCreateDTO;
import com.studentmgmt.dto.StudentUpdateDTO;
import com.studentmgmt.model.Student;
import com.studentmgmt.model.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    Optional<StudentDTO> findById(Long id);

    Optional<StudentDTO> findByStudentId(String studentId);

    Page<StudentDTO> findAll(Pageable pageable);

    Page<StudentDTO> search(Long departmentId, StudentStatus status, String search, Pageable pageable);

    StudentDTO create(StudentCreateDTO createDTO);

    StudentDTO update(Long id, StudentUpdateDTO updateDTO);

    void delete(Long id);

    void changeStatus(Long id, StudentStatus status);

    long countByDepartmentId(Long departmentId);

    long countByStatus(StudentStatus status);

    boolean existsByStudentId(String studentId);

    boolean existsByEmail(String email);

    List<StudentDTO> findByDepartmentId(Long departmentId);

    List<StudentDTO> findByStatus(StudentStatus status);
}