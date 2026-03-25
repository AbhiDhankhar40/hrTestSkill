package com.test.demo.service;

import java.util.List;

import com.test.demo.model.ModuleMaster;

public interface ModuleMasterService {

    ModuleMaster createModule(ModuleMaster module);

    ModuleMaster updateModule(Long id, ModuleMaster module);

    ModuleMaster getModuleById(Long id);

    List<ModuleMaster> getAllActiveModules();

    void deleteModule(Long id);
}
