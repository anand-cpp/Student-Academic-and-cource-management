package com.studentmgmt.service;

import com.studentmgmt.dto.GradingPolicyDTO;
import com.studentmgmt.dto.GradingPolicyCreateDTO;
import com.studentmgmt.dto.GradingPolicyUpdateDTO;
import com.studentmgmt.exception.ResourceNotFoundException;
import com.studentmgmt.model.GradingPolicy;
import com.studentmgmt.repository.GradingPolicyRepository;
import com.studentmgmt.util.DTOMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GradingPolicyServiceImpl implements GradingPolicyService {

    private final GradingPolicyRepository gradingPolicyRepository;
    private final DTOMapper dtoMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<GradingPolicyDTO> findById(Long id) {
        return gradingPolicyRepository.findById(id)
                .map(dtoMapper::toGradingPolicyDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GradingPolicyDTO> findDefault() {
        return gradingPolicyRepository.findByIsDefaultTrue()
                .map(dtoMapper::toGradingPolicyDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradingPolicyDTO> findAll() {
        return gradingPolicyRepository.findAll().stream()
                .map(dtoMapper::toGradingPolicyDTO)
                .toList();
    }

    @Override
    public GradingPolicyDTO create(GradingPolicyCreateDTO createDTO) {
        if (gradingPolicyRepository.existsByName(createDTO.getName())) {
            throw new IllegalArgumentException("Grading policy name already exists: " + createDTO.getName());
        }

        ObjectNode gradeBoundaries = objectMapper.createObjectNode();
        createDTO.getGradeBoundaries().forEach(gradeBoundaries::put);

        GradingPolicy policy = GradingPolicy.builder()
                .name(createDTO.getName())
                .description(createDTO.getDescription())
                .isDefault(createDTO.getIsDefault() != null ? createDTO.getIsDefault() : false)
                .gradeBoundaries(gradeBoundaries)
                .build();

        // If this is set as default, unset other defaults
        if (policy.getIsDefault()) {
            gradingPolicyRepository.findByIsDefaultTrue().ifPresent(existing -> {
                existing.setIsDefault(false);
                gradingPolicyRepository.save(existing);
            });
        }

        policy = gradingPolicyRepository.save(policy);
        log.info("Created grading policy: {} ({})", policy.getName(), policy.getId());

        return dtoMapper.toGradingPolicyDTO(policy);
    }

    @Override
    public GradingPolicyDTO update(Long id, GradingPolicyUpdateDTO updateDTO) {
        GradingPolicy policy = gradingPolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grading policy not found with id: " + id));

        if (updateDTO.getName() != null && !updateDTO.getName().equals(policy.getName())) {
            if (gradingPolicyRepository.existsByName(updateDTO.getName())) {
                throw new IllegalArgumentException("Grading policy name already exists: " + updateDTO.getName());
            }
            policy.setName(updateDTO.getName());
        }
        if (updateDTO.getDescription() != null) {
            policy.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getIsDefault() != null) {
            boolean wasDefault = policy.getIsDefault();
            policy.setIsDefault(updateDTO.getIsDefault());

            if (policy.getIsDefault() && !wasDefault) {
                gradingPolicyRepository.findByIsDefaultTrue().ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        existing.setIsDefault(false);
                        gradingPolicyRepository.save(existing);
                    }
                });
            }
        }
        if (updateDTO.getGradeBoundaries() != null) {
            ObjectNode gradeBoundaries = objectMapper.createObjectNode();
            updateDTO.getGradeBoundaries().forEach(gradeBoundaries::put);
            policy.setGradeBoundaries(gradeBoundaries);
        }

        policy = gradingPolicyRepository.save(policy);
        log.info("Updated grading policy: {} ({})", policy.getName(), policy.getId());

        return dtoMapper.toGradingPolicyDTO(policy);
    }

    @Override
    public void delete(Long id) {
        GradingPolicy policy = gradingPolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grading policy not found with id: " + id));

        if (policy.getIsDefault()) {
            throw new IllegalArgumentException("Cannot delete the default grading policy");
        }

        gradingPolicyRepository.delete(policy);
        log.info("Deleted grading policy: {} ({})", policy.getName(), policy.getId());
    }

    @Override
    public void setAsDefault(Long id) {
        GradingPolicy policy = gradingPolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grading policy not found with id: " + id));

        gradingPolicyRepository.findByIsDefaultTrue().ifPresent(existing -> {
            existing.setIsDefault(false);
            gradingPolicyRepository.save(existing);
        });

        policy.setIsDefault(true);
        gradingPolicyRepository.save(policy);
        log.info("Set grading policy as default: {} ({})", policy.getName(), policy.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return gradingPolicyRepository.existsByName(name);
    }
}