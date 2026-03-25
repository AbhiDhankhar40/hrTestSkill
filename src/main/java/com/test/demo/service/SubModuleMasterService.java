package com.test.demo.service;



import java.util.List;

import com.test.demo.model.SubModuleMaster;

public interface SubModuleMasterService {

    SubModuleMaster createSubModule(Long moduleId, SubModuleMaster subModule);

    SubModuleMaster updateSubModule(Long id, SubModuleMaster subModule);

    SubModuleMaster getSubModuleById(Long id);

    List<SubModuleMaster> getAllActiveSubModules();

    void deleteSubModule(Long id);
}
