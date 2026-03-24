package com.test.demo.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.demo.model.ModuleMaster;

public interface ModuleMasterRepository extends JpaRepository<ModuleMaster, Long> {

    Optional<ModuleMaster> findByModuleCode(String moduleCode);

    List<ModuleMaster> findByIsActiveTrueOrderByDisplayOrderAsc();
}
