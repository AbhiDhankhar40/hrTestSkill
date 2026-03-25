package com.test.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.test.demo.model.UserGroupMapping;
import com.test.demo.service.UserGroupMappingService;

import java.util.List;

@RestController
@RequestMapping("/api/user-group")
@RequiredArgsConstructor
public class UserGroupMappingController {

    private final UserGroupMappingService service;

    // Assign user to group
    @PostMapping("/assign")
    public ResponseEntity<UserGroupMapping> assignUser(
            @RequestParam Long userId,
            @RequestParam Long groupId) {

        return ResponseEntity.ok(
                service.assignUserToGroup(userId, groupId)
        );
    }

    // Remove user from group
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeUser(
            @RequestParam Long userId,
            @RequestParam Long groupId) {

        service.removeUserFromGroup(userId, groupId);
        return ResponseEntity.ok("User removed from group successfully");
    }

    // Get all groups of a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserGroupMapping>> getGroupsByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                service.getGroupsByUser(userId)
        );
    }

    // Get all users of a group
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<UserGroupMapping>> getUsersByGroup(
            @PathVariable Long groupId) {

        return ResponseEntity.ok(
                service.getUsersByGroup(groupId)
        );
    }
}
