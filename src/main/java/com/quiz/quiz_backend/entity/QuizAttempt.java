package com.quiz.quiz_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_attempts", indexes = {
        @Index(name = "idx_attempt_quiz", columnList = "quiz_id"),
        @Index(name = "idx_attempt_student", columnList = "student_id"),
        @Index(name = "idx_attempt_status", columnList = "status")
}, uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_quiz_student",
                columnNames = {"quiz_id", "student_id"}
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnoreProperties({"createdBy", "questions"})
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"password", "createdAt", "lastLogin"})
    private User student;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime submittedAt;

    @Column(nullable = false)
    private Integer totalMarks;

    @Builder.Default
    @Column(nullable = false)
    private Integer obtainedMarks = 0;

    @Builder.Default
    @Column(nullable = false)
    private Double percentage = 0.0;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentAnswer> studentAnswers = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private Boolean autoSubmitted = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer warningCount = 0;

    /**
     * Whether the quiz time window has passed.
     */
    public boolean isExpired() {
        if (quiz == null || quiz.getScheduledDate() == null || quiz.getDurationMinutes() == null) {
            return false;
        }
        return LocalDateTime.now()
                .isAfter(quiz.getScheduledDate().plusMinutes(quiz.getDurationMinutes()));
    }

    /**
     * Whether the quiz is currently within its active time window.
     */
    public boolean isActive() {
        if (quiz == null || quiz.getScheduledDate() == null || quiz.getDurationMinutes() == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = quiz.getScheduledDate();
        LocalDateTime end = start.plusMinutes(quiz.getDurationMinutes());
        return now.isAfter(start) && now.isBefore(end);
    }

    /**
     * Calculates and sets the percentage based on obtained and total marks.
     */
    public void calculatePercentage() {
        if (this.totalMarks != null && this.totalMarks > 0) {
            this.percentage = (this.obtainedMarks * 100.0) / this.totalMarks;
        } else {
            this.percentage = 0.0;
        }
    }
}