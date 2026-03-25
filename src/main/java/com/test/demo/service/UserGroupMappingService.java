package com.test.demo.service;

import com.test.demo.model.UserGroupMapping;

import java.util.List;

public interface UserGroupMappingService {

    UserGroupMapping assignUserToGroup(Long userId, Long groupId);

    void removeUserFromGroup(Long userId, Long groupId);

    List<UserGroupMapping> getGroupsByUser(Long userId);

    List<UserGroupMapping> getUsersByGroup(Long groupId);
}
