package com.foodtoeat.service.impl;

import com.foodtoeat.pojo.dto.FoodCategoryFilterDTO;
import com.foodtoeat.pojo.dto.FoodDeciderRequestDTO;
import com.foodtoeat.pojo.dto.FoodRecommendationRejectDTO;
import com.foodtoeat.pojo.entity.Food;
import com.foodtoeat.pojo.entity.Question;
import com.foodtoeat.pojo.vo.FoodDeciderResponseVO;
import com.foodtoeat.service.FoodService;
import com.foodtoeat.service.QuestionService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.foodtoeat.service.RecommendationService;

import java.time.Duration;
import java.util.*;

@Service
public class RecommendationServiceImpl implements RecommendationService{

    private static final String KEY_RECOMMENDATION = "recommendation:";
    private static final Duration SESSION_TTL = Duration.ofMinutes(30);
    private final QuestionService questionService;
    private final FoodService foodService;
    private final RedisTemplate<String, Object> redisTemplate;

    public RecommendationServiceImpl(
            QuestionService questionService,
            FoodService foodService,
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.questionService = questionService;
        this.foodService = foodService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public FoodDeciderResponseVO startDecision() {
        // 1. Generate a unique session ID
        UUID sessionId = UUID.randomUUID();

        String questionKey = getQuestionKey(sessionId);
        String rejectedKey = getRejectedKey(sessionId);
        String answersKey = getAnswersKey(sessionId);

        // Create empty state
        List<Integer> askedQuestionIds = new ArrayList<>();
        List<Integer> rejectedFoodIds = new ArrayList<>();
        List<Integer> questionAnswers = new ArrayList<>();

        // Get first question
        Question question = questionService.getFirstQuestion();

        askedQuestionIds.add(question.getId());

        // Store state in Redis
        redisTemplate.opsForValue().set(
                questionKey,
                askedQuestionIds,
                SESSION_TTL
        );

        redisTemplate.opsForValue().set(
                rejectedKey,
                rejectedFoodIds,
                SESSION_TTL
        );

        redisTemplate.opsForValue().set(
                answersKey,
                questionAnswers,
                SESSION_TTL
        );

        return FoodDeciderResponseVO.builder()
                .sessionId(sessionId)
                .question(question)
                .recommendation(null)
                .build();
    }

    @Override
    public FoodDeciderResponseVO answerQuestion(FoodDeciderRequestDTO foodDeciderRequestDTO) {
        UUID sessionId = foodDeciderRequestDTO.getSessionId();

        String questionKey = getQuestionKey(sessionId);
        String rejectedKey = getRejectedKey(sessionId);
        String answersKey = getAnswersKey(sessionId);
        List<Integer> askedQuestionIds = getList(questionKey);
        List<Integer> rejectedFoodIds = getList(rejectedKey);
        List<Boolean> questionAnswers = getList(answersKey);

        if (askedQuestionIds.isEmpty()) {
            // session expired or does not exist
            return startDecision();
        }
        questionAnswers.add(foodDeciderRequestDTO.getAnswer());
        FoodCategoryFilterDTO foodCategoryFilterDTO = buildFoodCategoryFilter(
                askedQuestionIds,
                questionAnswers,
                rejectedFoodIds
        );

        Food food = foodService.getFoodByCategory(foodCategoryFilterDTO);

        return FoodDeciderResponseVO.builder()
                .sessionId(sessionId)
                .question(null)
                .recommendation(food)
                .build();
    }

    @Override
    public FoodDeciderResponseVO rejectRecommendation(FoodRecommendationRejectDTO foodRecommendationRejectDTO) {
        UUID sessionId = foodRecommendationRejectDTO.getSessionId();

        String questionKey = getQuestionKey(sessionId);
        String rejectedKey = getRejectedKey(sessionId);

        List<Integer> rejectedFoodIds = getList(rejectedKey);
        List<Integer> askedQuestionIds = getList(questionKey);

        if (askedQuestionIds.isEmpty()) {
            // session expired or does not exist
            return startDecision();
        }

        rejectedFoodIds.add(foodRecommendationRejectDTO.getFoodId());
        Question nextQuestion = questionService.getNextQuestion(askedQuestionIds);

        redisTemplate.opsForValue().set(
                questionKey,
                askedQuestionIds,
                SESSION_TTL
        );

        redisTemplate.opsForValue().set(
                rejectedKey,
                rejectedFoodIds,
                SESSION_TTL
        );

        return FoodDeciderResponseVO.builder()
                .sessionId(sessionId)
                .question(nextQuestion)
                .recommendation(null)
                .build();
    }

    @Override
    public void acceptRecommendation(UUID sessionId) {
        redisTemplate.delete(
                getQuestionKey(sessionId)
        );

        redisTemplate.delete(
                getRejectedKey(sessionId)
        );

        redisTemplate.delete(
                getAnswersKey(sessionId)
        );
    }

    @Override
    public Food random() {
        return foodService.random();
    }


    @SuppressWarnings("unchecked")
    private <T> List<T> getList(String key) {
        List<T> list = (List<T>) redisTemplate
                .opsForValue()
                .get(key);

        return list != null ? list : new ArrayList<>();
    }

    private String getQuestionKey(UUID sessionId) {

        return sessionId
                + KEY_RECOMMENDATION
                + ":questions";
    }

    private String getRejectedKey(UUID sessionId) {

        return sessionId
                + KEY_RECOMMENDATION
                + ":rejected";
    }

    private String getAnswersKey(UUID sessionId) {

        return sessionId
                + KEY_RECOMMENDATION
                + ":answers";
    }

    private FoodCategoryFilterDTO buildFoodCategoryFilter(
            List<Integer> askedQuestionIds,
            List<Boolean> questionAnswers,
            List<Integer> rejectedFoodIds
    ) {
        List<Integer> includeCategoryIds = new ArrayList<>();
        List<Integer> excludeCategoryIds = new ArrayList<>();

        for (int i = 0; i < askedQuestionIds.size(); i++) {

            Integer questionId = askedQuestionIds.get(i);
            Boolean answer = questionAnswers.get(i);

            Integer categoryId =
                    questionService.getCategoryIdByQuestionId(questionId);

            if (answer) {
                includeCategoryIds.add(categoryId);
            } else {
                excludeCategoryIds.add(categoryId);
            }
        }

        return FoodCategoryFilterDTO.builder()
                .includeCategoryIds(includeCategoryIds)
                .excludeCategoryIds(excludeCategoryIds)
                .rejectedFoodIds(rejectedFoodIds)
                .build();
    }
}
