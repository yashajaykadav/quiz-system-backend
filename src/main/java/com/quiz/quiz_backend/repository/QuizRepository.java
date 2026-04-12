package com.quiz.quiz_backend.repository;

import com.quiz.quiz_backend.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz,Long> {
    List<Quiz> findByScheduledDate(LocalDate date);
    List<Quiz> findBySubjectId(Long subId);
    List<Quiz> findByScheduledDateBetween(LocalDateTime start, LocalDateTime end);

    void deleteById(Long id);

}
