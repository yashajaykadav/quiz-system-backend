package com.quiz.quiz_backend.service;

import com.quiz.quiz_backend.dto.SubjectRequest;
import com.quiz.quiz_backend.entity.Subject;
import com.quiz.quiz_backend.entity.User;
import com.quiz.quiz_backend.repository.SubjectRepository;
import com.quiz.quiz_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @Transactional
    @CacheEvict(value = "subjects", allEntries = true)
    public Subject createSubject(SubjectRequest request) {
        if (subjectRepository.existsByName(request.getName())) {
            throw new RuntimeException("Subject Already Exists");
        }
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin Not Found"));

        Subject subject = Subject.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(admin)
                .build();
                
        return subjectRepository.save(subject);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "subjects", key = "'all'")
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "subjects", key = "#id")
    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject Not Found"));
    }

    @Transactional
    @CacheEvict(value = "subjects", allEntries = true)
    public void deleteBySubjectId(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new RuntimeException("Subject Not Found");
        }
        subjectRepository.deleteById(id);
    }

    public long getCount() {
        return subjectRepository.count();
    }
}