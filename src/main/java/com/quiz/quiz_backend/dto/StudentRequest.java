package com.quiz.quiz_backend.dto;

import lombok.Data;

@Data
public class StudentRequest {

    private String fullName;
    private String username;
    private String email;
    private String password;

}
