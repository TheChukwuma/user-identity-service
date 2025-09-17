package com.octopus.user_identity_service.service;

import com.octopus.user_identity_service.dto.AuthRequest;
import com.octopus.user_identity_service.dto.RegisterRequest;
import com.octopus.user_identity_service.exception.AuthenticationException;
import com.octopus.user_identity_service.exception.UserAlreadyExistsException;
import com.octopus.user_identity_service.model.Role;
import com.octopus.user_identity_service.model.User;
import com.octopus.user_identity_service.repository.RoleRepository;
import com.octopus.user_identity_service.repository.UserRepository;
import com.octopus.user_identity_service.security.CustomUserDetails;
import com.octopus.user_identity_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public User register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use!");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        // Assign default role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role 'USER' not found"));
        user.setRoles(Set.of(userRole));

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        return savedUser;
    }

    @Transactional
    public User authenticate(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsernameOrEmail(request.getUsername())
                    .orElseThrow(() -> new AuthenticationException("User not found"));

            // Update last login time
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            log.info("User authenticated successfully: {}", user.getUsername());
            return user;

        } catch (Exception e) {
            log.error("Authentication failed for user: {}", request.getUsername());
            throw new AuthenticationException("Invalid username/email or password");
        }
    }

    @Transactional(readOnly = true)
    public User refreshToken(String refreshToken) {
        try {
            String username = jwtUtil.extractUsername(refreshToken);

            if (username != null) {
                User user = userRepository.findByUsernameOrEmail(username)
                        .orElseThrow(() -> new AuthenticationException("User not found"));

                if (jwtUtil.isRefreshToken(refreshToken) && jwtUtil.validateToken(refreshToken, new CustomUserDetails(user))) {
                    return user;
                }
            }

            throw new AuthenticationException("Invalid refresh token");

        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new AuthenticationException("Invalid refresh token");
        }
    }
}
