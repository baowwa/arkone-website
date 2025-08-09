package com.arkone.service.impl;

import com.arkone.entity.Category;
import com.arkone.mapper.CategoryMapper;
import com.arkone.service.CategoryService;
import com.arkone.dto.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public Result<List<Category>> getCategoriesByType(Category.CategoryType type) {
        try {
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Category::getDeleted, 0)
                   .eq(Category::getStatus, Category.CategoryStatus.ACTIVE)
                   .eq(Category::getType, type)
                   .orderByAsc(Category::getSortOrder)
                   .orderByAsc(Category::getCreatedAt);
            
            List<Category> categories = categoryMapper.selectList(wrapper);
            return Result.success(categories);
        } catch (Exception e) {
            log.error("根据类型获取分类列表失败，类型: {}", type, e);
            return Result.error("获取失败");
        }
    }

    @Override
    public Result<List<Category>> getAllEnabledCategories() {
        try {
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Category::getDeleted, 0)
                   .eq(Category::getStatus, Category.CategoryStatus.ACTIVE)
                   .orderByAsc(Category::getSortOrder)
                   .orderByAsc(Category::getCreatedAt);
            
            List<Category> categories = categoryMapper.selectList(wrapper);
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取所有启用分类失败", e);
            return Result.error("获取失败");
        }
    }

    @Override
    public Result<List<Category>> getCategoriesByParentId(Long parentId) {
        try {
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Category::getDeleted, 0)
                   .eq(Category::getStatus, Category.CategoryStatus.ACTIVE)
                   .eq(Category::getParentId, parentId)
                   .orderByAsc(Category::getSortOrder)
                   .orderByAsc(Category::getCreatedAt);
            
            List<Category> categories = categoryMapper.selectList(wrapper);
            return Result.success(categories);
        } catch (Exception e) {
            log.error("根据父分类ID获取子分类失败，父分类ID: {}", parentId, e);
            return Result.error("获取失败");
        }
    }

    @Override
    public Result<Category> getCategoryById(Long id) {
        try {
            Category category = categoryMapper.selectById(id);
            if (category == null || category.getDeleted() == 1) {
                return Result.error("分类不存在");
            }
            return Result.success(category);
        } catch (Exception e) {
            log.error("获取分类详情失败，ID: {}", id, e);
            return Result.error("获取失败");
        }
    }

    @Override
    @Transactional
    public boolean saveCategory(Category category) {
        try {
            // 检查名称是否重复
            if (existsByName(category.getName(), category.getType(), null)) {
                log.warn("分类名称已存在: {}", category.getName());
                return false;
            }
            
            category.setCreatedAt(LocalDateTime.now());
            category.setUpdatedAt(LocalDateTime.now());
            category.setDeleted(0);
            
            if (category.getStatus() == null) {
                category.setStatus(Category.CategoryStatus.ACTIVE);
            }
            
            if (category.getSortOrder() == null) {
                category.setSortOrder(0);
            }
            
            return categoryMapper.insert(category) > 0;
        } catch (Exception e) {
            log.error("创建分类失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateCategory(Category category) {
        try {
            Category existing = categoryMapper.selectById(category.getId());
            if (existing == null || existing.getDeleted() == 1) {
                return false;
            }
            
            // 检查名称是否重复（排除自己）
            if (existsByName(category.getName(), category.getType(), category.getId())) {
                log.warn("分类名称已存在: {}", category.getName());
                return false;
            }
            
            category.setUpdatedAt(LocalDateTime.now());
            return categoryMapper.updateById(category) > 0;
        } catch (Exception e) {
            log.error("更新分类失败，ID: {}", category.getId(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long id) {
        try {
            // 检查是否有子分类
            LambdaQueryWrapper<Category> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.eq(Category::getParentId, id)
                       .eq(Category::getDeleted, 0);
            Long childCount = categoryMapper.selectCount(childWrapper);
            if (childCount > 0) {
                log.warn("分类下存在子分类，无法删除，ID: {}", id);
                return false;
            }
            
            // 检查分类下是否有内容
            Long contentCount = getContentCountByCategoryId(id).getData();
            if (contentCount != null && contentCount > 0) {
                log.warn("分类下存在内容，无法删除，ID: {}", id);
                return false;
            }
            
            LambdaUpdateWrapper<Category> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Category::getId, id)
                   .set(Category::getDeleted, 1)
                   .set(Category::getUpdatedAt, LocalDateTime.now());
            
            return categoryMapper.update(null, wrapper) > 0;
        } catch (Exception e) {
            log.error("删除分类失败，ID: {}", id, e);
            return false;
        }
    }

    @Override
    public boolean existsByName(String name, Category.CategoryType type, Long excludeId) {
        try {
            if (!StringUtils.hasText(name)) {
                return false;
            }
            
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Category::getName, name)
                   .eq(Category::getType, type)
                   .eq(Category::getDeleted, 0);
            
            if (excludeId != null) {
                wrapper.ne(Category::getId, excludeId);
            }
            
            return categoryMapper.selectCount(wrapper) > 0;
        } catch (Exception e) {
            log.error("检查分类名称是否存在失败，名称: {}", name, e);
            return false;
        }
    }

    @Override
    public Result<Long> getContentCountByCategoryId(Long categoryId) {
        try {
            // 这里需要根据实际业务逻辑统计分类下的内容数量
            // 可能需要查询文章表、AI新闻表等
            // 暂时返回0
            return Result.success(0L);
        } catch (Exception e) {
            log.error("获取分类下内容数量失败，分类ID: {}", categoryId, e);
            return Result.error("获取失败");
        }
    }

    @Override
    public Result<List<Category>> buildCategoryTree(Category.CategoryType type) {
        try {
            // 获取所有该类型的分类
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Category::getDeleted, 0)
                   .eq(Category::getStatus, Category.CategoryStatus.ACTIVE)
                   .eq(Category::getType, type)
                   .orderByAsc(Category::getSortOrder)
                   .orderByAsc(Category::getCreatedAt);
            
            List<Category> allCategories = categoryMapper.selectList(wrapper);
            
            // 构建树形结构
            List<Category> tree = buildTree(allCategories, null);
            
            return Result.success(tree);
        } catch (Exception e) {
            log.error("构建分类树失败，类型: {}", type, e);
            return Result.error("构建失败");
        }
    }

    /**
     * 递归构建分类树
     */
    private List<Category> buildTree(List<Category> allCategories, Long parentId) {
        List<Category> tree = new ArrayList<>();
        
        for (Category category : allCategories) {
            if ((parentId == null && category.getParentId() == null) ||
                (parentId != null && parentId.equals(category.getParentId()))) {
                
                // 递归查找子分类
                List<Category> children = buildTree(allCategories, category.getId());
                // 注意：Category实体类中没有children字段，这里需要根据实际需求处理
                
                tree.add(category);
            }
        }
        
        return tree;
    }
}