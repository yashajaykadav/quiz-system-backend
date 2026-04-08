package com.quiz.quiz_backend.service;

import com.quiz.quiz_backend.dto.SubmitAnswerRequest;
import com.quiz.quiz_backend.entity.*;
import com.quiz.quiz_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public QuizAttempt startQuiz(Long quizId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Check for existing attempts
        List<QuizAttempt> existingAttempts = quizAttemptRepository.findByQuizIdAndStudentId(quizId, student.getId());

        for (QuizAttempt existing : existingAttempts) {
            if (existing.getStatus() == AttemptStatus.IN_PROGRESS) {
                return existing; // Resume
            }
            if (existing.getStatus() == AttemptStatus.COMPLETED) {
                throw new RuntimeException("Quiz already completed. Multiple attempts are not allowed.");
            }
        }

        // Check if quiz is scheduled for today
        if (!quiz.getScheduledDate().toLocalDate().equals(LocalDate.now())) {
            throw new RuntimeException("Quiz not available today");
        }

        // ✅ FIXED: Added .totalMarks(quiz.getTotalMarks()) to the builder
        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .student(student)
                .startTime(LocalDateTime.now())
                .status(AttemptStatus.IN_PROGRESS)
                .totalMarks(quiz.getTotalMarks()) // <--- This line fixes the "Column cannot be null" error
                .studentAnswers(new ArrayList<>())
                .build();

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);

        // Initialize student answers
        for (Question question : quiz.getQuestions()) {
            StudentAnswer answer = StudentAnswer.builder()
                    .quizAttempt(savedAttempt)
                    .question(question)
                    .attempted(false)
                    .isCorrect(false)
                    .build();
            studentAnswerRepository.save(answer);
        }

        return savedAttempt;
    }

    @Transactional
    public QuizAttempt recordWarning(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found"));

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new RuntimeException("Quiz already completed");
        }

        attempt.setWarningCount(attempt.getWarningCount() + 1);

        // Auto-submit after 3 warnings
        if (attempt.getWarningCount() >= 3) {
            List<StudentAnswer> answers = studentAnswerRepository.findByQuizAttemptId(attemptId);
            int obtainedMarks = (int) answers.stream()
                    .filter(StudentAnswer::getIsCorrect)
                    .count();

            attempt.setSubmittedAt(LocalDateTime.now());
            attempt.setObtainedMarks(obtainedMarks);
            
            // ✅ IMPROVED: Using attempt's own totalMarks
            if (attempt.getTotalMarks() != null && attempt.getTotalMarks() > 0) {
                attempt.setPercentage((obtainedMarks * 100.0) / attempt.getTotalMarks());
            }
            
            attempt.setStatus(AttemptStatus.COMPLETED);
            attempt.setAutoSubmitted(true);
        }

        return quizAttemptRepository.save(attempt);
    }

    @Transactional
    public void submitAnswer(Long attemptId, SubmitAnswerRequest request) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found"));

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new RuntimeException("Quiz already completed");
        }

        StudentAnswer answer = studentAnswerRepository
                .findByQuizAttemptIdAndQuestionId(attemptId, request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Answer record not found"));

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        answer.setSelectedOption(request.getSelectedOption());
        answer.setAttempted(true);
        answer.setIsCorrect(question.getCorrectOption().equals(request.getSelectedOption()));

        studentAnswerRepository.save(answer);
    }

    @Transactional
    public QuizAttempt submitQuiz(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found"));

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new RuntimeException("Quiz already completed");
        }

        // Calculate marks
        List<StudentAnswer> answers = studentAnswerRepository.findByQuizAttemptId(attemptId);
        int obtainedMarks = (int) answers.stream()
                .filter(StudentAnswer::getIsCorrect)
                .count();

        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setObtainedMarks(obtainedMarks);

        // ✅ IMPROVED: Using attempt's own totalMarks
        if (attempt.getTotalMarks() != null && attempt.getTotalMarks() > 0) {
            attempt.setPercentage((obtainedMarks * 100.0) / attempt.getTotalMarks());
        } else {
            attempt.setPercentage(0.0);
        }

        attempt.setStatus(AttemptStatus.COMPLETED);

        return quizAttemptRepository.save(attempt);
    }

    public QuizAttempt getAttemptById(Long attemptId) {
        return quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
    }

    public List<StudentAnswer> getAttemptAnswers(Long attemptId) {
        return studentAnswerRepository.findByQuizAttemptId(attemptId);
    }
}