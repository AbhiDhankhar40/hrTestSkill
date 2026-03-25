package com.test.demo.repository;

import com.test.demo.model.GroupPermissionMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupPermissionRepository
        extends JpaRepository<GroupPermissionMapping, Long> {

    List<GroupPermissionMapping> findByGroup_GroupId(Long groupId);

    void deleteByGroup_GroupId(Long groupId);

    boolean existsByGroup_GroupIdAndPermission_PermissionId(
            Long groupId, Long permissionId);

    void deleteByGroup_GroupIdAndPermission_PermissionId(Long groupId, Long permissionId);

    List<GroupPermissionMapping> findByGroup_GroupIdIn(List<Long> groupIds);
}
