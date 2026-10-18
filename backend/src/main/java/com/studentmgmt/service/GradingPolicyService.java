package com.studentmgmt.service;

import com.studentmgmt.dto.GradingPolicyDTO;
import com.studentmgmt.dto.GradingPolicyCreateDTO;
import com.studentmgmt.dto.GradingPolicyUpdateDTO;
import com.studentmgmt.model.GradingPolicy;

import java.util.List;
import java.util.Optional;

public interface GradingPolicyService {

    Optional<GradingPolicyDTO> findById(Long id);

    Optional<GradingPolicyDTO> findDefault();

    List<GradingPolicyDTO> findAll();

    GradingPolicyDTO create(GradingPolicyCreateDTO createDTO);

    GradingPolicyDTO update(Long id, GradingPolicyUpdateDTO updateDTO);

    void delete(Long id);

    void setAsDefault(Long id);

    boolean existsByName(String name);
}