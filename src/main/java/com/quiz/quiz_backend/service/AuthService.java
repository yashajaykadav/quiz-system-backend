package com.quiz.quiz_backend.service;

import com.quiz.quiz_backend.dto.ChangePasswordRequest;
import com.quiz.quiz_backend.dto.LoginRequest;
import com.quiz.quiz_backend.dto.LoginResponse;
import com.quiz.quiz_backend.entity.User;
import com.quiz.quiz_backend.exceptions.InvalidCredentialsException;
import com.quiz.quiz_backend.exceptions.ResourceNotFoundException;
import com.quiz.quiz_backend.repository.UserRepository;
import com.quiz.quiz_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        // 1. Authenticate using Spring Security
        // If this fails, Spring Security throws BadCredentialsException automatically
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Fetch User with Custom Exception
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + request.getUsername()));

        // 3. Professional Practice: Update login metadata
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // 4. Token Generation
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());

        return new LoginResponse(token, user.getUsername(), user.getRole().name(), user.getFullName());
    }

    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 5. Explicit Business Logic Exception
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password provided is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}