package com.quiz.quiz_backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentResultResponse {
    private String studentName;
    private String studentEmail;
    private Long quizId;
    private String quizTitle;
    private String subjectName;
    private String topicName;
    private LocalDate quizDate;
    private Integer totalMarks;
    private Integer obtainedMarks;
    private Double percentage;
    private String status;
    private int warningCount;
}