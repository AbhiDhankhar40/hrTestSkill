package com.test.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.test.demo.model.SubModuleMaster;
import com.test.demo.service.SubModuleMasterService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sub-modules")
@RequiredArgsConstructor
public class SubModuleMasterController {

    private final SubModuleMasterService service;

     @PostMapping("/add")
    public ResponseEntity<SubModuleMaster> createSubModule(
            @RequestBody CreateSubModuleRequest request) {

        if (request == null || request.moduleId() == null) {
            return ResponseEntity.badRequest().build();
        }

        SubModuleMaster subModule = SubModuleMaster.builder()
                .subModuleName(request.subModuleName())
                .subModuleCode(request.subModuleCode())
                .subModuleRoute(request.subModuleRoute())
                .displayOrder(request.displayOrder())
                .isActive(request.isActive())
                .build();

        return ResponseEntity.ok(service.createSubModule(request.moduleId(), subModule));
    }

    @PostMapping("/{moduleId:\\d+}")
    public ResponseEntity<SubModuleMaster> createSubModule(
            @PathVariable Long moduleId,
            @RequestBody SubModuleMaster subModule) {

        return ResponseEntity.ok(service.createSubModule(moduleId, subModule));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<SubModuleMaster> updateSubModule(
            @PathVariable Long id,
            @RequestBody SubModuleMaster subModule) {

        return ResponseEntity.ok(service.updateSubModule(id, subModule));
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<SubModuleMaster> getSubModule(@PathVariable Long id) {
        return ResponseEntity.ok(service.getSubModuleById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<List<SubModuleMaster>> getActiveSubModules() {
        return ResponseEntity.ok(service.getAllActiveSubModules());
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<String> deleteSubModule(@PathVariable Long id) {
        service.deleteSubModule(id);
        return ResponseEntity.ok("SubModule deleted successfully");
    }

    public record CreateSubModuleRequest(
            Long moduleId,
            String subModuleName,
            String subModuleCode,
            String subModuleRoute,
            Integer displayOrder,
            Boolean isActive,
            String unique_id,
            String latitude,
            String longitude,
            String ip_address,
            String current_date,
            String current_time,
            Map<String, Object> extra
    ) {
    }
}
