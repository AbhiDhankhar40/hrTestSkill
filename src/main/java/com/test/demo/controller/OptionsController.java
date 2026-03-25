package com.test.demo.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.test.demo.model.Options;
import com.test.demo.service.OptionsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/options")
@RequiredArgsConstructor
public class OptionsController {

    private final OptionsService optionsService;

    @PostMapping
    public ResponseEntity<Options> createOption(@RequestBody Options option) {
        return ResponseEntity.ok(optionsService.saveOption(option));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Options>> createOptions(@RequestBody List<Options> options) {
        return ResponseEntity.ok(optionsService.saveOptions(options));
    }

    @GetMapping
    public ResponseEntity<List<Options>> getAllOptions() {
        return ResponseEntity.ok(optionsService.getAllOptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Options> getOptionById(@PathVariable Long id) {
        return ResponseEntity.ok(optionsService.getOptionById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOption(@PathVariable Long id) {
        optionsService.deleteOption(id);
        return ResponseEntity.ok().build();
    }
}