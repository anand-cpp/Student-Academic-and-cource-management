package com.studentmgmt.service;

import com.studentmgmt.dto.FacultyDTO;
import com.studentmgmt.dto.FacultyCreateDTO;
import com.studentmgmt.dto.FacultyUpdateDTO;
import com.studentmgmt.model.Faculty;
import com.studentmgmt.model.FacultyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FacultyService {

    Optional<FacultyDTO> findById(Long id);

    Optional<FacultyDTO> findByFacultyId(String facultyId);

    Page<FacultyDTO> search(Long departmentId, FacultyStatus status, String search, Pageable pageable);

    FacultyDTO create(FacultyCreateDTO createDTO);

    FacultyDTO update(Long id, FacultyUpdateDTO updateDTO);

    void delete(Long id);

    void changeStatus(Long id, FacultyStatus status);

    long countByDepartmentId(Long departmentId);

    long countByStatus(FacultyStatus status);

    boolean existsByFacultyId(String facultyId);

    boolean existsByEmail(String email);

    List<FacultyDTO> findByDepartmentId(Long departmentId);
}