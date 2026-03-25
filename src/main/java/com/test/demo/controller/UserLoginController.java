package com.test.demo.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.demo.model.UserMaster;
import com.test.demo.repository.UserMasterRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class UserLoginController {

    private final UserMasterRepository userMasterRepository;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (request == null || request.username() == null || request.username().isBlank() || request.password() == null
                || request.password().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "username and password are required"));
        }

        UserMaster user = userMasterRepository.findByUsername(request.username()).orElse(null);

        if (user == null || user.getPassword() == null || !user.getPassword().equals(request.password())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }

        return ResponseEntity.ok(new LoginResponse(user.getName(), user.getUsername(), "Active"));
    }

    public record LoginRequest(String username, String password) {}

    public record LoginResponse(String name, String username, String status) {}
}
