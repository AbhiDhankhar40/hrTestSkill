package com.test.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.test.demo.model.GroupPermissionMapping;
import com.test.demo.service.GroupPermissionService;

import java.util.List;

@RestController
@RequestMapping("/api/group-permissions")
@RequiredArgsConstructor
public class GroupPermissionController {

    private final GroupPermissionService service;

    // Assign multiple permissions to a group
    @PostMapping("/{groupId}")
    public ResponseEntity<String> assignPermissions(
            @PathVariable Long groupId,
            @RequestBody List<Long> permissionIds) {

        service.assignPermissions(groupId, permissionIds);
        return ResponseEntity.ok("Permissions assigned successfully");
    }

    // Get all permissions assigned to a group
    @GetMapping("/{groupId}")
    public ResponseEntity<List<GroupPermissionMapping>> getPermissionsByGroup(
            @PathVariable Long groupId) {

        return ResponseEntity.ok(
                service.getPermissionsByGroup(groupId));
    }

    // Remove a specific permission from a group
    @DeleteMapping("/{groupId}/{permissionId}")
    public ResponseEntity<String> removePermission(
            @PathVariable Long groupId,
            @PathVariable Long permissionId) {

        service.removePermission(groupId, permissionId);
        return ResponseEntity.ok("Permission removed successfully");
    }
}

