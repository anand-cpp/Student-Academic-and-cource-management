package com.studentmgmt.service;

import com.studentmgmt.dto.UserDTO;
import com.studentmgmt.dto.UserUpdateDTO;
import com.studentmgmt.dto.ChangePasswordDTO;
import com.studentmgmt.model.User;
import com.studentmgmt.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<UserDTO> findById(Long id);

    Optional<UserDTO> getCurrentUser();

    Page<UserDTO> search(UserRole role, String search, Pageable pageable);

    UserDTO update(Long id, UserUpdateDTO updateDTO);

    UserDTO updateCurrentUser(UserUpdateDTO updateDTO);

    void changePassword(ChangePasswordDTO changePasswordDTO);

    void changeStatus(Long id, boolean isActive);

    void delete(Long id);

    long countByRole(UserRole role);
}