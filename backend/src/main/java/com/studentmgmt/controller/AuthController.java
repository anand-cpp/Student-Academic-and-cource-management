package com.studentmgmt.controller;

import com.studentmgmt.dto.AuthResponseDTO;
import com.studentmgmt.dto.LoginRequestDTO;
import com.studentmgmt.model.User;
import com.studentmgmt.model.UserRole;
import com.studentmgmt.repository.UserRepository;
import com.studentmgmt.security.JwtUtil;
import com.studentmgmt.security.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Login attempt for: {}", request.getUsernameOrEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());

        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        AuthResponseDTO.UserInfoDTO userInfo = buildUserInfo(user);

        AuthResponseDTO response = AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(86400L) // 24 hours in seconds
                .user(userInfo)
                .build();

        log.info("Login successful for: {}", user.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid refresh token format");
        }

        String refreshToken = authHeader.substring(7);
        String username = jwtUtil.extractUsername(refreshToken);

        if (username != null && jwtUtil.validateToken(refreshToken, userDetailsService.loadUserByUsername(username))) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            User user = userDetailsService.loadUserEntityByUsername(username);

            String newAccessToken = jwtUtil.generateToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            AuthResponseDTO.UserInfoDTO userInfo = buildUserInfo(user);

            AuthResponseDTO response = AuthResponseDTO.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .expiresIn(86400L)
                    .user(userInfo)
                    .build();

            return ResponseEntity.ok(response);
        }

        throw new IllegalArgumentException("Invalid or expired refresh token");
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponseDTO.UserInfoDTO> getCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());

        return ResponseEntity.ok(buildUserInfo(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // With JWT, logout is handled client-side by deleting the token
        // Server-side blacklisting could be implemented here if needed
        return ResponseEntity.ok().build();
    }

    private AuthResponseDTO.UserInfoDTO buildUserInfo(User user) {
        Long studentId = null;
        Long facultyId = null;

        if (user.getRole() != null) {
            if (user.getRole().getUserRole() == UserRole.STUDENT && user.getStudent() != null) {
                studentId = user.getStudent().getId();
            } else if (user.getRole().getUserRole() == UserRole.FACULTY && user.getFaculty() != null) {
                facultyId = user.getFaculty().getId();
            }
        }

        return AuthResponseDTO.UserInfoDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole() != null ? user.getRole().getUserRole() : null)
                .studentId(studentId)
                .facultyId(facultyId)
                .build();
    }
}