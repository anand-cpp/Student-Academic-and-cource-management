package com.studentmgmt.repository;

import com.studentmgmt.model.GradingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradingPolicyRepository extends JpaRepository<GradingPolicy, Long> {

    Optional<GradingPolicy> findByName(String name);

    Optional<GradingPolicy> findByIsDefaultTrue();

    List<GradingPolicy> findByIsDefaultFalse();

    boolean existsByName(String name);
}