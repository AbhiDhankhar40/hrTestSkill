package com.test.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.test.demo.model.ButtonMaster;
import com.test.demo.model.SubModuleMaster;
import com.test.demo.service.ButtonMasterService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/buttons")
@RequiredArgsConstructor
public class ButtonMasterController {

    private final ButtonMasterService service;

    @PostMapping("/add")
    public ResponseEntity<ButtonMaster> createButton(@RequestBody ButtonRequest request) {
        return ResponseEntity.ok(service.createButton(toEntity(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ButtonMaster> updateButton(
            @PathVariable Long id,
            @RequestBody ButtonRequest request) {
        return ResponseEntity.ok(service.updateButton(id, toEntity(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ButtonMaster> getButtonById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getButtonById(id));
    }

    @GetMapping("getAll")
    public ResponseEntity<List<ButtonMaster>> getAllButtons() {
        return ResponseEntity.ok(service.getAllButtons());
    }

    @GetMapping("/sub-module/{subModuleId}")
    public ResponseEntity<List<ButtonMaster>> getBySubModule(
            @PathVariable Long subModuleId) {
        return ResponseEntity.ok(service.getButtonsBySubModule(subModuleId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteButton(@PathVariable Long id) {
        service.deleteButton(id);
        return ResponseEntity.ok("Button deleted successfully");
    }

    private ButtonMaster toEntity(ButtonRequest request) {
        if (request == null || request.subModule() == null) {
            throw new RuntimeException("subModule is required");
        }

        Long subModuleId = extractSubModuleId(request.subModule());
        if (subModuleId == null) {
            throw new RuntimeException("subModule must be a number or object with subModuleId");
        }

        SubModuleMaster subModuleRef = new SubModuleMaster();
        subModuleRef.setSubModuleId(subModuleId);

        ButtonMaster button = new ButtonMaster();
        button.setButtonName(request.buttonName());
        button.setButtonCode(request.buttonCode());
        button.setIsActive(request.isActive());
        button.setSubModule(subModuleRef);
        return button;
    }

    private Long extractSubModuleId(Object subModule) {
        if (subModule instanceof Number number) {
            return number.longValue();
        }

        if (subModule instanceof Map<?, ?> map) {
            Object id = map.get("subModuleId");
            if (id == null) {
                id = map.get("id");
            }
            if (id instanceof Number number) {
                return number.longValue();
            }
            if (id instanceof String str && !str.isBlank()) {
                return Long.parseLong(str);
            }
        }

        if (subModule instanceof String str && !str.isBlank()) {
            return Long.parseLong(str);
        }

        return null;
    }

    public record ButtonRequest(
            String buttonName,
            String buttonCode,
            Boolean isActive,
            Object subModule
    ) {
    }
}
