package com.quiz.quiz_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ContactRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    private String type; // PASSWORD_RESET, ACCOUNT_ISSUE, etc.

    @Column(length = 1000)
    private String message;

    private String status = "PENDING"; // PENDING, RESOLVED

    private LocalDateTime createdAt = LocalDateTime.now();

    // getters & setters
}