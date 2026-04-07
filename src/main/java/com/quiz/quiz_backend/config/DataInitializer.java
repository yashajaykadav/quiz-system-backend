package com.quiz.quiz_backend.config;

import com.quiz.quiz_backend.entity.Role;
import com.quiz.quiz_backend.entity.User;
import com.quiz.quiz_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin already exists to prevent duplicate failures
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("App Admin")
                    .email("admin@quiz.com")
                    .role(Role.ADMIN)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(admin);
            System.out.println("Admin user automatically created! Username: admin, Password: admin123");
        }
        // Create Student
        if (!userRepository.existsByUsername("student")) {
            User student = User.builder()
                    .username("student")
                    .password(passwordEncoder.encode("student123"))
                    .fullName("Test Student")
                    .email("student@quiz.com")
                    .role(Role.STUDENT) // Make sure this exists in your Role enum
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(student);
            System.out.println("✅ Student created: student / student123");
        }
    }
}
