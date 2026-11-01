package com.foodtoeat.service.impl;


import com.foodtoeat.mapper.QuestionMapper;
import com.foodtoeat.pojo.entity.Question;
import com.foodtoeat.service.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Random;

@Service
public class QuestionServiceImpl implements QuestionService {
    Random random = new Random();
    private final QuestionMapper questionMapper;

    public QuestionServiceImpl(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }


    @Override
    public Question getFirstQuestion() {
        int total = questionMapper.count();
        int targetId = random.nextInt(1, total);
        return questionMapper.getQuestionById(targetId);
    }

    @Override
    public Integer getCategoryIdByQuestionId(Integer questionId) {
        if (questionId == null || questionId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A positive question ID is required");
        }

        return questionMapper.getCategoryIdByQuestionId(questionId);
    }

    @Override
    public Question getNextQuestion(List<Integer> askedQuestionIds) {
        List<Question> questions;
        questions = questionMapper.getNextQuestion(askedQuestionIds);

        if (questions.isEmpty()) {
            return null;
        }

        return questions.get(0);
    }
}
