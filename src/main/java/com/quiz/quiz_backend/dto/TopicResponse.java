package com.quiz.quiz_backend.dto;

import lombok.Data;

@Data
public class TopicResponse {
    private Long id;
    private String name;
    private String description;
    private Long subjectId;
    private String subjectName;
}
