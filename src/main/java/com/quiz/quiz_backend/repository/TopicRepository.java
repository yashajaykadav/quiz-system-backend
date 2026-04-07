package com.quiz.quiz_backend.repository;

import com.quiz.quiz_backend.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic,Long> {
    List<Topic> findBySubjectId(Long subId);
}
