package com.test.demo.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.demo.dto.AccessControlResponseDTO;
import com.test.demo.dto.GroupDTO;
import com.test.demo.dto.ModuleDTO;
import com.test.demo.model.UserMaster;
import com.test.demo.repository.UserMasterRepository;
import com.test.demo.service.AccessControlService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class UserLoginController {

    private final UserMasterRepository userMasterRepository;
    private final AccessControlService accessControlService;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (request == null || request.username() == null || request.username().isBlank()
                || request.password() == null || request.password().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "username and password are required"));
        }

        UserMaster user = userMasterRepository.findByUsername(request.username()).orElse(null);

        if (user == null || user.getPassword() == null || !user.getPassword().equals(request.password())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }

        AccessControlResponseDTO accessData = accessControlService.getUserAccessControl(user.getId());
        String token = generateToken(user.getId(), user.getUsername());

        return ResponseEntity.ok(
                new LoginResponse(
                        token,
                        "Bearer",
                        accessData.getUserId(),
                        accessData.getUsername(),
                        accessData.getName(),
                        accessData.getCreatedAt(),
                        accessData.getGroups(),
                        accessData.getModules()
                )
        );
    }

    private String generateToken(Long userId, String username) {

        String payload = userId + ":" + username + ":" + System.currentTimeMillis() + ":" + UUID.randomUUID();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    public record LoginRequest(String username, String password, String unique_id,
                               String latitude, String longitude, String ip_address,
                               String current_date, String current_time) {
    }

    public record LoginResponse(String token, String tokenType, Long userId, String username,
                                String name, LocalDateTime createdAt,
                                List<GroupDTO> groups, List<ModuleDTO> modules) {
    }
}
