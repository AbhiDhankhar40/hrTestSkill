package com.test.demo.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.test.demo.model.PermissionMaster;
import com.test.demo.service.PermissionMasterService;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionMasterController {

    private final PermissionMasterService service;

    @PostMapping("/add")
    public ResponseEntity<PermissionMaster> createPermission(
            @RequestBody PermissionMaster permission) {

        return ResponseEntity.ok(service.createPermission(permission));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionMaster> updatePermission(
            @PathVariable Long id,
            @RequestBody PermissionMaster permission) {

        return ResponseEntity.ok(service.updatePermission(id, permission));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionMaster> getPermissionById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getPermissionById(id));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<PermissionMaster>> getAllPermissions() {

        return ResponseEntity.ok(service.getAllPermissions());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePermission(@PathVariable Long id) {

        service.deletePermission(id);
        return ResponseEntity.ok("Permission deleted successfully");
    }
}
