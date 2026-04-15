package com.quiz.quiz_backend.service;

import com.quiz.quiz_backend.dto.TopicRequest;
import com.quiz.quiz_backend.dto.TopicResponse;
import com.quiz.quiz_backend.entity.Subject;
import com.quiz.quiz_backend.entity.Topic;
import com.quiz.quiz_backend.repository.SubjectRepository;
import com.quiz.quiz_backend.repository.TopicRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    @CacheEvict(value="topics" , allEntries = true)
    public TopicResponse createTopic(TopicRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject Not Found"));

        Topic topic = Topic.builder()
                .name(request.getName())
                .description(request.getDescription())
                .subject(subject)
                .build();

        topic = topicRepository.save(topic);
        return toTopicResponse(topic);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "topics" ,key = "'all'")
    public List<TopicResponse> getAllTopics() {
        return topicRepository.findAll().stream()
                .map(this::toTopicResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "topics", key = "#subjectId")
    public List<TopicResponse> getTopicsBySubject(Long subjectId) {
        return topicRepository.findBySubjectId(subjectId).stream()
                .map(this::toTopicResponse)
                .toList();
    }

    @CacheEvict(value="topics" , allEntries = true)
    public void deleteTopic(Long id) {
        topicRepository.deleteById(id);
    }

    private TopicResponse toTopicResponse(Topic t) {
        TopicResponse r = new TopicResponse();
        r.setId(t.getId());
        r.setName(t.getName());
        r.setDescription(t.getDescription());
        if (t.getSubject() != null) {
            r.setSubjectId(t.getSubject().getId());
            r.setSubjectName(t.getSubject().getName());
        }
        return r;
    }

    public long getCount() {
        return topicRepository.count();
    }
}
