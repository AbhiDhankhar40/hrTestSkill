package com.test.demo.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.demo.model.GroupMaster;
import com.test.demo.service.GroupMasterService;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupMasterController {

    private final GroupMasterService service;

    @PostMapping("/add")
    public ResponseEntity<GroupMaster> createGroup(
            @RequestBody GroupMaster group) {

        return ResponseEntity.ok(service.createGroup(group));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupMaster> updateGroup(
            @PathVariable Long id,
            @RequestBody GroupMaster group) {

        return ResponseEntity.ok(service.updateGroup(id, group));
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<GroupMaster> getGroupById(
//            @PathVariable Long id) {
//
//        return ResponseEntity.ok(service.getGroupById(id));
//    }

    @GetMapping("/getAll")
    public ResponseEntity<List<GroupMaster>> getAllGroups() {

        return ResponseEntity.ok(service.getAllGroups());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGroup(@PathVariable Long id) {

        service.deleteGroup(id);
        return ResponseEntity.ok("Group deleted successfully");
    }
}
