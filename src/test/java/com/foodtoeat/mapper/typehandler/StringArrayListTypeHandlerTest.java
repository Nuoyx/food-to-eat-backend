package com.foodtoeat.mapper.typehandler;

import org.junit.jupiter.api.Test;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StringArrayListTypeHandlerTest {
    private final StringArrayListTypeHandler handler = new StringArrayListTypeHandler();

    @Test
    void readsAttributeNamesAndFreesJdbcArray() throws SQLException {
        ResultSet result = mock(ResultSet.class);
        Array array = mock(Array.class);
        when(result.getArray("types")).thenReturn(array);
        when(array.getArray()).thenReturn(new String[]{"Poultry Dish", "Stir-Fry"});
        assertEquals(List.of("Poultry Dish", "Stir-Fry"), handler.getNullableResult(result, "types"));
        verify(array).free();
    }

    @Test
    void nullAndEmptyRelationsBecomeEmptyLists() throws SQLException {
        ResultSet result = mock(ResultSet.class);
        assertEquals(List.of(), handler.getNullableResult(result, "flavor"));
        Array array = mock(Array.class);
        when(result.getArray("flavor")).thenReturn(array);
        when(array.getArray()).thenReturn(new String[0]);
        assertEquals(List.of(), handler.getNullableResult(result, "flavor"));
        verify(array).free();
    }
}
