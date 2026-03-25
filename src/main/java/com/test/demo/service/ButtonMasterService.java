package com.test.demo.service;

import java.util.List;

import com.test.demo.model.ButtonMaster;

public interface ButtonMasterService {

    ButtonMaster createButton(ButtonMaster button);

    ButtonMaster updateButton(Long id, ButtonMaster button);

    ButtonMaster getButtonById(Long id);

    List<ButtonMaster> getAllButtons();

    List<ButtonMaster> getButtonsBySubModule(Long subModuleId);

    void deleteButton(Long id);
}
