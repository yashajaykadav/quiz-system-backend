package com.quiz.quiz_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QuizResponse {
    private Long id;
    private String title;
    private String description;
    private String subjectName;
    private String topicName;
    private Integer durationMinutes;
    private LocalDateTime scheduledDate;
    private Integer totalMarks;
    private Integer totalQuestions;
}