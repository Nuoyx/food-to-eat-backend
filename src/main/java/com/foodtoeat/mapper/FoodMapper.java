package com.foodtoeat.mapper;

import com.foodtoeat.pojo.dto.FoodCategoryFilterDTO;
import com.foodtoeat.pojo.dto.FoodPageQueryDTO;
import com.foodtoeat.pojo.vo.FoodVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper
public interface FoodMapper {
    // MyBatis reads rows; this adapter constructs Spring's Page with its total.
    default Page<FoodVO> pageQuery(FoodPageQueryDTO query) {
        if (query == null || query.getPage() < 1 || query.getPageSize() < 1
                || query.getPageSize() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Page must be at least 1 and pageSize must be between 1 and 100");
        }
        long total = countByQuery(query);
        return new PageImpl<>(pageRows(query),
                PageRequest.of(query.getPage() - 1, query.getPageSize()), total);
    }

    List<FoodVO> pageRows(FoodPageQueryDTO query);

    long countByQuery(FoodPageQueryDTO query);

    FoodVO getFoodById(Integer id);

    List<FoodVO> getFoodBySearch(String keyword);

    @Select("select count(*) from foods")
    Integer count();

    FoodVO getRandomFood();

    FoodVO getRandomFoodByCategory(FoodCategoryFilterDTO foodCategoryFilterDTO);
}
