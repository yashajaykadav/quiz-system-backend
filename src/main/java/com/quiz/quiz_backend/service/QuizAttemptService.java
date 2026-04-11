package com.quiz.quiz_backend.service;

import com.quiz.quiz_backend.dto.QuizAttemptResponse;
import com.quiz.quiz_backend.dto.StudentAnswerResponse;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public QuizAttemptResponse startQuiz(Long quizId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getScheduledDate().toLocalDate().equals(LocalDate.now())) {
            throw new RuntimeException("Quiz not available today");
        }

        Optional<QuizAttempt> completed = quizAttemptRepository.findByQuizIdAndStudentIdAndStatus(quizId, student.getId(), AttemptStatus.COMPLETED);
        if (completed.isPresent()) {
            throw new RuntimeException("Quiz already completed. Multiple attempts are not allowed.");
        }

        Optional<QuizAttempt> inProgress = quizAttemptRepository.findByQuizIdAndStudentIdAndStatus(quizId, student.getId(), AttemptStatus.IN_PROGRESS);
        if (inProgress.isPresent()) {
            return toResponse(inProgress.get());
        }

        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .student(student)
                .startTime(LocalDateTime.now())
                .status(AttemptStatus.IN_PROGRESS)
                .totalMarks(quiz.getTotalMarks())
                .studentAnswers(new ArrayList<>())
                .build();

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);

        for (Question question : quiz.getQuestions()) {
            StudentAnswer answer = StudentAnswer.builder()
                    .quizAttempt(savedAttempt)
                    .question(question)
                    .attempted(false)
                    .isCorrect(false)
                    .build();
            studentAnswerRepository.save(answer);
        }

        return toResponse(savedAttempt);
    }

    @Transactional
    public QuizAttemptResponse recordWarning(Long attemptId) {
//... (no changes in this block, continuing to end of file to safely replace)

        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found"));

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new RuntimeException("Quiz already completed");
        }

        attempt.setWarningCount(attempt.getWarningCount() + 1);

        if (attempt.getWarningCount() >= 3) {
            submitAttemptLogic(attempt);
        }

        return toResponse(quizAttemptRepository.save(attempt));
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
    public QuizAttemptResponse submitQuiz(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found"));

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new RuntimeException("Quiz already completed");
        }

        submitAttemptLogic(attempt);
        return toResponse(quizAttemptRepository.save(attempt));
    }

    public QuizAttemptResponse getAttemptById(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
        return toResponse(attempt);
    }

    public List<StudentAnswerResponse> getAttemptAnswers(Long attemptId) {
        List<StudentAnswer> answers = studentAnswerRepository.findByQuizAttemptId(attemptId);

        return answers.stream().map(answer -> StudentAnswerResponse.builder()
                .id(answer.getId())
                .questionId(answer.getQuestion().getId())
                .questionText(answer.getQuestion().getQuestionText())
                .codeSnippet(answer.getQuestion().getCodeSnippet())
                .type(answer.getQuestion().getType() != null ? answer.getQuestion().getType().name() : null)
                .option1(answer.getQuestion().getOption1())
                .option2(answer.getQuestion().getOption2())
                .option3(answer.getQuestion().getOption3())
                .option4(answer.getQuestion().getOption4())
                .marks(answer.getQuestion().getMarks())
                .selectedOption(answer.getSelectedOption())
                .isCorrect(answer.getIsCorrect())
                .attempted(answer.getAttempted())
                .build()
        ).toList();
    }

    private void submitAttemptLogic(QuizAttempt attempt) {
        List<StudentAnswer> answers = studentAnswerRepository.findByQuizAttemptId(attempt.getId());
        
        int obtainedMarks = answers.stream()
                .filter(StudentAnswer::getIsCorrect)
                .mapToInt(ans -> ans.getQuestion().getMarks() != null ? ans.getQuestion().getMarks() : 1)
                .sum();

        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setObtainedMarks(obtainedMarks);

        if (attempt.getTotalMarks() != null && attempt.getTotalMarks() > 0) {
            attempt.setPercentage((obtainedMarks * 100.0) / attempt.getTotalMarks());
        } else {
            attempt.setPercentage(0.0);
        }

        attempt.setStatus(AttemptStatus.COMPLETED);
        if (attempt.getWarningCount() >= 3) {
            attempt.setAutoSubmitted(true);
        }
    }

    private QuizAttemptResponse toResponse(QuizAttempt attempt) {
        QuizAttemptResponse dto = new QuizAttemptResponse();
        dto.setId(attempt.getId());
        dto.setQuizId(attempt.getQuiz().getId());
        dto.setQuizTitle(attempt.getQuiz().getTitle());
        dto.setSubjectName(attempt.getQuiz().getSubject().getName());
        dto.setTopicName(attempt.getQuiz().getTopic().getName());
        dto.setStartTime(attempt.getStartTime());
        dto.setEndTime(attempt.getSubmittedAt());
        dto.setObtainedMarks(attempt.getObtainedMarks());
        dto.setTotalMarks(attempt.getTotalMarks());
        dto.setPercentage(attempt.getPercentage());
        dto.setStatus(attempt.getStatus());
        dto.setAutoSubmitted(attempt.getAutoSubmitted());
        dto.setWarningCount(attempt.getWarningCount());
        dto.setDurationMinutes(attempt.getQuiz().getDurationMinutes());
        dto.setTotalQuestions(attempt.getQuiz().getQuestions() != null ? attempt.getQuiz().getQuestions().size() : 0);
        return dto;
    }
}