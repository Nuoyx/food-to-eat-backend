package com.foodtoeat.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CategoryMapper {
    @Select("SELECT name FROM cuisines ORDER BY name")
    List<String> cuisines();
    @Select("SELECT name FROM types ORDER BY name")
    List<String> types();
    @Select("SELECT name FROM ingredients ORDER BY name")
    List<String> ingredients();
    @Select("SELECT name FROM flavors ORDER BY name")
    List<String> flavors();
}
