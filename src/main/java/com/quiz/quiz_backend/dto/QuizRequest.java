package com.quiz.quiz_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuizRequest {
    private String title;
    private String description;
    private Long subjectId;
    private Long topicId;
    private List<Long> questionIds;
    private Integer durationMinutes;
    private LocalDateTime scheduledDate;
}
