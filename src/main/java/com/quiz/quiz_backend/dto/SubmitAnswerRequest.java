package com.quiz.quiz_backend.dto;

import lombok.Data;

@Data
public class SubmitAnswerRequest {

    private Long questionId;
    private Integer selectedOption;
}
