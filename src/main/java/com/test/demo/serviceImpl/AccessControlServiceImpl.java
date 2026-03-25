package com.test.demo.serviceImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.demo.dto.AccessControlResponseDTO;
import com.test.demo.dto.ButtonDTO;
import com.test.demo.dto.GroupDTO;
import com.test.demo.dto.ModuleDTO;
import com.test.demo.dto.PermissionDTO;
import com.test.demo.dto.SubModuleDTO;
import com.test.demo.model.ModuleMaster;
import com.test.demo.model.PermissionMaster;
import com.test.demo.model.SubModuleMaster;
import com.test.demo.model.UserMaster;
import com.test.demo.repository.GroupMasterRepository;
import com.test.demo.repository.PermissionMasterRepository;
import com.test.demo.repository.UserGroupMappingRepository;
import com.test.demo.repository.UserMasterRepository;
import com.test.demo.service.AccessControlService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccessControlServiceImpl implements AccessControlService {
    private final UserMasterRepository userRepository;
    private final UserGroupMappingRepository mappingRepository;
    private final PermissionMasterRepository permissionRepository;
    private final GroupMasterRepository groupRepository;

    @Override
    public AccessControlResponseDTO getUserAccessControl(Long userId) {

        log.info("Fetching access control for userId={}", userId);

        UserMaster user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found for userId={}", userId);
                    return new RuntimeException("User not found");
                });

        log.info("User found: id={}, username={}", user.getId(), user.getUsername());

        List<GroupDTO> groups = groupRepository.findGroupsByUserId(userId)
                .stream()
                .map(g -> GroupDTO.builder()
                        .groupId(g.getGroupId())
                        .groupName(g.getGroupName())
                        .build())
                .toList();

        log.info("Groups fetched for userId={}: count={}", userId, groups.size());

        List<PermissionMaster> permissions = permissionRepository.findPermissionsByUserId(userId);

        log.info("Permissions fetched for userId={}: count={}", userId,
                permissions != null ? permissions.size() : 0);

        if (permissions == null || permissions.isEmpty()) {
            log.warn("No permissions found for userId={}", userId);
        } else {
            permissions.forEach(p ->
                    log.debug("Permission: id={}, code={}, subModulesCount={}, buttonsCount={}",
                            p.getPermissionId(),
                            p.getPermissionCode(),
                            p.getSubModules() != null ? p.getSubModules().size() : 0,
                            p.getButtons() != null ? p.getButtons().size() : 0
                    ));
        }

        Map<ModuleMaster, Map<SubModuleMaster, List<PermissionMaster>>> grouped =
                permissions.stream()
                        .flatMap(permission ->
                                permission.getSubModules().stream()
                                        .map(subModule -> {
                                            log.debug("Mapping permission {} to subModule {} (module={})",
                                                    permission.getPermissionCode(),
                                                    subModule.getSubModuleName(),
                                                    subModule.getModule().getModuleName());
                                            return Map.entry(subModule, permission);
                                        })
                        )
                        .collect(Collectors.groupingBy(
                                entry -> entry.getKey().getModule(),
                                Collectors.groupingBy(
                                        Map.Entry::getKey,
                                        Collectors.mapping(
                                                Map.Entry::getValue,
                                                Collectors.toList()
                                        )
                                )
                        ));

        log.info("Grouped modules count={}", grouped.size());

        grouped.forEach((module, subMap) -> {
            log.info("Module: id={}, name={}, subModulesCount={}",
                    module.getModuleId(),
                    module.getModuleName(),
                    subMap.size());
        });

        List<ModuleDTO> modules = grouped.entrySet().stream()
                .map(moduleEntry -> ModuleDTO.builder()
                        .moduleId(moduleEntry.getKey().getModuleId())
                        .moduleName(moduleEntry.getKey().getModuleName())
                        .subModules(
                                moduleEntry.getValue().entrySet().stream()
                                        .map(subEntry -> SubModuleDTO.builder()
                                                .subModuleId(subEntry.getKey().getSubModuleId())
                                                .subModuleName(subEntry.getKey().getSubModuleName())
                                                .subModuleRoute(subEntry.getKey().getSubModuleRoute())
                                                .permissions(
                                                        subEntry.getValue().stream()
                                                                .distinct()
                                                                .map(permission -> PermissionDTO.builder()
                                                                        .permissionId(permission.getPermissionId())
                                                                        .permissionCode(permission.getPermissionCode())
                                                                        .buttons(
                                                                                permission.getButtons().stream()
                                                                                        .map(button -> ButtonDTO.builder()
                                                                                                .buttonId(button.getButtonId())
                                                                                                .buttonCode(button.getButtonCode())
                                                                                                .buttonName(button.getButtonName())
                                                                                                .build())
                                                                                        .toList()
                                                                        )
                                                                        .build())
                                                                .toList()
                                                )
                                                .build())
                                        .toList()
                        )
                        .build())
                .toList();

        log.info("Final modules DTO count={}", modules.size());

        if (modules.isEmpty()) {
            log.warn("No modules built for userId={}. Check permission-subModule-module mapping.", userId);
        }

        return AccessControlResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .createdAt(user.getCreatedAt())
                .groups(groups)
                .modules(modules)
                .build();
    }
}


