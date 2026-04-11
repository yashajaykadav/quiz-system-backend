package com.quiz.quiz_backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentAnswerResponse {

    private Long id;
    private Long questionId;
    private String questionText;
    private String codeSnippet;
    private String type;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private Integer marks;
    
    private Integer selectedOption;
    private Boolean isCorrect;
    private Boolean attempted;
}