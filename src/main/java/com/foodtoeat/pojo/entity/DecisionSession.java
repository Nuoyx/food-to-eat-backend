package com.foodtoeat.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Data
// Stored as one Redis value, so questions, answers, and rejections share an expiry.
public class DecisionSession implements Serializable {
    // The unanswered question; cleared once a recommendation is produced.
    private Integer currentQuestionId;
    // Question ID -> answer. Keeping the pair together avoids parallel-list mismatches.
    private Map<Integer, Boolean> answers = new LinkedHashMap<>();
    // A set prevents repeated rejections from adding duplicate food IDs.
    private Set<Integer> rejectedFoodIds = new LinkedHashSet<>();
    private Stage stage = Stage.AWAITING_ANSWER;

    public enum Stage {
        // The next request should answer currentQuestionId.
        AWAITING_ANSWER,
        // The next request should accept or reject the offered recommendation.
        AWAITING_FEEDBACK,
        // No matching food or no further questions; the user must start a new flow.
        EXHAUSTED
    }
}
