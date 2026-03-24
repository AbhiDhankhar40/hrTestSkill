package com.test.demo.service;

import com.test.demo.model.PermissionMaster;

import java.util.List;
public interface PermissionMasterService {

    PermissionMaster createPermission(PermissionMaster permission);

    PermissionMaster updatePermission(Long id, PermissionMaster permission);

    PermissionMaster getPermissionById(Long id);

    List<PermissionMaster> getAllPermissions();

    void deletePermission(Long id);
}
