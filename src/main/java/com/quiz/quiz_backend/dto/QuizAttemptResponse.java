package com.quiz.quiz_backend.dto;

import com.quiz.quiz_backend.entity.AttemptStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QuizAttemptResponse {
    private Long id;
    private Long quizId;
    private String quizTitle;
    private String subjectName;
    private String topicName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer obtainedMarks;
    private Integer totalMarks;
    private Double percentage;
    private AttemptStatus status;
}