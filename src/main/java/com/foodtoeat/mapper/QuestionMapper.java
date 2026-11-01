package com.foodtoeat.mapper;

import com.foodtoeat.pojo.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper {
    @Select("SELECT * FROM questions ORDER BY RANDOM() LIMIT 1")
    Question getRandomQuestion();

    @Select("select count(*) from questions")
    int count();

    @Select("select * from questions where id = #{targetId}")
    Question getQuestionById(int targetId);

    // Read the category directly, without depending on snake_case entity mapping.
    @Select("select category_id from questions where id = #{questionId}")
    Integer getCategoryIdByQuestionId(Integer questionId);

    List<Question> getNextQuestion(List<Integer> askedQuestionIds);
}
