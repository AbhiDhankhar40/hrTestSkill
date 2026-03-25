package com.test.demo.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.demo.model.GroupMaster;
import com.test.demo.model.PermissionMaster;
import com.test.demo.model.GroupPermissionMapping;
import com.test.demo.repository.GroupMasterRepository;
import com.test.demo.repository.PermissionMasterRepository;
import com.test.demo.repository.GroupPermissionRepository;
import com.test.demo.service.GroupPermissionService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupPermissionServiceImpl implements GroupPermissionService {

    private final GroupPermissionRepository mappingRepository;
    private final GroupMasterRepository groupRepository;
    private final PermissionMasterRepository permissionRepository;

    // Assign multiple permissions to group
    @Override
    public void assignPermissions(Long groupId, List<Long> permissionIds) {

        GroupMaster group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException(
                        "Group not found with id: " + groupId));

        List<PermissionMaster> permissions =
                permissionRepository.findAllById(permissionIds);

        if (permissions.size() != permissionIds.size()) {
            throw new RuntimeException("One or more Permission IDs are invalid");
        }

        for (PermissionMaster permission : permissions) {

            boolean exists =
                    mappingRepository.existsByGroup_GroupIdAndPermission_PermissionId(
                            groupId,
                            permission.getPermissionId());

            if (!exists) {

                GroupPermissionMapping mapping =
                        GroupPermissionMapping.builder()
                                .group(group)
                                .permission(permission)
                                .build();

                mappingRepository.save(mapping);
            }
        }
    }

    // Fetch all permissions assigned to a group
    @Override
    @Transactional(readOnly = true)
    public List<GroupPermissionMapping> getPermissionsByGroup(Long groupId) {

        if (!groupRepository.existsById(groupId)) {
            throw new RuntimeException(
                    "Group not found with id: " + groupId);
        }

        return mappingRepository.findByGroup_GroupId(groupId);
    }

    // Remove specific permission from group
    @Override
    public void removePermission(Long groupId, Long permissionId) {

        boolean exists =
                mappingRepository.existsByGroup_GroupIdAndPermission_PermissionId(
                        groupId, permissionId);

        if (!exists) {
            throw new RuntimeException(
                    "Permission not assigned to this group");
        }

        mappingRepository
                .deleteByGroup_GroupIdAndPermission_PermissionId(
                        groupId, permissionId);
    }
}
