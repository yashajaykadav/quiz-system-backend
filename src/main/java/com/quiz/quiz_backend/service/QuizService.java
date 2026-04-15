package com.quiz.quiz_backend.service;

import com.quiz.quiz_backend.dto.QuizRequest;
import com.quiz.quiz_backend.dto.QuizResponse;
import com.quiz.quiz_backend.entity.*;
import com.quiz.quiz_backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @CacheEvict(value = "quizzes", allEntries = true) // FIX: Evict cache when a new quiz is created
    public QuizResponse createQuiz(QuizRequest request) {
        if (request.getScheduledDate() == null) {
            throw new RuntimeException("Scheduled date is required");
        }

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
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

        int totalMarks = questions.stream()
                .mapToInt(q -> q.getMarks() != null ? q.getMarks() : 1)
                .sum();

        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .subject(subject)
                .topic(topic)
                .questions(questions)
                .durationMinutes(request.getDurationMinutes())
                .scheduledDate(request.getScheduledDate())
                .totalMarks(totalMarks)
                .createdBy(admin)
                .build();

        quiz = quizRepository.save(quiz);
        return toQuizResponse(quiz);
    }

    @Transactional
    public List<QuizResponse> getTodayQuizzes() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return quizRepository
                .findByScheduledDateBetween(startOfDay, endOfDay)
                .stream()
                .map(this::toQuizResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    @Transactional
    @Cacheable(value = "quizzes")
    public List<QuizResponse> getAllQuizzes() {
        return quizRepository.findAll().stream()
                .map(this::toQuizResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "quizzes", allEntries = true)
    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new RuntimeException("Quiz not found");
        }
        quizRepository.deleteById(id);
    }

    public QuizResponse toQuizResponse(Quiz quiz) {
        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setTitle(quiz.getTitle());
        response.setDescription(quiz.getDescription());
        response.setSubjectName(quiz.getSubject() != null ? quiz.getSubject().getName() : null);
        response.setTopicName(quiz.getTopic() != null ? quiz.getTopic().getName() : null);
        response.setDurationMinutes(quiz.getDurationMinutes());
        response.setScheduledDate(quiz.getScheduledDate());
        response.setTotalMarks(quiz.getTotalMarks());
        response.setTotalQuestions(quiz.getQuestions() != null ? quiz.getQuestions().size() : 0);
        return response;
    }
}