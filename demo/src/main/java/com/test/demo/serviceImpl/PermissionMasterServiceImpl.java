package com.test.demo.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.demo.model.ButtonMaster;
import com.test.demo.model.PermissionMaster;
import com.test.demo.model.SubModuleMaster;
import com.test.demo.repository.ButtonMasterRepository;
import com.test.demo.repository.PermissionMasterRepository;
import com.test.demo.repository.SubModuleMasterRepository;
import com.test.demo.service.PermissionMasterService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PermissionMasterServiceImpl implements PermissionMasterService {

    private final PermissionMasterRepository repository;
    private final SubModuleMasterRepository subModuleRepository;
    private final ButtonMasterRepository buttonRepository;

    // ================================
    // CREATE
    // ================================
    @Override
    public PermissionMaster createPermission(PermissionMaster permission) {

        log.info("Creating permission with code={}", permission.getPermissionCode());

        String normalizedCode = permission.getPermissionCode().trim().toUpperCase();

        if (repository.existsByPermissionCode(normalizedCode)) {
            log.error("Permission code already exists: {}", normalizedCode);
            throw new RuntimeException("Permission code already exists");
        }

        permission.setPermissionCode(normalizedCode);

        // 🔴 Enforce mandatory SubModule mapping
        if (permission.getSubModules() == null || permission.getSubModules().isEmpty()) {
            throw new RuntimeException("Permission must be mapped to at least one submodule");
        }

        // Attach managed SubModules
        Set<SubModuleMaster> managedSubModules =
                permission.getSubModules().stream()
                        .map(sm -> subModuleRepository.findById(sm.getSubModuleId())
                                .orElseThrow(() ->
                                        new RuntimeException("SubModule not found: " + sm.getSubModuleId())))
                        .collect(Collectors.toSet());

        permission.setSubModules(managedSubModules);

        // Attach managed Buttons (optional)
        if (permission.getButtons() != null && !permission.getButtons().isEmpty()) {

            Set<ButtonMaster> managedButtons =
                    permission.getButtons().stream()
                            .map(btn -> buttonRepository.findById(btn.getButtonId())
                                    .orElseThrow(() ->
                                            new RuntimeException("Button not found: " + btn.getButtonId())))
                            .collect(Collectors.toSet());

            permission.setButtons(managedButtons);
        }

        PermissionMaster saved = repository.save(permission);

        log.info("Permission created successfully with id={}", saved.getPermissionId());

        return saved;
    }

    // ================================
    // UPDATE
    // ================================
    @Override
    public PermissionMaster updatePermission(Long id, PermissionMaster permission) {

        log.info("Updating permission id={}", id);

        PermissionMaster existing = getPermissionById(id);

        existing.setPermissionName(permission.getPermissionName());
        existing.setActive(permission.isActive());

        // 🔴 SubModule update mandatory
        if (permission.getSubModules() == null || permission.getSubModules().isEmpty()) {
            throw new RuntimeException("Permission must be mapped to at least one submodule");
        }

        Set<SubModuleMaster> updatedSubModules =
                permission.getSubModules().stream()
                        .map(sm -> subModuleRepository.findById(sm.getSubModuleId())
                                .orElseThrow(() ->
                                        new RuntimeException("SubModule not found: " + sm.getSubModuleId())))
                        .collect(Collectors.toSet());

        existing.getSubModules().clear();
        existing.getSubModules().addAll(updatedSubModules);

        // 🔹 Sync Buttons
        if (permission.getButtons() != null) {

            Set<ButtonMaster> updatedButtons =
                    permission.getButtons().stream()
                            .map(btn -> buttonRepository.findById(btn.getButtonId())
                                    .orElseThrow(() ->
                                            new RuntimeException("Button not found: " + btn.getButtonId())))
                            .collect(Collectors.toSet());

            existing.getButtons().clear();
            existing.getButtons().addAll(updatedButtons);
        }

        PermissionMaster updated = repository.save(existing);

        log.info("Permission updated successfully id={}", updated.getPermissionId());

        return updated;
    }

    // ================================
    // READ
    // ================================
    @Override
    @Transactional(readOnly = true)
    public PermissionMaster getPermissionById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Permission not found with id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionMaster> getAllPermissions() {
        return repository.findAll();
    }

    // ================================
    // DELETE
    // ================================
    @Override
    public void deletePermission(Long id) {

        PermissionMaster existing = getPermissionById(id);

        log.warn("Deleting permission id={}, code={}",
                existing.getPermissionId(),
                existing.getPermissionCode());

        repository.delete(existing);
    }
}