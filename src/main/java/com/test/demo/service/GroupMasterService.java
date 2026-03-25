package com.test.demo.service;

import com.test.demo.model.GroupMaster;

import java.util.List;

public interface GroupMasterService {

    GroupMaster createGroup(GroupMaster group);

    GroupMaster updateGroup(Long id, GroupMaster group);

    GroupMaster getGroupById(Long id);

    List<GroupMaster> getAllGroups();

    void deleteGroup(Long id);
}
