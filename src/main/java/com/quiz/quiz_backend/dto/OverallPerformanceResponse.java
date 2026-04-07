package com.quiz.quiz_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class OverallPerformanceResponse {
    private Integer totalQuizzesAttempted;
    private Double averageScore;
    private Double bestScore;
    private List<StudentResultResponse> results;
}