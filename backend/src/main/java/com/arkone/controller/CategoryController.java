package com.arkone.controller;

import com.arkone.dto.Result;
import com.arkone.entity.Category;
import com.arkone.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 分类控制器
 *
 * @author arkone
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "分类管理", description = "分类相关接口")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 根据类型获取分类列表
     */
    @GetMapping("/type/{type}")
    @Operation(summary = "根据类型获取分类列表")
    public Result<List<Category>> getCategoriesByType(
            @Parameter(description = "分类类型") @PathVariable String type) {
        Category.CategoryType categoryType = Category.CategoryType.valueOf(type.toUpperCase());
        return categoryService.getCategoriesByType(categoryType);
    }

    /**
     * 获取所有启用的分类
     */
    @GetMapping("/enabled")
    @Operation(summary = "获取所有启用的分类")
    public Result<List<Category>> getAllEnabledCategories() {
        return categoryService.getAllEnabledCategories();
    }

    /**
     * 根据父分类ID获取子分类
     */
    @GetMapping("/children/{parentId}")
    @Operation(summary = "根据父分类ID获取子分类")
    public Result<List<Category>> getChildrenByParentId(
            @Parameter(description = "父分类ID") @PathVariable Long parentId) {
        return categoryService.getCategoriesByParentId(parentId);
    }

    /**
     * 根据ID获取分类详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取分类详情")
    public Result<Category> getCategoryById(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    /**
     * 创建分类
     */
    @PostMapping
    @Operation(summary = "创建分类")
    public Result<Void> createCategory(@Valid @RequestBody Category category) {
        categoryService.saveCategory(category);
        return Result.success();
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新分类")
    public Result<Void> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Valid @RequestBody Category category) {
        category.setId(id);
        categoryService.updateCategory(category);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    /**
     * 检查分类名称是否存在
     */
    @GetMapping("/check-name")
    @Operation(summary = "检查分类名称是否存在")
    public Result<Boolean> checkCategoryNameExists(
            @Parameter(description = "分类名称") @RequestParam String name,
            @Parameter(description = "分类类型") @RequestParam String type,
            @Parameter(description = "排除的分类ID") @RequestParam(required = false) Long excludeId) {
        Category.CategoryType categoryType = Category.CategoryType.valueOf(type.toUpperCase());
        boolean exists = categoryService.existsByName(name, categoryType, excludeId);
        return Result.success(exists);
    }

    /**
     * 获取分类下的内容数量
     */
    @GetMapping("/{id}/content-count")
    @Operation(summary = "获取分类下的内容数量")
    public Result<Map<String, Long>> getCategoryContentCount(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        return categoryService.getContentCountByCategoryId(id).getData() != null ? 
            Result.success(Map.of("count", categoryService.getContentCountByCategoryId(id).getData())) : 
            Result.success(Map.of("count", 0L));
    }

    /**
     * 构建分类树
     */
    @GetMapping("/tree/{type}")
    @Operation(summary = "构建分类树")
    public Result<List<Category>> buildCategoryTree(
            @Parameter(description = "分类类型") @PathVariable String type) {
        Category.CategoryType categoryType = Category.CategoryType.valueOf(type.toUpperCase());
        return categoryService.buildCategoryTree(categoryType);
    }
}