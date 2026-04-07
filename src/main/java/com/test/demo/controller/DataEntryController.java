package com.test.demo.controller;

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
        List<QuestionAnswer> response = questionRepository.findByType(dataEntry.getType()).stream()
                .sorted(Comparator.comparing(Question::getId))
                .map(question -> new QuestionAnswer(
                        question.getId() == null ? null : question.getId().intValue(),
                        getAnswerValue(answerEntry, question.getId() == null ? null : question.getId().intValue())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    public record DataEntryRequest(String name, String email, String mobile,String type, Map<Integer, Integer> entry) {}

    public record SubmitResponse(String result, DataEntry dataEntry) {}

    public record QuestionAnswer(Integer question, Integer answer) {}

    public record EmailRequest(String email) {}

    private void setAnswerValue(AnswerEntry answerEntry, Integer index, Integer value) {
        if (index == null) {
            return;
        }
        switch (index) {
            case 1 -> answerEntry.setAns1(value);
            case 2 -> answerEntry.setAns2(value);
            case 3 -> answerEntry.setAns3(value);
            case 4 -> answerEntry.setAns4(value);
            case 5 -> answerEntry.setAns5(value);
            case 6 -> answerEntry.setAns6(value);
            case 7 -> answerEntry.setAns7(value);
            case 8 -> answerEntry.setAns8(value);
            case 9 -> answerEntry.setAns9(value);
            case 10 -> answerEntry.setAns10(value);
            case 11 -> answerEntry.setAns11(value);
            case 12 -> answerEntry.setAns12(value);
            case 13 -> answerEntry.setAns13(value);
            case 14 -> answerEntry.setAns14(value);
            case 15 -> answerEntry.setAns15(value);
            case 16 -> answerEntry.setAns16(value);
            case 17 -> answerEntry.setAns17(value);
            case 18 -> answerEntry.setAns18(value);
            case 19 -> answerEntry.setAns19(value);
            case 20 -> answerEntry.setAns20(value);
            case 21 -> answerEntry.setAns21(value);
            case 22 -> answerEntry.setAns22(value);
            case 23 -> answerEntry.setAns23(value);
            case 24 -> answerEntry.setAns24(value);
            case 25 -> answerEntry.setAns25(value);
            case 26 -> answerEntry.setAns26(value);
            case 27 -> answerEntry.setAns27(value);
            case 28 -> answerEntry.setAns28(value);
            case 29 -> answerEntry.setAns29(value);
            case 30 -> answerEntry.setAns30(value);
            case 31 -> answerEntry.setAns31(value);
            case 32 -> answerEntry.setAns32(value);
            case 33 -> answerEntry.setAns33(value);
            case 34 -> answerEntry.setAns34(value);
            case 35 -> answerEntry.setAns35(value);
            case 36 -> answerEntry.setAns36(value);
            case 37 -> answerEntry.setAns37(value);
            case 38 -> answerEntry.setAns38(value);
            case 39 -> answerEntry.setAns39(value);
            case 40 -> answerEntry.setAns40(value);
            case 41 -> answerEntry.setAns41(value);
            case 42 -> answerEntry.setAns42(value);
            case 43 -> answerEntry.setAns43(value);
            case 44 -> answerEntry.setAns44(value);
            case 45 -> answerEntry.setAns45(value);
            case 46 -> answerEntry.setAns46(value);
            case 47 -> answerEntry.setAns47(value);
            case 48 -> answerEntry.setAns48(value);
            case 49 -> answerEntry.setAns49(value);
            case 50 -> answerEntry.setAns50(value);
            case 51 -> answerEntry.setAns51(value);
            case 52 -> answerEntry.setAns52(value);
            case 53 -> answerEntry.setAns53(value);
            case 54 -> answerEntry.setAns54(value);
            case 55 -> answerEntry.setAns55(value);
            case 56 -> answerEntry.setAns56(value);
            case 57 -> answerEntry.setAns57(value);
            case 58 -> answerEntry.setAns58(value); 
            case 59 -> answerEntry.setAns59(value);
            case 60 -> answerEntry.setAns60(value);
            default -> {
            }
        }
    }

    private Integer getAnswerValue(AnswerEntry answerEntry, Integer index) {
        if (index == null) {
            return null;
        }
        return switch (index) {
            case 1 -> answerEntry.getAns1();
            case 2 -> answerEntry.getAns2();
            case 3 -> answerEntry.getAns3();
            case 4 -> answerEntry.getAns4();
            case 5 -> answerEntry.getAns5();
            case 6 -> answerEntry.getAns6();
            case 7 -> answerEntry.getAns7();
            case 8 -> answerEntry.getAns8();
            case 9 -> answerEntry.getAns9();
            case 10 -> answerEntry.getAns10();
            case 11 -> answerEntry.getAns11();
            case 12 -> answerEntry.getAns12();
            case 13 -> answerEntry.getAns13();
            case 14 -> answerEntry.getAns14();
            case 15 -> answerEntry.getAns15();
            case 16 -> answerEntry.getAns16();
            case 17 -> answerEntry.getAns17();
            case 18 -> answerEntry.getAns18();
            case 19 -> answerEntry.getAns19();
            case 20 -> answerEntry.getAns20();
            case 21 -> answerEntry.getAns21();
            case 22 -> answerEntry.getAns22();
            case 23 -> answerEntry.getAns23();
            case 24 -> answerEntry.getAns24();
            case 25 -> answerEntry.getAns25();
            case 26 -> answerEntry.getAns26();
            case 27 -> answerEntry.getAns27();
            case 28 -> answerEntry.getAns28();
            case 29 -> answerEntry.getAns29();
            case 30 -> answerEntry.getAns30();
            case 31 -> answerEntry.getAns31();
            case 32 -> answerEntry.getAns32();
            case 33 -> answerEntry.getAns33();
            case 34 -> answerEntry.getAns34();
            case 35 -> answerEntry.getAns35();
            case 36 -> answerEntry.getAns36();
            case 37 -> answerEntry.getAns37();
            case 38 -> answerEntry.getAns38();
            case 39 -> answerEntry.getAns39();
            case 40 -> answerEntry.getAns40();
            case 41 -> answerEntry.getAns41();
            case 42 -> answerEntry.getAns42();
            case 43 -> answerEntry.getAns43();
            case 44 -> answerEntry.getAns44();
            case 45 -> answerEntry.getAns45();
            case 46 -> answerEntry.getAns46();
            case 47 -> answerEntry.getAns47();
            case 48 -> answerEntry.getAns48();
            case 49 -> answerEntry.getAns49();
            case 50 -> answerEntry.getAns50();
            case 51 -> answerEntry.getAns51();
            case 52 -> answerEntry.getAns52();
            case 53 -> answerEntry.getAns53();
            case 54 -> answerEntry.getAns54();
            case 55 -> answerEntry.getAns55();
            case 56 -> answerEntry.getAns56();
            case 57 -> answerEntry.getAns57();
            case 58 -> answerEntry.getAns58();
            case 59 -> answerEntry.getAns59();
            case 60 -> answerEntry.getAns60();
            

            default -> null;
        };
    }
}
