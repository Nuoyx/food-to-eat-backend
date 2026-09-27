package com.foodtoeat.service;

import com.foodtoeat.pojo.entity.Question;

import java.util.List;

public interface QuestionService {
    Question getFirstQuestion();

    Integer getCategoryIdByQuestionId(Integer questionId);

    Question getNextQuestion(List<Integer> askedQuestionIds);
}
