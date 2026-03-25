package com.test.demo.serviceImpl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.test.demo.model.ModuleMaster;
import com.test.demo.repository.ModuleMasterRepository;
import com.test.demo.service.ModuleMasterService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleMasterServiceImpl implements ModuleMasterService {

    private final ModuleMasterRepository repository;

    @Override
    public ModuleMaster createModule(ModuleMaster module) {
        return repository.save(module);
    }

    @Override
    public ModuleMaster updateModule(Long id, ModuleMaster module) {
        ModuleMaster existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found with id: " + id));

        existing.setModuleName(module.getModuleName());
        existing.setModuleCode(module.getModuleCode());
        existing.setDisplayOrder(module.getDisplayOrder());
        existing.setIsActive(module.getIsActive());

        return repository.save(existing);
    }

    @Override
    public ModuleMaster getModuleById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found with id: " + id));
    }

    @Override
    public List<ModuleMaster> getAllActiveModules() {
        return repository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    @Override
    public void deleteModule(Long id) {
        repository.deleteById(id);
    }
}

