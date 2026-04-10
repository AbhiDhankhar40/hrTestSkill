package com.test.demo.service;

import java.util.List;

import com.test.demo.model.UserMaster;

public interface UserMasterService {

    UserMaster createUser(UserMaster user);

    UserMaster updateUser(Long id, UserMaster user);

    UserMaster getUserById(Long id);

    List<UserMaster> getAllUsers();

    void deleteUser(Long id);
    

}
