package com.test.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.demo.model.ModuleMaster;
import com.test.demo.service.ModuleMasterService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleMasterController {

    private final ModuleMasterService service;

    @PostMapping("/add")
    public ResponseEntity<ModuleMaster> createModule(@RequestBody ModuleMaster module) {
        return ResponseEntity.ok(service.createModule(module));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuleMaster> updateModule(
            @PathVariable Long id,
            @RequestBody ModuleMaster module) {
        return ResponseEntity.ok(service.updateModule(id, module));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuleMaster> getModule(@PathVariable Long id) {
        return ResponseEntity.ok(service.getModuleById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<List<ModuleMaster>> getActiveModules() {
        return ResponseEntity.ok(service.getAllActiveModules());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteModule(@PathVariable Long id) {
        service.deleteModule(id);
        return ResponseEntity.ok("Module deleted successfully");
    }
}
