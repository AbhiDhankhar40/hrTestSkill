package com.test.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.demo.dto.AccessControlResponseDTO;
import com.test.demo.service.AccessControlService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AccessControlController {

    private final AccessControlService accessControlService;

    @GetMapping("/{userId}/access-control")
    public ResponseEntity<AccessControlResponseDTO> getAccessControl(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                accessControlService.getUserAccessControl(userId)
        );
    }
}