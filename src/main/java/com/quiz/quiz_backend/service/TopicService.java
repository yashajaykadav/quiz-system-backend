package com.quiz.quiz_backend.service;

import com.quiz.quiz_backend.dto.TopicRequest;
import com.quiz.quiz_backend.entity.Subject;
import com.quiz.quiz_backend.entity.Topic;
import com.quiz.quiz_backend.repository.SubjectRepository;
import com.quiz.quiz_backend.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;

    public Topic createTopic(TopicRequest request){
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(()-> new RuntimeException("Subject Not Found"));

        Topic topic = Topic.builder()
                .name(request.getName())
                .description(request.getDescription())
                .subject(subject)
                .build();

        return topicRepository.save(topic);
    }

    public List<Topic> getAllTopics(){
        return topicRepository.findAll();
    }
    public List<Topic>getTopicsBySubject(Long subjectId){
        return  topicRepository.findBySubjectId(subjectId);
    }
    public void deleteTopic(Long id){
        topicRepository.deleteById(id);
    }
}
