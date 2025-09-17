package com.octopus.user_identity_service.service;

import com.octopus.user_identity_service.exception.ResourceNotFoundException;
import com.octopus.user_identity_service.model.Role;
import com.octopus.user_identity_service.model.User;
import com.octopus.user_identity_service.repository.RoleRepository;
import com.octopus.user_identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        log.info("Creating new user: {}", user.getUsername());
        
        // Encode password if not already encoded
        if (!user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByRole(String roleName) {
        return userRepository.findAll().stream()
                .filter(user -> user.hasRole(roleName))
                .toList();
    }

    public User updateUser(Long id, User userDetails) {
        log.info("Updating user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update fields
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setDateOfBirth(userDetails.getDateOfBirth());
        user.setGender(userDetails.getGender());
        user.setProfilePictureUrl(userDetails.getProfilePictureUrl());
        user.setIsEnabled(userDetails.getIsEnabled());
        user.setEmailVerified(userDetails.getEmailVerified());
        user.setPhoneVerified(userDetails.getPhoneVerified());
        user.setTwoFactorEnabled(userDetails.getTwoFactorEnabled());

        return userRepository.save(user);
    }

    public User updatePassword(Long id, String newPassword) {
        log.info("Updating password for user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public User assignRole(Long userId, String roleName) {
        log.info("Assigning role {} to user with id: {}", roleName, userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        user.getRoles().add(role);
        return userRepository.save(user);
    }

    public User removeRole(Long userId, String roleName) {
        log.info("Removing role {} from user with id: {}", roleName, userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.getRoles().removeIf(role -> role.getName().equals(roleName));
        return userRepository.save(user);
    }

    public User updateLastLogin(Long id) {
        log.info("Updating last login for user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setLastLoginAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User lockAccount(Long id, LocalDateTime lockedUntil) {
        log.info("Locking account for user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setIsAccountNonLocked(false);
        user.setAccountLockedUntil(lockedUntil);
        return userRepository.save(user);
    }

    public User unlockAccount(Long id) {
        log.info("Unlocking account for user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setIsAccountNonLocked(true);
        user.setAccountLockedUntil(null);
        user.setFailedLoginAttempts(0);
        return userRepository.save(user);
    }

    public User incrementFailedLoginAttempts(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public long countUsersByRole(String roleName) {
        return userRepository.findAll().stream()
                .filter(user -> user.hasRole(roleName))
                .count();
    }
}
