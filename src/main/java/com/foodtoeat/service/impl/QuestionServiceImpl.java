package com.foodtoeat.service.impl;


import com.foodtoeat.mapper.QuestionMapper;
import com.foodtoeat.pojo.entity.Question;
import com.foodtoeat.service.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionMapper questionMapper;

    public QuestionServiceImpl(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }


    @Override
    public Question getFirstQuestion() {
        return questionMapper.getRandomQuestion();
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
