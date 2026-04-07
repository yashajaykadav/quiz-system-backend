package com.quiz.quiz_backend.repository;

import com.quiz.quiz_backend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long> {
    List<Question> findBySubjectId(Long subId);
    List<Question> findByTopicId(Long topicId);
    List<Question> findBySubjectIdAndTopicId(Long subjectId ,Long questionId);
}
