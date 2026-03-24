package com.test.demo.serviceImpl;


import org.springframework.stereotype.Service;

import com.test.demo.model.ModuleMaster;
import com.test.demo.model.SubModuleMaster;
import com.test.demo.repository.ModuleMasterRepository;
import com.test.demo.repository.SubModuleMasterRepository;
import com.test.demo.service.SubModuleMasterService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubModuleMasterServiceImpl implements SubModuleMasterService {

    private final SubModuleMasterRepository subModuleRepository;
      private final ModuleMasterRepository moduleRepository;

    @Override
public SubModuleMaster createSubModule(Long moduleId, SubModuleMaster subModule) {

    ModuleMaster module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new RuntimeException(
                    "Module not found with id: " + moduleId));

    subModule.setModule(module);

    return subModuleRepository.save(subModule);
}


    @Override
    public SubModuleMaster updateSubModule(Long id, SubModuleMaster subModule) {

        SubModuleMaster existing = subModuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubModule not found with id: " + id));

        existing.setSubModuleName(subModule.getSubModuleName());
        existing.setSubModuleCode(subModule.getSubModuleCode());
        existing.setDisplayOrder(subModule.getDisplayOrder());
        existing.setIsActive(subModule.getIsActive());
   
        if (subModule.getModule() != null) {
            ModuleMaster module = moduleRepository.findById(
                    subModule.getModule().getModuleId()
            ).orElseThrow(() -> new RuntimeException(
                    "Module not found with id: " + subModule.getModule().getModuleId()
            ));

            existing.setModule(module);
        }

        return subModuleRepository.save(existing);
    }

    @Override
    public SubModuleMaster getSubModuleById(Long id) {
        return subModuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubModule not found with id: " + id));
    }

    @Override
    public List<SubModuleMaster> getAllActiveSubModules() {
        return subModuleRepository.findActiveSubModulesOfActiveModules();
    }

    @Override
    public void deleteSubModule(Long id) {
        subModuleRepository.deleteById(id);
    }
}
