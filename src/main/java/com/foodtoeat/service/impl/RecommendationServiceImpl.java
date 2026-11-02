package com.foodtoeat.service.impl;

import com.foodtoeat.pojo.dto.FoodCategoryFilterDTO;
import com.foodtoeat.pojo.dto.FoodDeciderRequestDTO;
import com.foodtoeat.pojo.dto.FoodRecommendationRejectDTO;
import com.foodtoeat.pojo.entity.DecisionSession;
import com.foodtoeat.pojo.entity.DecisionSession.Stage;
import com.foodtoeat.pojo.vo.FoodVO;
import com.foodtoeat.pojo.entity.Question;
import com.foodtoeat.pojo.vo.FoodDeciderResponseVO;
import com.foodtoeat.service.FoodService;
import com.foodtoeat.service.QuestionService;
import com.foodtoeat.service.RecommendationService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    private static final String SESSION_PREFIX = "recommendation:";
    private static final Duration SESSION_TTL = Duration.ofMinutes(30);

    private final QuestionService questionService;
    private final FoodService foodService;
    private final RedisTemplate<String, Object> redisTemplate;

    public RecommendationServiceImpl(QuestionService questionService,
                                     FoodService foodService,
                                     RedisTemplate<String, Object> redisTemplate) {
        this.questionService = questionService;
        this.foodService = foodService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public FoodDeciderResponseVO startDecision() {
        Question question = questionService.getFirstQuestion();
        if (question == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No recommendation questions are available");
        }

        UUID sessionId = UUID.randomUUID();
        DecisionSession session = new DecisionSession();

        session.setCurrentQuestionId(question.getId());
        saveSession(sessionId, session);
        return response(sessionId, session, question, null);
    }

    @Override
    public FoodDeciderResponseVO answerQuestion(FoodDeciderRequestDTO request) {
        if (request == null || request.getAnswer() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Answer is required");
        }
        UUID sessionId = request.getSessionId();
        DecisionSession session = loadSession(sessionId);
        requireStage(session, Stage.AWAITING_ANSWER);

        Integer questionId = session.getCurrentQuestionId();
        if (questionId == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "There is no question waiting for an answer");
        }
        // Keep earlier preferences and associate this answer with the pending question.
        session.getAnswers().put(questionId, request.getAnswer());
        FoodVO recommendation = foodService.getRandomFoodByCategory(buildFilter(session));
        // The question is complete; feedback is only valid if a food was found.
        session.setCurrentQuestionId(null);
        session.setStage(recommendation == null ? Stage.EXHAUSTED : Stage.AWAITING_FEEDBACK);
        saveSession(sessionId, session);
        return response(sessionId, session, null, recommendation);
    }

    @Override
    public FoodDeciderResponseVO rejectRecommendation(FoodRecommendationRejectDTO request) {
        if (request == null || request.getFoodId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Rejected food ID is required");
        }
        UUID sessionId = request.getSessionId();
        DecisionSession session = loadSession(sessionId);
        requireStage(session, Stage.AWAITING_FEEDBACK);

        session.getRejectedFoodIds().add(request.getFoodId());
        // Answered question IDs also serve as the list of questions to avoid repeating.
        Question next = questionService.getNextQuestion(
                new ArrayList<>(session.getAnswers().keySet()));
        // Save the new pending question, or explicitly end the flow if none remain.
        session.setCurrentQuestionId(next == null ? null : next.getId());
        session.setStage(next == null ? Stage.EXHAUSTED : Stage.AWAITING_ANSWER);
        saveSession(sessionId, session);
        return response(sessionId, session, next, null);
    }

    @Override
    public void acceptRecommendation(UUID sessionId) {
        DecisionSession session = loadSession(sessionId);
        requireStage(session, Stage.AWAITING_FEEDBACK);
        // Acceptance completes the flow, so its temporary state is no longer needed.
        redisTemplate.delete(sessionKey(sessionId));
    }

    @Override
    public FoodVO random() {
        return foodService.random();
    }

    private void requireStage(DecisionSession session, Stage expected) {
        if (session.getStage() != expected) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This action is not allowed during " + session.getStage());
        }
    }

    private DecisionSession loadSession(UUID sessionId) {
        if (sessionId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session ID is required");
        }
        Object stored = redisTemplate.opsForValue().get(sessionKey(sessionId));
        if (stored == null) {
            // Do not silently replace an expired session and discard the user's action.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Session not found or expired; start a new decision");
        }
        if (!(stored instanceof DecisionSession)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Stored session has an unexpected format");
        }
        return (DecisionSession) stored;
    }

    private void saveSession(UUID sessionId, DecisionSession session) {
        // Redis reads return a deserialized copy: local changes require an explicit save.
        // Each successful transition refreshes the entire session's 30-minute expiry.
        redisTemplate.opsForValue().set(sessionKey(sessionId), session, SESSION_TTL);
    }

    private String sessionKey(UUID sessionId) {
        return SESSION_PREFIX + sessionId;
    }

    private FoodCategoryFilterDTO buildFilter(DecisionSession session) {
        // Multiple questions may map to the same category; filters need unique IDs.
        Set<Integer> included = new LinkedHashSet<>();
        Set<Integer> excluded = new LinkedHashSet<>();
        for (Map.Entry<Integer, Boolean> answer : session.getAnswers().entrySet()) {
            Integer categoryId = questionService.getCategoryIdByQuestionId(answer.getKey());
            if (categoryId == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Answered question has no category");
            }
            // Yes adds an OR option; every no remains an exclusion.
            if (answer.getValue()) {
                included.add(categoryId);
            } else {
                excluded.add(categoryId);
            }
        }
        return FoodCategoryFilterDTO.builder()
                .includeCategoryIds(new ArrayList<>(included))
                .excludeCategoryIds(new ArrayList<>(excluded))
                .rejectedFoodIds(new ArrayList<>(session.getRejectedFoodIds()))
                .build();
    }

    private FoodDeciderResponseVO response(UUID sessionId, DecisionSession session,
                                           Question question, FoodVO food) {
        return FoodDeciderResponseVO.builder()
                .sessionId(sessionId)
                .stage(session.getStage())
                .question(question)
                .recommendation(food)
                .build();
    }
}
