package com.quiz.quiz_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,columnDefinition = "Text")
    private String questionText;

    @Column(columnDefinition = "Text")
    private String codeSnippet;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR) // This fixes the "expecting enum" error
    @Column(length = 20)
    private QuestionType type;

    @Column(nullable = false)
    private String option1;

    @Column(nullable = false)
    private String option2;

    @Column(nullable = false)
    private String option3;

    @Column(nullable = false)
    private String option4;

    @Column(nullable = false)
    private Integer correctOption;

    @ManyToOne
    @JoinColumn(name = "subject_id",nullable = false)
    @JsonIgnoreProperties({"topics", "createdBy"})
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "topic_id",nullable = false)
    @JsonIgnoreProperties({"questions", "subject"})
    private Topic topic;

    @Builder.Default
    @Column(nullable = false)
    private Integer marks = 1;

    @Builder.Default
    @Column(nullable = false,updatable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

}
