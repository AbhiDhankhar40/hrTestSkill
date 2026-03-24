package com.test.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.demo.model.UserGroupMapping;

import java.util.List;

public interface UserGroupMappingRepository
        extends JpaRepository<UserGroupMapping, Long> {

    boolean existsByUser_IdAndGroup_GroupId(Long userId, Long groupId);

    List<UserGroupMapping> findByUser_Id(Long userId);

    List<UserGroupMapping> findByGroup_GroupId(Long groupId);

    void deleteByUser_IdAndGroup_GroupId(Long userId, Long groupId);
}
