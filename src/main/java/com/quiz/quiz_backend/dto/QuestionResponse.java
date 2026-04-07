package com.quiz.quiz_backend.dto;

import com.quiz.quiz_backend.entity.QuestionType;
import lombok.Data;

@Data
public class QuestionResponse {
    private Long id;
    private String questionText;
    private String codeSnippet;
    private QuestionType type;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    // Note: correctOption not included in student view
}