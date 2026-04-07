package com.quiz.quiz_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "quiz_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnoreProperties({ "createdBy" })
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({ "password", "createdAt", "lastLogin" })
    private User student;

    @Column(nullable = false)
    private LocalDateTime startTime;

    // ✅ renamed
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
    @Column(nullable = false)
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;

    @JsonIgnore
    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL)
    private List<StudentAnswer> studentAnswers;

    @Builder.Default
    @Column(nullable = false)
    private Boolean autoSubmitted = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer warningCount = 0;

    // ✅ helpers
    public boolean isExpired() {
        return LocalDateTime.now()
                .isAfter(quiz.getScheduledDate().plusMinutes(quiz.getDurationMinutes()));
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = quiz.getScheduledDate();
        LocalDateTime end = start.plusMinutes(quiz.getDurationMinutes());

        return now.isAfter(start) && now.isBefore(end);
    }
}