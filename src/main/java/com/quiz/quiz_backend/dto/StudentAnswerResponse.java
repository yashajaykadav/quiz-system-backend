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
    private Integer selectedOption;
    private Boolean isCorrect;
    private Boolean attempted;
}