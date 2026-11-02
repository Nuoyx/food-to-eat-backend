package com.foodtoeat.pojo.vo;

import com.foodtoeat.pojo.entity.DecisionSession.Stage;
import com.foodtoeat.pojo.vo.FoodVO;
import com.foodtoeat.pojo.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodDeciderResponseVO {
    private UUID sessionId;
    private Stage stage;
    private Question question;
    private FoodVO recommendation;
}
