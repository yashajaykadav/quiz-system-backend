package com.quiz.quiz_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 FIX: Break circular reference completely
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    @JsonIgnore
    private QuizAttempt quizAttempt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnoreProperties({"subject", "topic"})
    private Question question;

    private Integer selectedOption;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isCorrect = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean attempted = false;

    public void evaluate() {
        if (this.selectedOption != null && this.question != null) {
            this.attempted = true;
            this.isCorrect = this.selectedOption.equals(this.question.getCorrectOption());
        }
    }
}