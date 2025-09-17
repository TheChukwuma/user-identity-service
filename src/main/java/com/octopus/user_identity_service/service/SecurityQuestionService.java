package com.octopus.user_identity_service.service;

import com.octopus.user_identity_service.enums.PredefinedQuestions;
import com.octopus.user_identity_service.enums.SecurityQuestionType;
import com.octopus.user_identity_service.exception.ResourceNotFoundException;
import com.octopus.user_identity_service.model.SecurityQuestion;
import com.octopus.user_identity_service.model.User;
import com.octopus.user_identity_service.repository.SecurityQuestionRepository;
import com.octopus.user_identity_service.repository.UserRepository;
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
public class SecurityQuestionService {

    private final SecurityQuestionRepository securityQuestionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SecurityQuestion createSecurityQuestion(SecurityQuestion securityQuestion, Long userId) {
        log.info("Creating security question for user with id: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Encrypt the answer
        securityQuestion.setAnswer(passwordEncoder.encode(securityQuestion.getAnswer()));
        securityQuestion.setUser(user);
        
        return securityQuestionRepository.save(securityQuestion);
    }

    public SecurityQuestion createPredefinedSecurityQuestion(Long userId, PredefinedQuestions predefinedQuestion, String answer) {
        log.info("Creating predefined security question for user with id: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        SecurityQuestion securityQuestion = new SecurityQuestion();
        securityQuestion.setQuestion(predefinedQuestion.getQuestion());
        securityQuestion.setAnswer(passwordEncoder.encode(answer));
        securityQuestion.setQuestionType(SecurityQuestionType.PREDEFINED);
        securityQuestion.setUser(user);
        
        return securityQuestionRepository.save(securityQuestion);
    }

    @Transactional(readOnly = true)
    public Optional<SecurityQuestion> getSecurityQuestionById(Long id) {
        return securityQuestionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<SecurityQuestion> getSecurityQuestionsByUserId(Long userId) {
        return securityQuestionRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<SecurityQuestion> getActiveSecurityQuestionsByUserId(Long userId) {
        return securityQuestionRepository.findByUserIdAndIsActiveTrue(userId);
    }

    @Transactional(readOnly = true)
    public List<SecurityQuestion> getSecurityQuestionsByQuestionType(SecurityQuestionType questionType) {
        return securityQuestionRepository.findByQuestionType(questionType.name());
    }

    @Transactional(readOnly = true)
    public List<SecurityQuestion> getAllActiveSecurityQuestions() {
        return securityQuestionRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<SecurityQuestion> getAllSecurityQuestions() {
        return securityQuestionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<SecurityQuestion> getAllSecurityQuestions(Pageable pageable) {
        return securityQuestionRepository.findAll(pageable);
    }

    public SecurityQuestion updateSecurityQuestion(Long id, SecurityQuestion securityQuestionDetails) {
        log.info("Updating security question with id: {}", id);
        
        SecurityQuestion securityQuestion = securityQuestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Security question not found with id: " + id));

        securityQuestion.setQuestion(securityQuestionDetails.getQuestion());
        securityQuestion.setAnswer(passwordEncoder.encode(securityQuestionDetails.getAnswer()));
        securityQuestion.setQuestionType(securityQuestionDetails.getQuestionType());
        securityQuestion.setIsActive(securityQuestionDetails.getIsActive());

        return securityQuestionRepository.save(securityQuestion);
    }

    public SecurityQuestion updateAnswer(Long id, String newAnswer) {
        log.info("Updating answer for security question with id: {}", id);
        
        SecurityQuestion securityQuestion = securityQuestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Security question not found with id: " + id));

        securityQuestion.setAnswer(passwordEncoder.encode(newAnswer));
        return securityQuestionRepository.save(securityQuestion);
    }

    public SecurityQuestion activateSecurityQuestion(Long id) {
        log.info("Activating security question with id: {}", id);
        
        SecurityQuestion securityQuestion = securityQuestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Security question not found with id: " + id));

        securityQuestion.setIsActive(true);
        return securityQuestionRepository.save(securityQuestion);
    }

    public SecurityQuestion deactivateSecurityQuestion(Long id) {
        log.info("Deactivating security question with id: {}", id);
        
        SecurityQuestion securityQuestion = securityQuestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Security question not found with id: " + id));

        securityQuestion.setIsActive(false);
        return securityQuestionRepository.save(securityQuestion);
    }

    public boolean validateAnswer(Long id, String providedAnswer) {
        log.info("Validating answer for security question with id: {}", id);
        
        SecurityQuestion securityQuestion = securityQuestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Security question not found with id: " + id));

        return passwordEncoder.matches(providedAnswer, securityQuestion.getAnswer());
    }

    public boolean validateUserSecurityQuestion(Long userId, String question, String answer) {
        log.info("Validating security question for user with id: {}", userId);
        
        List<SecurityQuestion> userQuestions = securityQuestionRepository.findByUserIdAndIsActiveTrue(userId);
        
        return userQuestions.stream()
                .anyMatch(sq -> sq.getQuestion().equals(question) && 
                               passwordEncoder.matches(answer, sq.getAnswer()));
    }

    public void deleteSecurityQuestion(Long id) {
        log.info("Deleting security question with id: {}", id);
        
        if (!securityQuestionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Security question not found with id: " + id);
        }
        
        securityQuestionRepository.deleteById(id);
    }

    public void deleteAllSecurityQuestionsByUserId(Long userId) {
        log.info("Deleting all security questions for user with id: {}", userId);
        
        List<SecurityQuestion> userQuestions = securityQuestionRepository.findByUserId(userId);
        securityQuestionRepository.deleteAll(userQuestions);
    }

    @Transactional(readOnly = true)
    public long countSecurityQuestionsByUserId(Long userId) {
        return securityQuestionRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long countActiveSecurityQuestionsByUserId(Long userId) {
        return securityQuestionRepository.countActiveByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean userHasActiveSecurityQuestions(Long userId) {
        return countActiveSecurityQuestionsByUserId(userId) > 0;
    }

    @Transactional(readOnly = true)
    public List<PredefinedQuestions> getAvailablePredefinedQuestions() {
        return List.of(PredefinedQuestions.values());
    }

    @Transactional(readOnly = true)
    public long countSecurityQuestions() {
        return securityQuestionRepository.count();
    }

    @Transactional(readOnly = true)
    public long countActiveSecurityQuestions() {
        return securityQuestionRepository.findByIsActiveTrue().size();
    }
}
