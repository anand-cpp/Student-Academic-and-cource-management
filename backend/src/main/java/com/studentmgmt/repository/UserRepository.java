package com.studentmgmt.repository;

import com.studentmgmt.model.User;
import com.studentmgmt.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRoleName(String roleName);

    List<User> findByRoleUserRole(UserRole userRole);

    Page<User> findByRoleUserRole(UserRole userRole, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.isActive = true AND (:search IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    long countByRoleUserRole(UserRole userRole);
}