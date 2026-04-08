package com.quiz.quiz_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "quizzes")
// ✅ FIX: Prevents 500 error when Jackson hits Hibernate proxies
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    // ✅ FIX: Prevents infinite loops and proxy errors
    @JsonIgnoreProperties({"topics", "quizzes", "hibernateLazyInitializer", "handler"})
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    // ✅ FIX: Prevents infinite loops and proxy errors
    @JsonIgnoreProperties({"questions", "subject", "hibernateLazyInitializer", "handler"})
    private Topic topic;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "quiz_questions",
        joinColumns = @JoinColumn(name = "quiz_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    // ✅ FIX: Questions also have lazy properties
    @JsonIgnoreProperties({"topic", "subject", "hibernateLazyInitializer", "handler"})
    private List<Question> questions = new ArrayList<>();

    private int durationMinutes;

    private LocalDateTime scheduledDate;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}