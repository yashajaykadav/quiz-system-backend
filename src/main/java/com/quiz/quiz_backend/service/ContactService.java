package com.quiz.quiz_backend.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quiz.quiz_backend.entity.ContactRequest;
import com.quiz.quiz_backend.repository.ContactRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // Using this for cleaner injection like your other services
public class ContactService {

    private final ContactRequestRepository contactRequestRepository;

    @Transactional
    @CacheEvict(value = "contactRequests", allEntries = true)
    public ContactRequest save(ContactRequest contactRequest) {
        return contactRequestRepository.save(contactRequest);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "contactRequests", key = "'all'")
    public List<ContactRequest> getAll() {
        return contactRequestRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = "contactRequests", allEntries = true)
    public void markResolved(Long id) {
        ContactRequest request = contactRequestRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Request not found"));

        request.setStatus("RESOLVED");
        contactRequestRepository.save(request); // Database updates, Cache is wiped
    }

    @Transactional
    @CacheEvict(value = "contactRequests", allEntries = true)
    public void delete(Long id) {
        if (!contactRequestRepository.existsById(id)) {
            throw new RuntimeException("Request not found");
        }
        contactRequestRepository.deleteById(id);
    }

}
