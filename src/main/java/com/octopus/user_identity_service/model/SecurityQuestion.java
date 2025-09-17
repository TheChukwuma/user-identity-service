package com.octopus.user_identity_service.model;

import com.octopus.user_identity_service.enums.PredefinedQuestions;
import com.octopus.user_identity_service.enums.SecurityQuestionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "security_questions")
@Getter
@Setter
public class SecurityQuestion extends BaseModel {

    @Column(name = "question", nullable = false)
    private String question;

    @Column(name = "answer", nullable = false)
    private String answer;

    @Column(name = "question_type")
    @Enumerated(EnumType.STRING)
    private SecurityQuestionType questionType = SecurityQuestionType.CUSTOM;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
