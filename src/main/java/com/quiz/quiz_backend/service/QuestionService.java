package com.quiz.quiz_backend.service;

import com.quiz.quiz_backend.dto.QuestionRequest;
import com.quiz.quiz_backend.entity.Question;
import com.quiz.quiz_backend.entity.Subject;
import com.quiz.quiz_backend.entity.Topic;
import com.quiz.quiz_backend.repository.QuestionRepository;
import com.quiz.quiz_backend.repository.SubjectRepository;
import com.quiz.quiz_backend.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;

    public Question createQuestion(QuestionRequest  request){
        Subject subject = subjectRepository.findById(request.getSubjectId()).
                orElseThrow(()->new RuntimeException("Subject not found"));
        Topic topic = topicRepository.findById(request.getTopicId()).orElseThrow(()->
                new RuntimeException("Topic not found"));

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

        return questionRepository.save(question);
    }

    public List<Question> getQuestionBySubjectAndTopic(Long subjectId, Long topicId){
        return questionRepository.findBySubjectIdAndTopicId(subjectId,topicId);
    }
    public List<Question> getAllQuestions(){
        return questionRepository.findAll();
    }
    public void deleteQuestion(Long questionId){
        questionRepository.deleteById(questionId);
    }
}
