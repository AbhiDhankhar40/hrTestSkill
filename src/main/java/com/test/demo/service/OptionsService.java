package com.test.demo.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.test.demo.model.Options;
import com.test.demo.repository.OptionsRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OptionsService {

    private final OptionsRepository optionsRepository;

    public Options saveOption(Options option) {
        return optionsRepository.save(option);
    }

    public List<Options> saveOptions(List<Options> options) {
        return optionsRepository.saveAll(options);
    }

    public List<Options> getAllOptions() {
        return optionsRepository.findAll();
    }

    public Options getOptionById(Long id) {
        return optionsRepository.findById(id).orElse(null);
    }

    public void deleteOption(Long id) {
        optionsRepository.deleteById(id);
    }
}