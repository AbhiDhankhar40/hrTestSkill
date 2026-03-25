package com.test.demo.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.demo.model.ButtonMaster;

@Repository
public interface ButtonMasterRepository extends JpaRepository<ButtonMaster, Long> {

    Optional<ButtonMaster> findByButtonCode(String buttonCode);

    List<ButtonMaster> findBySubModule(Long subModule);

    List<ButtonMaster> findByIsActiveTrue();
}
