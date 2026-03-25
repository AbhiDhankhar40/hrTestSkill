package com.test.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.test.demo.model.DataEntry;
import com.test.demo.service.DataEntryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/data-entry")
@RequiredArgsConstructor
public class DataEntryController {

    private final DataEntryService dataEntryService;

    @PostMapping
    public ResponseEntity<DataEntry> createDataEntry(@RequestBody DataEntry dataEntry) {
        return ResponseEntity.ok(dataEntryService.saveDataEntry(dataEntry));
    }

    @GetMapping("/allEntries")
    public ResponseEntity<List<DataEntry>> getAllDataEntries() {
        return ResponseEntity.ok(dataEntryService.getAllDataEntries());
    }

    // @GetMapping("/{id}")
    // public ResponseEntity<DataEntry> getDataEntryById(@PathVariable Long id) {
    //     return ResponseEntity.ok(dataEntryService.getDataEntryById(id));
    // }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDataEntry(@PathVariable Long id) {
        dataEntryService.deleteDataEntry(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/submit")
    public ResponseEntity<SubmitResponse> submitDataEntry(@RequestBody DataEntryRequest request) {
        int totalScore = 0;
        if (request.entry() != null) {
            for (int i = 1; i <= 50; i++) {
                totalScore += request.entry().getOrDefault(i, 0);
            }
        }

        DataEntry dataEntry = new DataEntry();
        dataEntry.setName(request.name());
        dataEntry.setEmail(request.email());
        dataEntry.setMobile(request.mobile());
        dataEntry.setTotalScore(totalScore);

        double percentage = (totalScore / 250.0) * 100;
        String result;
        if (percentage < 50) {
            result = "Failed";
        } else if (percentage < 60) {
            result = "Assistant Professor ";
        } else if (percentage <= 80) {
            result = "Associate Professor";
        } else {
            result = "Professor";
        }
        dataEntry.setResult(result);

        DataEntry savedDataEntry = dataEntryService.saveDataEntry(dataEntry);
        return ResponseEntity.ok(new SubmitResponse(result, savedDataEntry));
    }

    @GetMapping("/average")
    public ResponseEntity<Map<String, Double>> getAverageScore() {
        List<DataEntry> entries = dataEntryService.getAllDataEntries();
        if (entries.isEmpty()) {
            return ResponseEntity.ok(Map.of("average", 0.0));
        }

        double total = 0;
        int count = 0;
        for (DataEntry entry : entries) {
            if (entry.getTotalScore() != null) {
                total += entry.getTotalScore();
                count++;
            }
        }

        double average = count == 0 ? 0 : total / count;
        average = Math.round(average * 100.0) / 100.0;
        return ResponseEntity.ok(Map.of("average", average));
    }

    public record DataEntryRequest(String name, String email, String mobile, Map<Integer, Integer> entry) {}

    public record SubmitResponse(String result, DataEntry dataEntry) {}
}