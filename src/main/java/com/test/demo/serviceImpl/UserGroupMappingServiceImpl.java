package com.test.demo.serviceImpl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.demo.model.*;
import com.test.demo.repository.*;
import com.test.demo.service.UserGroupMappingService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserGroupMappingServiceImpl implements UserGroupMappingService {

    private final UserGroupMappingRepository mappingRepository;
    private final UserMasterRepository userRepository;
    private final GroupMasterRepository groupRepository;

    @Override
    public UserGroupMapping assignUserToGroup(Long userId, Long groupId) {

        if (mappingRepository.existsByUser_IdAndGroup_GroupId(userId, groupId)) {
            throw new RuntimeException("User already assigned to this group");
        }

        UserMaster user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupMaster group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        UserGroupMapping mapping = UserGroupMapping.builder()
                .user(user)
                .group(group)
                .build();

        return mappingRepository.save(mapping);
    }

    @Override
    public void removeUserFromGroup(Long userId, Long groupId) {

        if (!mappingRepository.existsByUser_IdAndGroup_GroupId(userId, groupId)) {
            throw new RuntimeException("Mapping not found");
        }

        mappingRepository.deleteByUser_IdAndGroup_GroupId(userId, groupId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserGroupMapping> getGroupsByUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }

        return mappingRepository.findByUser_Id(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserGroupMapping> getUsersByGroup(Long groupId) {

        if (!groupRepository.existsById(groupId)) {
            throw new RuntimeException("Group not found");
        }

        return mappingRepository.findByGroup_GroupId(groupId);
    }
}
