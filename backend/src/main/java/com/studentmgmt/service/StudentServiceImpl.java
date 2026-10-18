package com.studentmgmt.service;

import com.studentmgmt.dto.StudentDTO;
import com.studentmgmt.dto.StudentCreateDTO;
import com.studentmgmt.dto.StudentUpdateDTO;
import com.studentmgmt.exception.ResourceNotFoundException;
import com.studentmgmt.model.Department;
import com.studentmgmt.model.Role;
import com.studentmgmt.model.Student;
import com.studentmgmt.model.StudentStatus;
import com.studentmgmt.model.User;
import com.studentmgmt.model.UserRole;
import com.studentmgmt.repository.DepartmentRepository;
import com.studentmgmt.repository.RoleRepository;
import com.studentmgmt.repository.StudentRepository;
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
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final DTOMapper dtoMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentDTO> findById(Long id) {
        return studentRepository.findById(id)
                .map(dtoMapper::toStudentDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentDTO> findByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId)
                .map(dtoMapper::toStudentDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentDTO> findAll(Pageable pageable) {
        return studentRepository.findAll(pageable)
                .map(dtoMapper::toStudentDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentDTO> search(Long departmentId, StudentStatus status, String search, Pageable pageable) {
        return studentRepository.searchStudents(departmentId, status, search, pageable)
                .map(dtoMapper::toStudentDTO);
    }

    @Override
    public StudentDTO create(StudentCreateDTO createDTO) {
        // Validate unique constraints
        if (studentRepository.existsByStudentId(createDTO.getStudentId())) {
            throw new IllegalArgumentException("Student ID already exists: " + createDTO.getStudentId());
        }
        if (userRepository.existsByEmail(createDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + createDTO.getEmail());
        }
        if (userRepository.existsByUsername(createDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + createDTO.getUsername());
        }

        Department department = departmentRepository.findById(createDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + createDTO.getDepartmentId()));

        Role studentRole = roleRepository.findByUserRole(UserRole.STUDENT)
                .orElseThrow(() -> new ResourceNotFoundException("Student role not found"));

        // Create user
        User user = User.builder()
                .username(createDTO.getUsername())
                .email(createDTO.getEmail())
                .passwordHash(passwordEncoder.encode(createDTO.getPassword()))
                .fullName(createDTO.getFullName())
                .phone(createDTO.getPhone())
                .role(studentRole)
                .isActive(true)
                .emailVerified(false)
                .build();
        user = userRepository.save(user);

        // Create student
        Student student = Student.builder()
                .studentId(createDTO.getStudentId())
                .user(user)
                .department(department)
                .admissionYear(createDTO.getAdmissionYear())
                .currentSemester(createDTO.getCurrentSemester() != null ? createDTO.getCurrentSemester() : 1)
                .status(StudentStatus.ACTIVE)
                .dateOfBirth(createDTO.getDateOfBirth())
                .gender(createDTO.getGender() != null ? com.studentmgmt.model.Gender.valueOf(createDTO.getGender()) : null)
                .address(createDTO.getAddress())
                .emergencyContactName(createDTO.getEmergencyContactName())
                .emergencyContactPhone(createDTO.getEmergencyContactPhone())
                .bloodGroup(createDTO.getBloodGroup())
                .build();

        student = studentRepository.save(student);
        log.info("Created student: {} ({})", student.getStudentId(), student.getId());

        return dtoMapper.toStudentDTO(student);
    }

    @Override
    public StudentDTO update(Long id, StudentUpdateDTO updateDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        // Update student fields
        if (updateDTO.getCurrentSemester() != null) {
            student.setCurrentSemester(updateDTO.getCurrentSemester());
        }
        if (updateDTO.getDateOfBirth() != null) {
            student.setDateOfBirth(updateDTO.getDateOfBirth());
        }
        if (updateDTO.getGender() != null) {
            student.setGender(com.studentmgmt.model.Gender.valueOf(updateDTO.getGender()));
        }
        if (updateDTO.getAddress() != null) {
            student.setAddress(updateDTO.getAddress());
        }
        if (updateDTO.getEmergencyContactName() != null) {
            student.setEmergencyContactName(updateDTO.getEmergencyContactName());
        }
        if (updateDTO.getEmergencyContactPhone() != null) {
            student.setEmergencyContactPhone(updateDTO.getEmergencyContactPhone());
        }
        if (updateDTO.getBloodGroup() != null) {
            student.setBloodGroup(updateDTO.getBloodGroup());
        }
        if (updateDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(updateDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + updateDTO.getDepartmentId()));
            student.setDepartment(department);
        }

        // Update user fields
        User user = student.getUser();
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
        student = studentRepository.save(student);
        log.info("Updated student: {} ({})", student.getStudentId(), student.getId());

        return dtoMapper.toStudentDTO(student);
    }

    @Override
    public void delete(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        // Soft delete: deactivate user and mark student as withdrawn
        student.getUser().setActive(false);
        student.setStatus(StudentStatus.WITHDRAWN);
        studentRepository.save(student);
        log.info("Deleted student: {} ({})", student.getStudentId(), student.getId());
    }

    @Override
    public void changeStatus(Long id, StudentStatus status) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        student.setStatus(status);
        studentRepository.save(student);
        log.info("Changed status for student {} to {}", student.getStudentId(), status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByDepartmentId(Long departmentId) {
        return studentRepository.countByDepartmentId(departmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(StudentStatus status) {
        return studentRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByStudentId(String studentId) {
        return studentRepository.existsByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> findByDepartmentId(Long departmentId) {
        return studentRepository.findByDepartmentId(departmentId).stream()
                .map(dtoMapper::toStudentDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> findByStatus(StudentStatus status) {
        return studentRepository.findByStatus(status).stream()
                .map(dtoMapper::toStudentDTO)
                .toList();
    }
}