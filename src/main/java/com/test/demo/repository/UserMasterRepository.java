package com.test.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.demo.model.UserMaster;

import java.util.Optional;

public interface UserMasterRepository extends JpaRepository<UserMaster, Long> {

    Optional<UserMaster> findByUsername(String username);

    boolean existsByUsername(String username);
}
