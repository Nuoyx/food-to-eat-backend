package com.foodtoeat.mapper;

import com.foodtoeat.pojo.dto.FoodCategoryFilterDTO;
import com.foodtoeat.pojo.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper {
    @Select("select count(*) from questions")
    int count();

    @Select("select * from questions where id = #{targetId}")
    Question getQuestionById(int targetId);

    List<Question> getNextQuestion(List<Integer> askedQuestionIds);
}
