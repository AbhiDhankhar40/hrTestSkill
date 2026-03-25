package com.test.demo.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.test.demo.model.DataEntry;
import com.test.demo.repository.DataEntryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataEntryService {

    private final DataEntryRepository dataEntryRepository;

    public DataEntry saveDataEntry(DataEntry dataEntry) {
        return dataEntryRepository.save(dataEntry);
    }

    public List<DataEntry> getAllDataEntries() {
        return dataEntryRepository.findAll();
    }

    public DataEntry getDataEntryById(Long id) {
        return dataEntryRepository.findById(id).orElse(null);
    }

    public void deleteDataEntry(Long id) {
        dataEntryRepository.deleteById(id);
    }
}