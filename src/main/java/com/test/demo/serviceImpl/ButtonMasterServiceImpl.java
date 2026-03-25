package com.test.demo.serviceImpl;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.demo.model.ButtonMaster;
import com.test.demo.repository.ButtonMasterRepository;
import com.test.demo.service.ButtonMasterService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ButtonMasterServiceImpl implements ButtonMasterService {

    private final ButtonMasterRepository repository;

    @Override
    public ButtonMaster createButton(ButtonMaster button) {

        // Check duplicate buttonCode
        repository.findByButtonCode(button.getButtonCode())
                .ifPresent(b -> {
                    throw new RuntimeException("Button code already exists");
                });

        return repository.save(button);
    }

    @Override
    public ButtonMaster updateButton(Long id, ButtonMaster button) {

        ButtonMaster existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Button not found with id: " + id));

        existing.setButtonName(button.getButtonName());
        existing.setButtonCode(button.getButtonCode());
        existing.setIsActive(button.getIsActive());
        existing.setSubModule(button.getSubModule());

        return repository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public ButtonMaster getButtonById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Button not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ButtonMaster> getAllButtons() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ButtonMaster> getButtonsBySubModule(Long subModuleId) {
        return repository.findBySubModule(subModuleId);
    }

    @Override
    public void deleteButton(Long id) {

        ButtonMaster existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Button not found with id: " + id));

        repository.delete(existing);
    }
}
