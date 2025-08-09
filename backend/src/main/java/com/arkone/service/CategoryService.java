package com.arkone.service;

import com.arkone.entity.Category;
import com.arkone.dto.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {

    /**
     * 根据类型获取分类列表
     */
    Result<List<Category>> getCategoriesByType(Category.CategoryType type);

    /**
     * 获取所有启用的分类
     */
    Result<List<Category>> getAllEnabledCategories();

    /**
     * 根据父分类ID获取子分类
     */
    Result<List<Category>> getCategoriesByParentId(Long parentId);

    /**
     * 根据ID获取分类详情
     */
    Result<Category> getCategoryById(Long id);

    /**
     * 创建分类
     */
    boolean saveCategory(Category category);

    /**
     * 更新分类
     */
    boolean updateCategory(Category category);

    /**
     * 删除分类
     */
    boolean deleteCategory(Long id);

    /**
     * 检查分类名称是否存在
     */
    boolean existsByName(String name, Category.CategoryType type, Long excludeId);

    /**
     * 获取分类下的内容数量
     */
    Result<Long> getContentCountByCategoryId(Long categoryId);

    /**
     * 构建分类树
     */
    Result<List<Category>> buildCategoryTree(Category.CategoryType type);
}