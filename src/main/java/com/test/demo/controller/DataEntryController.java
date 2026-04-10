package com.test.demo.controller;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.test.demo.controller.DataEntryController.QuestionAnswer;
import com.test.demo.model.AnswerEntry;
import com.test.demo.model.DataEntry;
import com.test.demo.model.Options;
import com.test.demo.model.Question;
import com.test.demo.repository.AnswerEntryRepository;
import com.test.demo.repository.OptionsRepository;
import com.test.demo.repository.QuestionRepository;
import com.test.demo.service.DataEntryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/data-entry")
@RequiredArgsConstructor
public class DataEntryController {

    private final DataEntryService dataEntryService;
    private final AnswerEntryRepository answerEntryRepository;
    private final OptionsRepository optionsRepository;
    private final QuestionRepository questionRepository;

    @PostMapping
    public ResponseEntity<DataEntry> createDataEntry(@RequestBody DataEntry dataEntry) {
        return ResponseEntity.ok(dataEntryService.saveDataEntry(dataEntry));
    }

    @GetMapping("/allEntries")
    public ResponseEntity<List<DataEntry>> getAllDataEntries() {
        return ResponseEntity.ok(dataEntryService.getAllDataEntries());
    }

    @PostMapping({"/check-email", "/checkEmail"})
    public ResponseEntity<String> checkEmailUsage(@RequestBody EmailRequest request) {
        String normalizedEmail = request == null || request.email() == null ? "" : request.email().trim();
        if (normalizedEmail.isEmpty()) {
            return ResponseEntity.ok("new");
        }
        boolean used = dataEntryService.isEmailUsed(normalizedEmail);
        return ResponseEntity.ok(used ? "used" : "new");
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
        DataEntry dataEntry = new DataEntry();
        dataEntry.setName(request.name());
        dataEntry.setEmail(request.email());
        dataEntry.setMobile(request.mobile());
        dataEntry.setType(request.type());
    Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String strDate = sdf.format(cal.getTime());

        dataEntry.setSubmittedOn(strDate);
        Map<Integer, Integer> entry = request.entry();
        if (entry != null && !entry.isEmpty()) {
            List<Long> optionIds = entry.values().stream()
                    .filter(Objects::nonNull)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            List<Options> options = optionsRepository.findAllById(optionIds);
            int totalMarks = options.stream()
                    .map(Options::getMarks)
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .sum();
            dataEntry.setTotalScore(totalMarks);
        } else {
            dataEntry.setTotalScore(0);
        }
        String result="Failed";
        if(dataEntry.getTotalScore()>0) {
        double percentage = (dataEntry.getTotalScore() / 20.0) * 100;
        
        if (dataEntry.getTotalScore()>16) {
            result = "Pass"; 
        } else {
           result = "Failed";
        }
        }
        dataEntry.setResult(result);

        DataEntry savedDataEntry = dataEntryService.saveDataEntry(dataEntry);

        AnswerEntry answerEntry = new AnswerEntry();
        answerEntry.setDataEntryId(savedDataEntry.getId());
        if (entry != null && !entry.isEmpty()) {
            entry.entrySet().stream()
                    .filter(e -> e.getKey() != null)
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> setAnswerValue(answerEntry, e.getKey(), e.getValue()));
        }
        answerEntryRepository.save(answerEntry);

        return ResponseEntity.ok(new SubmitResponse("Mission Accomplished Successfully", savedDataEntry));
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

    @GetMapping("/answers/{entryId}")
    public ResponseEntity<List<QuestionAnswer>> getAnswersByEntryId(@PathVariable Long entryId) {
        DataEntry dataEntry = dataEntryService.getDataEntryById(entryId);
        if (dataEntry == null) {
            return ResponseEntity.notFound().build();
        }

        Optional<AnswerEntry> answerEntryOptional = answerEntryRepository.findByDataEntryId(entryId);
        if (answerEntryOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AnswerEntry answerEntry = answerEntryOptional.get();
        List<Question> questions = questionRepository.findByType(dataEntry.getType()).stream()
                .sorted(Comparator.comparing(Question::getId))
                .collect(Collectors.toList());
        List<Long> optionIds = questions.stream()
                .map(question -> getAnswerValue(answerEntry, question.getId() == null ? null : question.getId().intValue()))
                .filter(Objects::nonNull)
                .map(Integer::longValue)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Options> optionsById = optionIds.isEmpty()
                ? Map.of()
                : optionsRepository.findAllById(optionIds).stream()
                        .collect(Collectors.toMap(Options::getId, option -> option));
        List<QuestionAnswer> response = questions.stream()
                .map(question -> {
                    Integer questionId = question.getId() == null ? null : question.getId().intValue();
                    Integer answerId = getAnswerValue(answerEntry, questionId);
                    Integer marks = null;
                    if (answerId != null) {
                        Options option = optionsById.get(answerId.longValue());
                        if (option != null) {
                            marks = option.getMarks();
                        }
                    }
                    return new QuestionAnswer(questionId, answerId, marks);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    public record DataEntryRequest(String name, String email, String mobile,String type, Map<Integer, Integer> entry) {}

    public record SubmitResponse(String result, DataEntry dataEntry) {}

    public record QuestionAnswer(Integer question, Integer answer, Integer marks) {}

    public record EmailRequest(String email) {}

    private void setAnswerValue(AnswerEntry answerEntry, Integer index, Integer value) {
        if (answerEntry == null || index == null || index < 1 || index > 60) return;
        try {
            Method method = answerEntry.getClass().getMethod("setAns" + index, Integer.class);
            method.invoke(answerEntry, value);
        } catch (Exception e) {
            // Optionally log error
        }
    }

    private Integer getAnswerValue(AnswerEntry answerEntry, Integer index) {
        if (answerEntry == null || index == null || index < 1 || index > 60) return null;
        try {
            Method method = answerEntry.getClass().getMethod("getAns" + index);
            return (Integer) method.invoke(answerEntry);
        } catch (Exception e) {
            return null;
        }
    }
}
