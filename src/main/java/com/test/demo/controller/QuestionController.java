package com.test.demo.controller;


import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import com.test.demo.model.Options;
import com.test.demo.model.Question;
import com.test.demo.service.OptionsService;
import com.test.demo.service.QuestionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final OptionsService optionsService;

    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) {
        return ResponseEntity.ok(questionService.saveQuestion(question));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Question>> createQuestions(@RequestBody List<Question> questions) {
        return ResponseEntity.ok(questionService.saveQuestions(questions));
    }

    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping("/exam")
    public ResponseEntity<List<Map<String, Object>>> getExam() {
        List<Question> questions = questionService.getAllQuestions();
        List<Options> options = optionsService.getAllOptions();

        Map<Integer, List<Options>> optionsMap = options.stream()
                .collect(Collectors.groupingBy(Options::getQuestionId));

        List<Map<String, Object>> examList = new ArrayList<>();
        for (Question q : questions) {
            Map<String, Object> questionMap = new HashMap<>();
            questionMap.put("id", q.getId());
            questionMap.put("name", q.getName());

            List<Map<String, Object>> opts = new ArrayList<>();
            List<Options> qOptions = optionsMap.get(q.getId().intValue());
            if (qOptions != null) {
                for (Options opt : qOptions) {
                    opts.add(Map.of("name", opt.getOptionName(), "marks", opt.getId()));
                }
            }
            Collections.shuffle(opts);
            questionMap.put("options", opts);
            examList.add(questionMap);
        }
        return ResponseEntity.ok(examList);
    }

    @GetMapping("/examQuestion")
    public ResponseEntity<List<Map<String, Object>>> getExamQuestion(@RequestParam String type) {
        List<Question> questions = questionService.findByType(type);
        List<Options> options = optionsService.getAllOptions();

        Map<Integer, List<Options>> optionsMap = options.stream()
                .collect(Collectors.groupingBy(Options::getQuestionId));

        List<Map<String, Object>> examList = new ArrayList<>();
        for (Question q : questions) {
            Map<String, Object> questionMap = new HashMap<>();
            questionMap.put("id", q.getId());
            questionMap.put("name", q.getName());

            List<Map<String, Object>> opts = new ArrayList<>();
            List<Options> qOptions = optionsMap.get(q.getId().intValue());
            if (qOptions != null) {
                for (Options opt : qOptions) {
                    opts.add(Map.of("name", opt.getOptionName(), "marks", opt.getId()));
                }
            }
            Collections.shuffle(opts);
            questionMap.put("options", opts);
            examList.add(questionMap);
        }
        return ResponseEntity.ok(examList);
    }
}