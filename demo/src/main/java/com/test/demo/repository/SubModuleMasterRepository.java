package com.test.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.test.demo.model.SubModuleMaster;

import java.util.List;
import java.util.Optional;

public interface SubModuleMasterRepository extends JpaRepository<SubModuleMaster, Long> {

    Optional<SubModuleMaster> findBySubModuleCode(String subModuleCode);

    List<SubModuleMaster> findByIsActiveTrueOrderByDisplayOrderAsc();

    @Query("""
            SELECT sm
            FROM SubModuleMaster sm
            JOIN FETCH sm.module m
            WHERE sm.isActive = true
              AND m.isActive = true
            ORDER BY sm.displayOrder ASC
            """)
    List<SubModuleMaster> findActiveSubModulesOfActiveModules();
}
