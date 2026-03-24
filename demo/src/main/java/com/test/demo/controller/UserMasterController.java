package com.test.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.test.demo.model.UserMaster;
import com.test.demo.service.UserMasterService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserMasterController {

    private final UserMasterService service;

    @PostMapping
    public ResponseEntity<UserMaster> createUser(
            @RequestBody UserMaster user) {

        return ResponseEntity.ok(service.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserMaster> updateUser(
            @PathVariable Long id,
            @RequestBody UserMaster user) {

        return ResponseEntity.ok(service.updateUser(id, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserMaster> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserMaster>> getAllUsers() {

        return ResponseEntity.ok(service.getAllUsers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id) {

        service.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
