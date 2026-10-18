package com.studentmgmt.service;

import com.studentmgmt.dto.DepartmentDTO;
import com.studentmgmt.dto.DepartmentCreateDTO;
import com.studentmgmt.dto.DepartmentUpdateDTO;
import com.studentmgmt.exception.ResourceNotFoundException;
import com.studentmgmt.model.Department;
import com.studentmgmt.model.Faculty;
import com.studentmgmt.repository.DepartmentRepository;
import com.studentmgmt.repository.FacultyRepository;
import com.studentmgmt.util.DTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final DTOMapper dtoMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<DepartmentDTO> findById(Long id) {
        return departmentRepository.findById(id)
                .map(dtoMapper::toDepartmentDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DepartmentDTO> findByCode(String code) {
        return departmentRepository.findByCode(code)
                .map(dtoMapper::toDepartmentDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentDTO> search(String search, Pageable pageable) {
        return departmentRepository.searchDepartments(search, pageable)
                .map(dtoMapper::toDepartmentDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> findActive() {
        return departmentRepository.findByIsActiveTrue().stream()
                .map(dtoMapper::toDepartmentDTO)
                .toList();
    }

    @Override
    public DepartmentDTO create(DepartmentCreateDTO createDTO) {
        if (departmentRepository.existsByCode(createDTO.getCode())) {
            throw new IllegalArgumentException("Department code already exists: " + createDTO.getCode());
        }

        Faculty headFaculty = null;
        if (createDTO.getHeadFacultyId() != null) {
            headFaculty = facultyRepository.findById(createDTO.getHeadFacultyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + createDTO.getHeadFacultyId()));
        }

        Department department = Department.builder()
                .code(createDTO.getCode())
                .name(createDTO.getName())
                .description(createDTO.getDescription())
                .headFaculty(headFaculty)
                .isActive(true)
                .build();

        department = departmentRepository.save(department);
        log.info("Created department: {} ({})", department.getCode(), department.getId());

        return dtoMapper.toDepartmentDTO(department);
    }

    @Override
    public DepartmentDTO update(Long id, DepartmentUpdateDTO updateDTO) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        if (updateDTO.getName() != null) {
            department.setName(updateDTO.getName());
        }
        if (updateDTO.getDescription() != null) {
            department.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getIsActive() != null) {
            department.setIsActive(updateDTO.getIsActive());
        }
        if (updateDTO.getHeadFacultyId() != null) {
            Faculty headFaculty = facultyRepository.findById(updateDTO.getHeadFacultyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + updateDTO.getHeadFacultyId()));
            department.setHeadFaculty(headFaculty);
        } else if (updateDTO.getHeadFacultyId() != null && updateDTO.getHeadFacultyId() == 0L) {
            department.setHeadFaculty(null);
        }

        department = departmentRepository.save(department);
        log.info("Updated department: {} ({})", department.getCode(), department.getId());

        return dtoMapper.toDepartmentDTO(department);
    }

    @Override
    public void delete(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        department.setIsActive(false);
        departmentRepository.save(department);
        log.info("Deactivated department: {} ({})", department.getCode(), department.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public long countActive() {
        return departmentRepository.countByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return departmentRepository.existsByCode(code);
    }
}