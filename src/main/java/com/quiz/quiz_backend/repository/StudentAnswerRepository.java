package com.quiz.quiz_backend.repository;

import com.quiz.quiz_backend.entity.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer,Long> {
    List<StudentAnswer> findByQuizAttemptId(Long quizAttemptId);
    Optional<StudentAnswer> findByQuizAttemptIdAndQuestionId(Long quizAttemptId, Long questionId);

}
