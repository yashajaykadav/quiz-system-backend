package com.quiz.quiz_backend.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.quiz.quiz_backend.dto.StudentRequest;
import com.quiz.quiz_backend.entity.Role;
import com.quiz.quiz_backend.entity.User;
import com.quiz.quiz_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public List<User> getStudents() {
        return userRepository.findByRole(Role.STUDENT);
    }

    public User saveStudent(StudentRequest request) {

        if (request.getUsername() == null || request.getPassword() == null) {
            throw new RuntimeException("Username or Password is null");
        }

        User user = new User();

        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);
        user.setActive(true);

        return userRepository.save(user);
    }

    public User ReseteStudentPassword(Long studId, String newPassword) {
        User user = userRepository.findById(studId)
                .orElseThrow(() -> new RuntimeException("Student not found!"));

        user.setPassword(encoder.encode(newPassword));
        return userRepository.save(user);
    }

    public User togglUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Student Not Found!"));

        user.setActive(!user.getActive());
        return userRepository.save(user);
    }
}
