package com.quiz.quiz_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    @JsonIgnoreProperties({"studentAnswers"})
    private QuizAttempt quizAttempt;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnoreProperties({"subject", "topic"})
    private Question question;

    private Integer selectedOption; // 1, 2, 3, or 4

    @Builder.Default
    @Column(nullable = false)
    private Boolean isCorrect = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean attempted = false;
}