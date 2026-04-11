package com.quiz.quiz_backend.service;

import com.quiz.quiz_backend.dto.QuestionRequest;
import com.quiz.quiz_backend.dto.QuestionResponse;
import com.quiz.quiz_backend.entity.Question;
import com.quiz.quiz_backend.entity.Subject;
import com.quiz.quiz_backend.entity.Topic;
import com.quiz.quiz_backend.repository.QuestionRepository;
import com.quiz.quiz_backend.repository.SubjectRepository;
import com.quiz.quiz_backend.repository.TopicRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public QuestionResponse createQuestion(QuestionRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        Question question = Question.builder()
                .questionText(request.getQuestionText())
                .codeSnippet(request.getCodeSnippet())
                .type(request.getType())
                .option1(request.getOption1())
                .option2(request.getOption2())
                .option3(request.getOption3())
                .option4(request.getOption4())
                .correctOption(request.getCorrectOption())
                .subject(subject)
                .topic(topic)
                .build();

        question = questionRepository.save(question);
        return toQuestionResponse(question);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "questions", key = "#subjectId + '-' + #topicId")
    public List<QuestionResponse> getQuestionBySubjectAndTopic(Long subjectId, Long topicId) {
        return questionRepository.findBySubjectIdAndTopicId(subjectId, topicId)
                .stream().map(this::toQuestionResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "questions")
    public List<QuestionResponse> getAllQuestions() {
        return questionRepository.findAll()
                .stream().map(this::toQuestionResponse).collect(Collectors.toList());
    }

    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }

    private QuestionResponse toQuestionResponse(Question q) {
        QuestionResponse r = new QuestionResponse();
        r.setId(q.getId());
        r.setQuestionText(q.getQuestionText());
        r.setCodeSnippet(q.getCodeSnippet());
        r.setType(q.getType() != null ? q.getType().name() : null);
        r.setOption1(q.getOption1());
        r.setOption2(q.getOption2());
        r.setOption3(q.getOption3());
        r.setOption4(q.getOption4());
        r.setCorrectOption(q.getCorrectOption());
        r.setMarks(q.getMarks());
        if (q.getSubject() != null) {
            r.setSubjectId(q.getSubject().getId());
            r.setSubjectName(q.getSubject().getName());
        }
        if (q.getTopic() != null) {
            r.setTopicId(q.getTopic().getId());
            r.setTopicName(q.getTopic().getName());
        }
        return r;
    }

    public long getCount() {
        return questionRepository.count();
    }
}
