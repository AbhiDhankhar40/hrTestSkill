package com.test.demo.service;

import com.test.demo.dto.ModulePermissionDTO;
import com.test.demo.model.UserMaster;

import java.util.List;

public interface UserMasterService {

    UserMaster createUser(UserMaster user);

    UserMaster updateUser(Long id, UserMaster user);

    UserMaster getUserById(Long id);

    List<UserMaster> getAllUsers();

    void deleteUser(Long id);
    

}
