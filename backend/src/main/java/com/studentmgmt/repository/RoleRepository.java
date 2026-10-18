package com.studentmgmt.repository;

import com.studentmgmt.model.Role;
import com.studentmgmt.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    Optional<Role> findByUserRole(UserRole userRole);

    boolean existsByName(String name);
}