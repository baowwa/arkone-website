package com.arkone.mapper;

import com.arkone.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分类Mapper接口
 *
 * @author ArkOne
 * @since 2024-01-01
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据类型获取分类列表
     */
    @Select("SELECT * FROM categories WHERE type = #{type} AND status = 'active' AND deleted = 0 ORDER BY sort_order ASC, created_at ASC")
    List<Category> selectByType(@Param("type") Category.CategoryType type);

    /**
     * 获取所有启用的分类
     */
    @Select("SELECT * FROM categories WHERE status = 'active' AND deleted = 0 ORDER BY type, sort_order ASC")
    List<Category> selectAllActive();

    /**
     * 根据父分类ID获取子分类
     */
    @Select("SELECT * FROM categories WHERE parent_id = #{parentId} AND status = 'active' AND deleted = 0 ORDER BY sort_order ASC")
    List<Category> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 检查分类名称是否存在
     */
    @Select("SELECT COUNT(*) FROM categories WHERE name = #{name} AND type = #{type} AND deleted = 0 AND id != #{excludeId}")
    int checkNameExists(@Param("name") String name, @Param("type") Category.CategoryType type, @Param("excludeId") Long excludeId);

    /**
     * 获取分类下的内容数量
     */
    @Select("SELECT COUNT(*) FROM articles WHERE category_id = #{categoryId} AND deleted = 0")
    int getArticleCountByCategoryId(@Param("categoryId") Long categoryId);
}