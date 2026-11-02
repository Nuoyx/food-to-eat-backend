package com.foodtoeat.controller;

import com.foodtoeat.mapper.CategoryMapper;
import com.foodtoeat.result.Result;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryMapper mapper;
    public CategoryController(CategoryMapper mapper) { this.mapper = mapper; }

    @GetMapping
    public Result<Map<String, List<String>>> options() {
        return Result.success(Map.of(
                "cuisine", mapper.cuisines(), "types", mapper.types(),
                "ingredients", mapper.ingredients(), "flavor", mapper.flavors()));
    }
}
