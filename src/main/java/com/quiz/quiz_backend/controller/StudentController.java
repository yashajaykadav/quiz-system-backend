package com.quiz.quiz_backend.controller;

import com.quiz.quiz_backend.dto.OverallPerformanceResponse;
import com.quiz.quiz_backend.dto.QuizAttemptResponse;
import com.quiz.quiz_backend.dto.QuizResponse;
import com.quiz.quiz_backend.dto.StudentAnswerResponse;
import com.quiz.quiz_backend.dto.SubmitAnswerRequest;
import com.quiz.quiz_backend.service.QuizAttemptService;
import com.quiz.quiz_backend.service.QuizService;
import com.quiz.quiz_backend.service.StudentResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final QuizService quizService;
    private final QuizAttemptService quizAttemptService;
    private final StudentResultService studentResultService;

    // View Today's Quizzes
    @GetMapping("/quizzes/today")
    public ResponseEntity<List<QuizResponse>> getTodayQuizzes() {
        return ResponseEntity.ok(quizService.getTodayQuizzes());
    }

    // Start Quiz — returns raw entity (frontend reads quiz.title, quiz.subject etc. for quizMeta)
    @PostMapping("/quizzes/{quizId}/start")
    public ResponseEntity<QuizAttemptResponse> startQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizAttemptService.startQuiz(quizId));
    }

    // Submit Answer
    @PostMapping("/attempts/{attemptId}/answer")
    public ResponseEntity<String> submitAnswer(
            @PathVariable Long attemptId,
            @RequestBody SubmitAnswerRequest request) {
        quizAttemptService.submitAnswer(attemptId, request);
        return ResponseEntity.ok("Answer submitted");
    }

    // Get Attempt Details
    @GetMapping("/attempts/{attemptId}")
    public ResponseEntity<QuizAttemptResponse> getAttempt(@PathVariable Long attemptId) {
        return ResponseEntity.ok(quizAttemptService.getAttemptById(attemptId));
    }

    // Get All Answers for Question Navigation
    @GetMapping("/attempts/{attemptId}/answers")
    public ResponseEntity<List<StudentAnswerResponse>> getAttemptAnswers(@PathVariable Long attemptId) {
        return ResponseEntity.ok(quizAttemptService.getAttemptAnswers(attemptId));
    }

    // Record Tab-Switch Warning
    @PostMapping("/attempts/{attemptId}/warning")
    public ResponseEntity<QuizAttemptResponse> recordWarning(@PathVariable Long attemptId) {
        return ResponseEntity.ok(quizAttemptService.recordWarning(attemptId));
    }

    // Submit Quiz
    @PostMapping("/attempts/{attemptId}/submit")
    public ResponseEntity<QuizAttemptResponse> submitQuiz(@PathVariable Long attemptId) {
        return ResponseEntity.ok(quizAttemptService.submitQuiz(attemptId));
    }

    // Overall Performance
    @GetMapping("/performance")
    public ResponseEntity<OverallPerformanceResponse> getOverallPerformance() {
        return ResponseEntity.ok(studentResultService.getOverallPerformance());
    }
}