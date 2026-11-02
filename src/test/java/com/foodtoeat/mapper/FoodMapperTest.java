package com.foodtoeat.mapper;

import com.foodtoeat.pojo.dto.FoodCategoryFilterDTO;
import com.foodtoeat.pojo.dto.FoodPageQueryDTO;
import com.foodtoeat.pojo.vo.FoodVO;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoodMapperTest {
    @Test
    void pageContractReturnsRecordsAndMatchingTotal() {
        FoodMapper mapper = mock(FoodMapper.class, CALLS_REAL_METHODS);
        FoodPageQueryDTO query = new FoodPageQueryDTO();
        query.setPage(2);
        List<FoodVO> records = List.of(new FoodVO());
        when(mapper.countByQuery(query)).thenReturn(21L);
        when(mapper.pageRows(query)).thenReturn(records);

        Page<FoodVO> page = mapper.pageQuery(query);

        assertEquals(21L, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertEquals(records, page.getContent());
        assertEquals(20L, query.getOffset());
    }

    @Test
    void invalidPageDoesNotReachDatabase() {
        FoodMapper mapper = mock(FoodMapper.class, CALLS_REAL_METHODS);
        FoodPageQueryDTO query = new FoodPageQueryDTO();
        query.setPage(0);
        assertThrows(ResponseStatusException.class, () -> mapper.pageQuery(query));
        verify(mapper, never()).pageRows(any());
        verify(mapper, never()).countByQuery(any());
    }

    @Test
    void xmlBindsNormalizedFiltersAndArrayMappings() throws Exception {
        Configuration config = configuration();
        FoodPageQueryDTO query = new FoodPageQueryDTO();
        query.setCuisine("Chinese");
        query.setTypes(List.of("Stir-Fry"));
        query.setIngredients(List.of("Chicken"));
        query.setFlavor(List.of("Spicy"));

        String rows = sql(config, "pageRows", query);
        String count = sql(config, "countByQuery", query);
        for (String table : List.of("cuisines", "food_types", "food_ingredients", "food_flavors")) {
            assertTrue(rows.contains(table));
            assertTrue(count.contains(table));
        }
        assertTrue(rows.contains("LIMIT ? OFFSET ?"));
        assertFalse(count.contains("LIMIT"));
        assertTrue(config.getResultMap("com.foodtoeat.mapper.FoodMapper.foodVOResultMap")
                .getResultMappings().stream().anyMatch(mapping ->
                        "ingredients".equals(mapping.getProperty())
                                && mapping.getTypeHandler().getClass().getSimpleName()
                                .equals("StringArrayListTypeHandler")));
    }

    @Test
    void decisionFiltersUseDerivedMembershipAndReturnOneFood() throws Exception {
        FoodCategoryFilterDTO filter = FoodCategoryFilterDTO.builder()
                .includeCategoryIds(List.of(1, 2))
                .excludeCategoryIds(List.of(3, 5))
                .rejectedFoodIds(List.of(4)).build();
        String sql = sql(configuration(), "getRandomFoodByCategory", filter);
        assertTrue(sql.contains("food_category_membership"));
        assertTrue(sql.contains("WHERE EXISTS ("));
        assertTrue(sql.contains("AND NOT EXISTS ("));
        assertTrue(sql.contains("fc.food_id = foods.id AND fc.category_id IN ( ? , ? )"));
        assertFalse(sql.contains("HAVING"));
        assertTrue(sql.contains("NOT IN"));
        assertTrue(sql.contains("ORDER BY RANDOM() LIMIT 1"));

        // This reproduces parameter resolution during actual query execution.
        // Merely building BoundSql did not catch the former collection.size binding.
        Configuration config = configuration();
        var statement = config.getMappedStatement(
                "com.foodtoeat.mapper.FoodMapper.getRandomFoodByCategory");
        var boundSql = statement.getBoundSql(filter);
        var executor = config.newExecutor(null);
        assertDoesNotThrow(() -> executor.createCacheKey(
                statement, filter, RowBounds.DEFAULT, boundSql));
    }

    @Test
    void emptyDecisionFiltersDoNotGenerateEmptyInClauses() throws Exception {
        for (FoodCategoryFilterDTO filter : List.of(
                FoodCategoryFilterDTO.builder().build(),
                FoodCategoryFilterDTO.builder().includeCategoryIds(List.of())
                        .excludeCategoryIds(List.of()).rejectedFoodIds(List.of()).build())) {
            String sql = sql(configuration(), "getRandomFoodByCategory", filter);
            assertTrue(sql.endsWith("FROM foods ORDER BY RANDOM() LIMIT 1"));
            assertFalse(sql.contains("IN ("));
        }
    }

    @Test
    void exclusionsApplyEvenWithoutIncludes() throws Exception {
        String sql = sql(configuration(), "getRandomFoodByCategory",
                FoodCategoryFilterDTO.builder().excludeCategoryIds(List.of(3, 5)).build());
        assertTrue(sql.contains("WHERE NOT EXISTS ("));
        assertFalse(sql.contains("WHERE EXISTS ("));
        assertFalse(sql.contains("HAVING"));
    }

    private Configuration configuration() throws Exception {
        Configuration config = new Configuration();
        String resource = "com/foodtoeat/mapper/FoodMapper.xml";
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resource)) {
            assertNotNull(input);
            new XMLMapperBuilder(input, config, resource, config.getSqlFragments()).parse();
        }
        return config;
    }

    private String sql(Configuration config, String method, Object parameter) {
        return config.getMappedStatement("com.foodtoeat.mapper.FoodMapper." + method)
                .getBoundSql(parameter).getSql().replaceAll("\\s+", " ").trim();
    }
}
