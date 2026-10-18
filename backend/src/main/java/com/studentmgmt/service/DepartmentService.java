package com.studentmgmt.service;

import com.studentmgmt.dto.DepartmentDTO;
import com.studentmgmt.dto.DepartmentCreateDTO;
import com.studentmgmt.dto.DepartmentUpdateDTO;
import com.studentmgmt.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {

    Optional<DepartmentDTO> findById(Long id);

    Optional<DepartmentDTO> findByCode(String code);

    Page<DepartmentDTO> search(String search, Pageable pageable);

    List<DepartmentDTO> findActive();

    DepartmentDTO create(DepartmentCreateDTO createDTO);

    DepartmentDTO update(Long id, DepartmentUpdateDTO updateDTO);

    void delete(Long id);

    long countActive();

    boolean existsByCode(String code);
}