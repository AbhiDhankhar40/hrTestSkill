package com.test.demo.serviceImpl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.demo.model.UserMaster;

import com.test.demo.repository.UserMasterRepository;
import com.test.demo.service.UserMasterService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserMasterServiceImpl implements UserMasterService {

    private final UserMasterRepository repository;


    @Override
    public UserMaster createUser(UserMaster user) {

        if (repository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        return repository.save(user);
    }

    @Override
    public UserMaster updateUser(Long id, UserMaster user) {

        UserMaster existing = getUserById(id);
        existing.setName(user.getName());

        return repository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public UserMaster getUserById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserMaster> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public void deleteUser(Long id) {

        UserMaster existing = getUserById(id);
        repository.delete(existing);
    }


}
