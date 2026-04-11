package com.quiz.quiz_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.quiz.quiz_backend.entity.ContactRequest;
import com.quiz.quiz_backend.repository.ContactRequestRepository;

@Service
public class ContactService {

    private final ContactRequestRepository contactRequestRepository;

    public ContactService(ContactRequestRepository contactRequestRepository) {
        this.contactRequestRepository = contactRequestRepository;
    }

    public ContactRequest save(ContactRequest contactRequest) {
        return contactRequestRepository.save(contactRequest);
    }

    public List<ContactRequest> getAll() {
        return contactRequestRepository.findAll();
    }

    public void markResolved(Long id) {
        ContactRequest request = contactRequestRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Request not found"));
        request.setStatus("RESOLVED");
        contactRequestRepository.save(request);
    }

}
