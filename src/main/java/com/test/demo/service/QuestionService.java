package com.test.demo.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.test.demo.model.Question;
import com.test.demo.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    public List<Question> saveQuestions(List<Question> questions) {
        return questionRepository.saveAll(questions);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
}