package com.octopus.user_identity_service.repository;

import com.octopus.user_identity_service.model.SecurityQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Long> {

    List<SecurityQuestion> findByUserId(Long userId);

    List<SecurityQuestion> findByUserIdAndIsActiveTrue(Long userId);

    List<SecurityQuestion> findByQuestionType(String questionType);

    List<SecurityQuestion> findByIsActiveTrue();

    @Query("SELECT sq FROM SecurityQuestion sq WHERE sq.user.id = :userId AND sq.questionType = :questionType")
    List<SecurityQuestion> findByUserIdAndQuestionType(@Param("userId") Long userId, @Param("questionType") String questionType);

    @Query("SELECT sq FROM SecurityQuestion sq WHERE sq.question = :question")
    List<SecurityQuestion> findByQuestion(@Param("question") String question);

    @Query("SELECT COUNT(sq) FROM SecurityQuestion sq WHERE sq.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(sq) FROM SecurityQuestion sq WHERE sq.user.id = :userId AND sq.isActive = true")
    long countActiveByUserId(@Param("userId") Long userId);
}
