package com.quiz.quiz_backend.dto;

import lombok.Data;

@Data
public class TopicRequest {
    private String name;
    private String description;
    private Long subjectId;
}