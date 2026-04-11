package com.quiz.quiz_backend.controller;

import com.quiz.quiz_backend.dto.*;
import com.quiz.quiz_backend.entity.*;
import com.quiz.quiz_backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SubjectService subjectService;
    private final TopicService topicService;
    private final QuestionService questionService;
    private final QuizService quizService;
    private final UserService userService;
    private final StudentResultService studentResultService;

    // ── Students ──────────────────────────────────────────────────────────
    @GetMapping("/students")
    public ResponseEntity<List<User>> getAllStudents() {
        return ResponseEntity.ok(userService.getStudents());
    }

    @PostMapping("/students")
    public ResponseEntity<User> addStudent(@RequestBody StudentRequest request) {
        return ResponseEntity.ok(userService.saveStudent(request));
    }

    @PatchMapping("/students/{id}/reset-password")
    public ResponseEntity<User> adminResetPassword(@PathVariable Long id, @RequestBody String newPassword) {
        return ResponseEntity.ok(userService.ReseteStudentPassword(id, newPassword));
    }

    @PatchMapping("/students/{id}/toggle-status")
    public ResponseEntity<User> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userService.togglUserStatus(id));
    }

    // ── Subjects ──────────────────────────────────────────────────────────
    @PostMapping("/subjects")
    public ResponseEntity<Subject> createSubject(@RequestBody SubjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.createSubject(request));
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<String> deleteSubject(@PathVariable Long id) {
        subjectService.deleteBySubjectId(id);
        return ResponseEntity.ok("Subject deleted successfully");
    }

    // ── Topics ────────────────────────────────────────────────────────────
    @PostMapping("/topics")
    public ResponseEntity<TopicResponse> createTopic(@RequestBody TopicRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(topicService.createTopic(request));
    }

    @GetMapping("/topics")
    public ResponseEntity<List<TopicResponse>> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    @GetMapping("/topics/subject/{subjectId}")
    public ResponseEntity<List<TopicResponse>> getTopicsBySubject(@PathVariable Long subjectId) {
        return ResponseEntity.ok(topicService.getTopicsBySubject(subjectId));
    }

    @DeleteMapping("/topics/{id}")
    public ResponseEntity<String> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.ok("Topic deleted successfully");
    }

    // ── Questions ─────────────────────────────────────────────────────────
    @PostMapping("/questions")
    public ResponseEntity<QuestionResponse> createQuestion(@RequestBody QuestionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(request));
    }

    @GetMapping("/questions")
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping("/questions/filter")
    public ResponseEntity<List<QuestionResponse>> getQuestionsBySubjectAndTopic(
            @RequestParam Long subjectId,
            @RequestParam Long topicId) {
        return ResponseEntity.ok(questionService.getQuestionBySubjectAndTopic(subjectId, topicId));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok("Question deleted successfully");
    }

    // ── Quizzes ───────────────────────────────────────────────────────────
    @PostMapping("/quizzes")
    public ResponseEntity<QuizResponse> createQuiz(@RequestBody QuizRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.createQuiz(request));
    }

    @GetMapping("/quizzes")
    public ResponseEntity<List<QuizResponse>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    // ── Results ───────────────────────────────────────────────────────────
    @GetMapping("/results/all")
    public ResponseEntity<List<StudentResultResponse>> getAllResults() {
        return ResponseEntity.ok(studentResultService.getAllStudentResultsForAdmin().getResults());
    }
}