package com.quiz.quiz_backend.service;

import com.quiz.quiz_backend.dto.OverallPerformanceResponse;
import com.quiz.quiz_backend.dto.StudentResultResponse;
import com.quiz.quiz_backend.entity.QuizAttempt;
import com.quiz.quiz_backend.entity.User;
import com.quiz.quiz_backend.repository.QuizAttemptRepository;
import com.quiz.quiz_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentResultService {

        private final QuizAttemptRepository quizAttemptRepository;
        private final UserRepository userRepository;

        @Transactional(readOnly = true)
        @Cacheable(value = "results")
        public OverallPerformanceResponse getOverallPerformance() {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                User student = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("Student not found"));

                List<QuizAttempt> attempts = quizAttemptRepository.findByStudentId(student.getId());

                List<StudentResultResponse> results = attempts.stream()
                                .map(this::convertToResultResponse)
                                .collect(Collectors.toList());

                double averageScore = attempts.stream()
                                .mapToDouble(QuizAttempt::getPercentage)
                                .average()
                                .orElse(0.0);

                double bestScore = attempts.stream()
                                .mapToDouble(QuizAttempt::getPercentage)
                                .max()
                                .orElse(0.0);

                OverallPerformanceResponse response = new OverallPerformanceResponse();
                response.setTotalQuizzesAttempted(attempts.size());
                response.setAverageScore(averageScore);
                response.setBestScore(bestScore);
                response.setResults(results);

                return response;
        }

        private StudentResultResponse convertToResultResponse(QuizAttempt attempt) {
                StudentResultResponse response = new StudentResultResponse();
                response.setQuizId(attempt.getQuiz().getId());
                response.setQuizTitle(attempt.getQuiz().getTitle());
                response.setSubjectName(attempt.getQuiz().getSubject().getName());
                response.setTopicName(attempt.getQuiz().getTopic().getName());
                response.setQuizDate(LocalDate.from(attempt.getQuiz().getScheduledDate()));
                response.setTotalMarks(attempt.getQuiz().getTotalMarks());
                response.setObtainedMarks(attempt.getObtainedMarks());
                response.setPercentage(attempt.getPercentage());
                response.setStatus(attempt.getStatus().name());
                return response;
        }

        @Transactional(readOnly = true)
        @Cacheable(value = "results")
        public OverallPerformanceResponse getAllStudentResultsForAdmin() {

                List<QuizAttempt> allAttempts = quizAttemptRepository.findAll();

                List<StudentResultResponse> results = allAttempts.stream()
                                .map(this::convertToAdminResultResponse)
                                .collect(Collectors.toList());

                double globalAverage = allAttempts.stream()
                                .mapToDouble(QuizAttempt::getPercentage)
                                .average()
                                .orElse(0.0);

                double highestScore = allAttempts.stream()
                                .mapToDouble(QuizAttempt::getPercentage)
                                .max()
                                .orElse(0.0);

                OverallPerformanceResponse response = new OverallPerformanceResponse();
                response.setTotalQuizzesAttempted(allAttempts.size());
                response.setAverageScore(globalAverage);
                response.setBestScore(highestScore);
                response.setResults(results);

                return response;

        }

        private StudentResultResponse convertToAdminResultResponse(QuizAttempt attempt) {
                StudentResultResponse response = convertToResultResponse(attempt);
                if (attempt.getStudent() != null) {
                        response.setStudentName(attempt.getStudent().getFullName());
                        response.setStudentEmail(attempt.getStudent().getEmail());
                }

                return response;
        }
}