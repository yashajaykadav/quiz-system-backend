package com.quiz.quiz_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    @JsonIgnoreProperties({ "topics", "createdBy" })
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    @JsonIgnoreProperties({ "questions", "subject" })
    private Topic topic;

    @ManyToMany
    @JoinTable(name = "quiz_questions", joinColumns = @JoinColumn(name = "quiz_id"), inverseJoinColumns = @JoinColumn(name = "question_id"))
    @JsonIgnoreProperties({ "subject", "topic" })
    private List<Question> questions;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private LocalDateTime scheduledDate;

    @Column(nullable = false)
    private Integer totalMarks;

    // ✅ NEW
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    @JdbcTypeCode(SqlType.VARCHAR)
    private QuizStatus status = QuizStatus.SCHEDULED;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @JsonIgnoreProperties({ "password", "createdAt", "lastLogin" })
    private User createdBy;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ✅ HELPER
    public LocalDateTime getEndTime() {
        return scheduledDate.plusMinutes(durationMinutes);
    }
}