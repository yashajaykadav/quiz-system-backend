package com.quiz.quiz_backend.dto;

import lombok.Data;

@Data
public class QuestionResponse {
    private Long id;
    private String questionText;
    private String codeSnippet;
    private String type;        // String instead of QuestionType enum — safe for JSON
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private Integer correctOption;  // admin needs this to see the correct answer
    private Integer marks;
    private Long subjectId;
    private String subjectName;
    private Long topicId;
    private String topicName;
}