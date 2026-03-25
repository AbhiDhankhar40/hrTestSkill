package com.test.demo.serviceImpl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.demo.dto.ButtonDTO;
import com.test.demo.dto.ModulePermissionDTO;
import com.test.demo.dto.SubModulePermissionDTO;
import com.test.demo.model.ButtonMaster;
import com.test.demo.model.GroupPermissionMapping;
import com.test.demo.model.PermissionMaster;
import com.test.demo.model.UserGroupMapping;
import com.test.demo.model.UserMaster;
import com.test.demo.repository.GroupPermissionRepository;
import com.test.demo.repository.UserGroupMappingRepository;
import com.test.demo.repository.UserMasterRepository;
import com.test.demo.service.UserMasterService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserMasterServiceImpl implements UserMasterService {

    private final UserMasterRepository repository;
    private final UserGroupMappingRepository userGroupMappingRepository;
    private final GroupPermissionRepository groupPermissionRepository;

    @Override
    public UserMaster createUser(UserMaster user) {

        if (repository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        return repository.save(user);
    }

    @Override
    public UserMaster updateUser(Long id, UserMaster user) {

        UserMaster existing = getUserById(id);
        existing.setName(user.getName());

        return repository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public UserMaster getUserById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserMaster> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public void deleteUser(Long id) {

        UserMaster existing = getUserById(id);
        repository.delete(existing);
    }

  @Override
@Transactional(readOnly = true)
public List<ModulePermissionDTO> getUserPermissions(Long userId) {

    // 1️⃣ Fetch all groups of user
    List<UserGroupMapping> userGroups =
            userGroupMappingRepository.findByUser_Id(userId);

    if (userGroups.isEmpty()) {
        return List.of();
    }

    List<Long> groupIds = userGroups.stream()
            .map(m -> m.getGroup().getGroupId())
            .toList();

    // 2️⃣ Fetch group permissions
    List<GroupPermissionMapping> groupPermissions =
            groupPermissionRepository.findByGroup_GroupIdIn(groupIds);

    // 3️⃣ Extract permissions
    Set<PermissionMaster> permissions = groupPermissions.stream()
            .map(GroupPermissionMapping::getPermission)
            .filter(PermissionMaster::isActive)
            .collect(Collectors.toSet());

    Map<Long, ModulePermissionDTO> moduleMap = new LinkedHashMap<>();

    for (PermissionMaster permission : permissions) {

        for (ButtonMaster button : permission.getButtons()) {

            Long moduleId =
                    button.getSubModule().getModule().getModuleId();

            Long subModuleId =
                    button.getSubModule().getSubModuleId();

            moduleMap.putIfAbsent(moduleId,
                    ModulePermissionDTO.builder()
                            .moduleId(moduleId)
                            .moduleName(
                                button.getSubModule()
                                      .getModule()
                                      .getModuleName())
                            .subModules(new ArrayList<>())
                            .build());

            ModulePermissionDTO moduleDTO = moduleMap.get(moduleId);

            SubModulePermissionDTO subModuleDTO =
                    moduleDTO.getSubModules().stream()
                            .filter(sm ->
                                sm.getSubModuleId()
                                  .equals(subModuleId))
                            .findFirst()
                            .orElseGet(() -> {

                                SubModulePermissionDTO newSub =
                                        SubModulePermissionDTO.builder()
                                                .subModuleId(subModuleId)
                                                .subModuleName(
                                                    button.getSubModule()
                                                          .getSubModuleName())
                                                .buttons(new ArrayList<>())
                                                .build();

                                moduleDTO.getSubModules().add(newSub);
                                return newSub;
                            });

            subModuleDTO.getButtons().add(
                    ButtonDTO.builder()
                            .buttonId(button.getButtonId())
                            .buttonName(button.getButtonName())
                            .build()
            );
        }
    }

    return new ArrayList<>(moduleMap.values());
}

}
