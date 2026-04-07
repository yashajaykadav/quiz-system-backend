package com.quiz.quiz_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subjects")
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String name;

    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "subject",cascade = CascadeType.ALL)
    private List<Topic> topics;

    @Builder.Default
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt =  LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

}
