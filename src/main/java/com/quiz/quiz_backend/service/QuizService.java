package com.quiz.quiz_backend.service;

import com.quiz.quiz_backend.dto.QuizRequest;
import com.quiz.quiz_backend.dto.QuizResponse;
import com.quiz.quiz_backend.entity.*;
import com.quiz.quiz_backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @Transactional
    public Quiz createQuiz(QuizRequest request) {

        // ✅ Validate datetime
        if (request.getScheduledDate() == null) {
            throw new RuntimeException("Scheduled date is required");
        }

        String userName = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User admin = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        List<Question> questions = questionRepository.findAllById(request.getQuestionIds());

        if (questions.size() != request.getQuestionIds().size()) {
            throw new RuntimeException("Some questions not found");
        }

        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .subject(subject)
                .topic(topic)
                .questions(questions)
                .durationMinutes(request.getDurationMinutes())
                .scheduledDate(request.getScheduledDate()) // ✅ correct
                .totalMarks(questions.size())
                .createdBy(admin)
                .build();

        return quizRepository.save(quiz);
    }

    public List<QuizResponse> getTodayQuizzes() {

        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();

        // ✅ FIXED (no missing edge cases)
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return quizRepository
                .findByScheduledDateBetween(startOfDay, endOfDay)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    private QuizResponse convertToResponse(Quiz quiz) {

        QuizResponse response = new QuizResponse();

        response.setId(quiz.getId());
        response.setTitle(quiz.getTitle());
        response.setDescription(quiz.getDescription());
        response.setSubjectName(quiz.getSubject().getName());
        response.setTopicName(quiz.getTopic().getName());
        response.setDurationMinutes(quiz.getDurationMinutes());

        // ✅ send full datetime
        response.setScheduledDate(quiz.getScheduledDate());

        response.setTotalMarks(quiz.getTotalMarks());
        response.setTotalQuestions(quiz.getQuestions().size());

        return response;
    }
}