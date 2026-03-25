package com.test.demo.service;

import com.test.demo.model.GroupPermissionMapping;

import java.util.List;

public interface GroupPermissionService {

    void assignPermissions(Long groupId, List<Long> permissionIds);

    List<GroupPermissionMapping> getPermissionsByGroup(Long groupId);

    void removePermission(Long groupId, Long permissionId);

 }
