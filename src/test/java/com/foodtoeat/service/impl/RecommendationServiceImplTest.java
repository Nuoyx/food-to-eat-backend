package com.foodtoeat.service.impl;

import com.foodtoeat.pojo.dto.FoodCategoryFilterDTO;
import com.foodtoeat.pojo.dto.FoodDeciderRequestDTO;
import com.foodtoeat.pojo.dto.FoodRecommendationRejectDTO;
import com.foodtoeat.pojo.entity.DecisionSession;
import com.foodtoeat.pojo.entity.DecisionSession.Stage;
import com.foodtoeat.pojo.vo.FoodVO;
import com.foodtoeat.pojo.entity.Question;
import com.foodtoeat.service.FoodService;
import com.foodtoeat.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.util.SerializationUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RecommendationServiceImplTest {
    private QuestionService questions;
    private FoodService foods;
    private RedisTemplate<String, Object> redis;
    private ValueOperations<String, Object> values;
    private RecommendationServiceImpl service;
    private final Map<String, byte[]> stored = new HashMap<>();

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setup() {
        questions = mock(QuestionService.class);
        foods = mock(FoodService.class);
        redis = mock(RedisTemplate.class);
        values = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(values);
        // Serialize every write and deserialize every read, as Redis does.
        // Shared in-memory references would hide missing persistence bugs.
        doAnswer(call -> {
            stored.put(call.getArgument(0), SerializationUtils.serialize(call.getArgument(1)));
            return null;
        }).when(values).set(anyString(), any(), eq(Duration.ofMinutes(30)));
        when(values.get(anyString())).thenAnswer(call -> {
            byte[] bytes = stored.get(call.getArgument(0));
            return bytes == null ? null : SerializationUtils.deserialize(bytes);
        });
        when(questions.getFirstQuestion()).thenReturn(new Question(1, 10, "Spicy?"));
        when(questions.getCategoryIdByQuestionId(1)).thenReturn(10);
        when(questions.getCategoryIdByQuestionId(2)).thenReturn(20);
        when(foods.getRandomFoodByCategory(any())).thenReturn(new FoodVO());
        service = new RecommendationServiceImpl(questions, foods, redis);
    }

    @Test
    void preservesAnswersAndAdvancesQuestionsAcrossRedisRoundTrips() {
        UUID id = service.startDecision().getSessionId();
        assertEquals(Stage.AWAITING_ANSWER, state(id).getStage());
        assertEquals(1, state(id).getCurrentQuestionId());

        service.answerQuestion(answer(id, true));
        assertEquals(Map.of(1, true), state(id).getAnswers());
        assertEquals(Stage.AWAITING_FEEDBACK, state(id).getStage());

        when(questions.getNextQuestion(List.of(1)))
                .thenReturn(new Question(2, 20, "Vegetarian?"));
        assertEquals(2, service.rejectRecommendation(rejection(id, 7)).getQuestion().getId());
        assertEquals(2, state(id).getCurrentQuestionId());
        assertEquals(Stage.AWAITING_ANSWER, state(id).getStage());

        service.answerQuestion(answer(id, false));
        assertEquals(Map.of(1, true, 2, false), state(id).getAnswers());
        ArgumentCaptor<FoodCategoryFilterDTO> filters =
                ArgumentCaptor.forClass(FoodCategoryFilterDTO.class);
        verify(foods, times(2)).getRandomFoodByCategory(filters.capture());
        FoodCategoryFilterDTO last = filters.getAllValues().get(1);
        assertEquals(List.of(10), last.getIncludeCategoryIds());
        assertEquals(List.of(20), last.getExcludeCategoryIds());
        assertEquals(List.of(7), last.getRejectedFoodIds());
        verify(values, times(4)).set(eq("recommendation:" + id),
                any(DecisionSession.class), eq(Duration.ofMinutes(30)));

        service.acceptRecommendation(id);
        verify(redis).delete("recommendation:" + id);
    }

    @Test
    void expiredSessionReturnsNotFoundWithoutStartingAnotherSession() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.answerQuestion(answer(UUID.randomUUID(), true)));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verifyNoInteractions(questions, foods);
    }

    @Test
    void rejectsOutOfOrderRequestsWithoutChangingSavedState() {
        UUID id = service.startDecision().getSessionId();
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                () -> service.rejectRecommendation(rejection(id, 7))).getStatusCode());
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                () -> service.acceptRecommendation(id)).getStatusCode());
        service.answerQuestion(answer(id, true));
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                () -> service.answerQuestion(answer(id, false))).getStatusCode());
        assertEquals(Map.of(1, true), state(id).getAnswers());
        verify(values, times(2)).set(anyString(), any(), eq(Duration.ofMinutes(30)));
    }

    @Test
    void exhaustsSessionWhenNoQuestionsRemain() {
        UUID id = service.startDecision().getSessionId();
        service.answerQuestion(answer(id, true));
        when(questions.getNextQuestion(List.of(1))).thenReturn(null);
        assertEquals(Stage.EXHAUSTED, service.rejectRecommendation(rejection(id, 7)).getStage());
        assertNull(state(id).getCurrentQuestionId());
    }

    @Test
    void exhaustsSessionWhenFoodServiceReturnsNoMatch() {
        UUID id = service.startDecision().getSessionId();
        when(foods.getRandomFoodByCategory(any())).thenReturn(null);
        assertEquals(Stage.EXHAUSTED, service.answerQuestion(answer(id, true)).getStage());
        assertEquals(Map.of(1, true), state(id).getAnswers());
    }

    @Test
    void rejectsMissingInputAndUnavailableQuestions() {
        assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class,
                () -> service.answerQuestion(answer(null, true))).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class,
                () -> service.answerQuestion(answer(UUID.randomUUID(), null))).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class,
                () -> service.rejectRecommendation(rejection(UUID.randomUUID(), null))).getStatusCode());
        when(questions.getFirstQuestion()).thenReturn(null);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, assertThrows(ResponseStatusException.class,
                () -> service.startDecision()).getStatusCode());
        assertTrue(stored.isEmpty());
    }

    private DecisionSession state(UUID id) {
        return (DecisionSession) SerializationUtils.deserialize(stored.get("recommendation:" + id));
    }

    private FoodDeciderRequestDTO answer(UUID id, Boolean answer) {
        FoodDeciderRequestDTO request = new FoodDeciderRequestDTO();
        request.setSessionId(id);
        request.setAnswer(answer);
        return request;
    }

    private FoodRecommendationRejectDTO rejection(UUID id, Integer foodId) {
        FoodRecommendationRejectDTO request = new FoodRecommendationRejectDTO();
        request.setSessionId(id);
        request.setFoodId(foodId);
        return request;
    }
}
