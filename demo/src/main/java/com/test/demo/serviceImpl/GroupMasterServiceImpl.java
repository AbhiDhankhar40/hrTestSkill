package com.test.demo.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.test.demo.model.GroupMaster;
import com.test.demo.repository.GroupMasterRepository;
import com.test.demo.service.GroupMasterService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupMasterServiceImpl implements GroupMasterService {

    private final GroupMasterRepository repository;

    @Override
    public GroupMaster createGroup(GroupMaster group) {

        if (repository.existsByGroupCode(group.getGroupCode())) {
            throw new RuntimeException("Group code already exists");
        }

        return repository.save(group);
    }

    @Override
    public GroupMaster updateGroup(Long id, GroupMaster group) {

        GroupMaster existing = getGroupById(id);

        existing.setGroupName(group.getGroupName());
        existing.setDescription(group.getDescription());
        existing.setIsActive(group.getIsActive());

        return repository.save(existing);
    }

    @Override
    public GroupMaster getGroupById(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found with id " + id));
    }

    @Override
    public List<GroupMaster> getAllGroups() {
        return repository.findAll();
    }

    @Override
    public void deleteGroup(Long id) {

        GroupMaster existing = getGroupById(id);
        repository.delete(existing);
    }
}
