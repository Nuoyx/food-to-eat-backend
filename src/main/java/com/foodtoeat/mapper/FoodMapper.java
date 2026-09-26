package com.foodtoeat.mapper;

import com.foodtoeat.pojo.dto.FoodPageQueryDTO;
import com.foodtoeat.pojo.entity.Food;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FoodMapper {
    Page<Food> pageQuery(FoodPageQueryDTO foodPageQueryDTO);

    @Select("select * from foods where id = #{id}")
    Food getFoodById(Integer id);

    @Select("select * from foods where name like concat('%',#{keyword},'%')")
    Food[] getFoodBySearch(String keyword);

    @Select("select count(*) from foods")
    int count();
}
