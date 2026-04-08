package com.quiz.quiz_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// ✅ FIX 1: Class-level ignore for Hibernate Proxies
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
@Table(name = "quizzes", indexes = {
        @Index(name = "idx_quiz_subject", columnList = "subject_id"),
        @Index(name = "idx_quiz_topic", columnList = "topic_id"),
        @Index(name = "idx_quiz_status", columnList = "status"),
        @Index(name = "idx_quiz_scheduled_date", columnList = "scheduledDate")
})
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
    // ✅ FIX 2: Added proxy ignores here to prevent 500 errors on fetch
    @JsonIgnoreProperties({"topics", "createdBy", "hibernateLazyInitializer", "handler"})
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    // ✅ FIX 3: Added proxy ignores here
    @JsonIgnoreProperties({"questions", "subject", "hibernateLazyInitializer", "handler"})
    private Topic topic;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "quiz_questions",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    // ✅ FIX 4: Added proxy ignores here
    @JsonIgnoreProperties({"subject", "topic", "hibernateLazyInitializer", "handler"})
    private List<Question> questions = new ArrayList<>();

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private LocalDateTime scheduledDate;

    @Column(nullable = false)
    private Integer totalMarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private QuizStatus status = QuizStatus.SCHEDULED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    // ✅ FIX 5: Added proxy ignores here
    @JsonIgnoreProperties({"password", "createdAt", "lastLogin", "hibernateLazyInitializer", "handler"})
    private User createdBy;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    /**
     * Returns the end time of this quiz.
     */
    public LocalDateTime getEndTime() {
        return scheduledDate.plusMinutes(durationMinutes);
    }

    /**
     * Checks if the quiz window is currently active.
     */
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(scheduledDate) && now.isBefore(getEndTime());
    }
}