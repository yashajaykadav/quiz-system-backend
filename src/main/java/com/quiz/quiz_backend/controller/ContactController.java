package com.quiz.quiz_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.quiz_backend.entity.ContactRequest;
import com.quiz.quiz_backend.service.ContactService;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/contact")
    public ContactRequest submitContactRequest(@RequestBody ContactRequest contactRequest) {
        return contactService.save(contactRequest);
    }

    @GetMapping("/admin/contact")
    public List<ContactRequest> getAllContactRequests() {
        return contactService.getAll();
    }

    @PatchMapping("/admin/contact/{id}/resolve")
    public void resolve(@PathVariable Long id) {
        contactService.markResolved(id);
    }

    @DeleteMapping("/admin/contact/{id}")
    public void delete(@PathVariable Long id) {
        contactService.delete(id);
    }
}
