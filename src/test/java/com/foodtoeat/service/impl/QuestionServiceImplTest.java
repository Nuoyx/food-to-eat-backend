package com.foodtoeat.service.impl;

import com.foodtoeat.mapper.QuestionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionServiceImplTest {
    private final QuestionMapper mapper = mock(QuestionMapper.class);
    private final QuestionServiceImpl service = new QuestionServiceImpl(mapper);

    @Test
    void resolvesEachQuestionsActualCategory() {
        when(mapper.getCategoryIdByQuestionId(1)).thenReturn(10);
        when(mapper.getCategoryIdByQuestionId(2)).thenReturn(20);

        assertEquals(10, service.getCategoryIdByQuestionId(1));
        assertEquals(20, service.getCategoryIdByQuestionId(2));
    }

    @Test
    void rejectsInvalidQuestionIdsBeforeQueryingDatabase() {
        for (Integer id : new Integer[]{null, 0, -1}) {
            ResponseStatusException error = assertThrows(ResponseStatusException.class,
                    () -> service.getCategoryIdByQuestionId(id));
            assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        }
        verifyNoInteractions(mapper);
    }

    @Test
    void rejectsMissingOrInvalidCategoriesInsteadOfFilteringOnZero() {
        when(mapper.getCategoryIdByQuestionId(1)).thenReturn(null);
        when(mapper.getCategoryIdByQuestionId(2)).thenReturn(0);
        when(mapper.getCategoryIdByQuestionId(3)).thenReturn(-1);

        for (int id : new int[]{1, 2, 3}) {
            ResponseStatusException error = assertThrows(ResponseStatusException.class,
                    () -> service.getCategoryIdByQuestionId(id));
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, error.getStatusCode());
        }
    }
}
