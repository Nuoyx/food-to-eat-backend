package com.foodtoeat.mapper;

import com.foodtoeat.pojo.dto.FoodCategoryFilterDTO;
import com.foodtoeat.pojo.dto.FoodPageQueryDTO;
import com.foodtoeat.pojo.entity.Food;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper
public interface FoodMapper {
    Page<Food> pageQuery(FoodPageQueryDTO foodPageQueryDTO);

    @Select("select * from foods where id = #{id}")
    Food getFoodById(Integer id);

    @Select("select * from foods where name like concat('%',#{keyword},'%')")
    List<Food> getFoodBySearch(String keyword);

    @Select("select count(*) from foods")
    Integer count();

    List<Food> getFoodByCategory(FoodCategoryFilterDTO foodCategoryFilterDTO);
}
