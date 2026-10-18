package com.studentmgmt.service;

import com.studentmgmt.dto.FacultyDTO;
import com.studentmgmt.dto.FacultyCreateDTO;
import com.studentmgmt.dto.FacultyUpdateDTO;
import com.studentmgmt.exception.ResourceNotFoundException;
import com.studentmgmt.model.Department;
import com.studentmgmt.model.Faculty;
import com.studentmgmt.model.FacultyDesignation;
import com.studentmgmt.model.FacultyStatus;
import com.studentmgmt.model.Role;
import com.studentmgmt.model.User;
import com.studentmgmt.model.UserRole;
import com.studentmgmt.repository.DepartmentRepository;
import com.studentmgmt.repository.FacultyRepository;
import com.studentmgmt.repository.RoleRepository;
import com.studentmgmt.repository.UserRepository;
import com.studentmgmt.util.DTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final DTOMapper dtoMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<FacultyDTO> findById(Long id) {
        return facultyRepository.findById(id)
                .map(dtoMapper::toFacultyDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FacultyDTO> findByFacultyId(String facultyId) {
        return facultyRepository.findByFacultyId(facultyId)
                .map(dtoMapper::toFacultyDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FacultyDTO> search(Long departmentId, FacultyStatus status, String search, Pageable pageable) {
        return facultyRepository.searchFaculty(departmentId, status, search, pageable)
                .map(dtoMapper::toFacultyDTO);
    }

    @Override
    public FacultyDTO create(FacultyCreateDTO createDTO) {
        if (facultyRepository.existsByFacultyId(createDTO.getFacultyId())) {
            throw new IllegalArgumentException("Faculty ID already exists: " + createDTO.getFacultyId());
        }
        if (userRepository.existsByEmail(createDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + createDTO.getEmail());
        }
        if (userRepository.existsByUsername(createDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + createDTO.getUsername());
        }

        Department department = departmentRepository.findById(createDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + createDTO.getDepartmentId()));

        Role facultyRole = roleRepository.findByName(UserRole.FACULTY)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty role not found"));

        User user = User.builder()
                .username(createDTO.getUsername())
                .email(createDTO.getEmail())
                .passwordHash(passwordEncoder.encode(createDTO.getPassword()))
                .fullName(createDTO.getFullName())
                .phone(createDTO.getPhone())
                .role(facultyRole)
                .isActive(true)
                .emailVerified(false)
                .build();
        user = userRepository.save(user);

        Faculty faculty = Faculty.builder()
                .facultyId(createDTO.getFacultyId())
                .user(user)
                .department(department)
                .designation(FacultyDesignation.valueOf(createDTO.getDesignation()))
                .specialization(createDTO.getSpecialization())
                .qualification(createDTO.getQualification())
                .hireDate(createDTO.getHireDate())
                .status(FacultyStatus.ACTIVE)
                .officeLocation(createDTO.getOfficeLocation())
                .researchInterests(createDTO.getResearchInterests())
                .build();

        faculty = facultyRepository.save(faculty);
        log.info("Created faculty: {} ({})", faculty.getFacultyId(), faculty.getId());

        return dtoMapper.toFacultyDTO(faculty);
    }

    @Override
    public FacultyDTO update(Long id, FacultyUpdateDTO updateDTO) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + id));

        if (updateDTO.getDesignation() != null) {
            faculty.setDesignation(FacultyDesignation.valueOf(updateDTO.getDesignation()));
        }
        if (updateDTO.getSpecialization() != null) {
            faculty.setSpecialization(updateDTO.getSpecialization());
        }
        if (updateDTO.getQualification() != null) {
            faculty.setQualification(updateDTO.getQualification());
        }
        if (updateDTO.getOfficeLocation() != null) {
            faculty.setOfficeLocation(updateDTO.getOfficeLocation());
        }
        if (updateDTO.getResearchInterests() != null) {
            faculty.setResearchInterests(updateDTO.getResearchInterests());
        }
        if (updateDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(updateDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + updateDTO.getDepartmentId()));
            faculty.setDepartment(department);
        }

        User user = faculty.getUser();
        if (updateDTO.getFullName() != null) {
            user.setFullName(updateDTO.getFullName());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + updateDTO.getEmail());
            }
            user.setEmail(updateDTO.getEmail());
        }

        userRepository.save(user);
        faculty = facultyRepository.save(faculty);
        log.info("Updated faculty: {} ({})", faculty.getFacultyId(), faculty.getId());

        return dtoMapper.toFacultyDTO(faculty);
    }

    @Override
    public void delete(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + id));

        faculty.getUser().setActive(false);
        faculty.setStatus(FacultyStatus.INACTIVE);
        facultyRepository.save(faculty);
        log.info("Deleted faculty: {} ({})", faculty.getFacultyId(), faculty.getId());
    }

    @Override
    public void changeStatus(Long id, FacultyStatus status) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + id));
        faculty.setStatus(status);
        facultyRepository.save(faculty);
        log.info("Changed status for faculty {} to {}", faculty.getFacultyId(), status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByDepartmentId(Long departmentId) {
        return facultyRepository.countByDepartmentId(departmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(FacultyStatus status) {
        return facultyRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByFacultyId(String facultyId) {
        return facultyRepository.existsByFacultyId(facultyId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacultyDTO> findByDepartmentId(Long departmentId) {
        return facultyRepository.findByDepartmentId(departmentId).stream()
                .map(dtoMapper::toFacultyDTO)
                .toList();
    }
}