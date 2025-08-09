package com.arkone.mapper;

import com.arkone.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 文章Mapper接口
 *
 * @author ArkOne
 * @since 2024-01-01
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 分页查询文章（包含分类名称）
     */
    IPage<Article> selectArticlePageWithCategory(Page<Article> page, @Param("query") Object query);

    /**
     * 根据ID查询文章（包含分类名称）
     */
    Article selectArticleWithCategoryById(@Param("id") Long id);

    /**
     * 获取热门文章
     */
    @Select("SELECT * FROM articles WHERE status = 'published' AND deleted = 0 ORDER BY view_count DESC, created_at DESC LIMIT #{limit}")
    List<Article> selectHotArticles(@Param("limit") Integer limit);

    /**
     * 获取最新文章
     */
    @Select("SELECT * FROM articles WHERE status = 'published' AND deleted = 0 ORDER BY publish_time DESC, created_at DESC LIMIT #{limit}")
    List<Article> selectLatestArticles(@Param("limit") Integer limit);

    /**
     * 获取推荐文章（置顶文章）
     */
    @Select("SELECT * FROM articles WHERE status = 'published' AND is_top = 1 AND deleted = 0 ORDER BY publish_time DESC LIMIT #{limit}")
    List<Article> selectRecommendArticles(@Param("limit") Integer limit);

    /**
     * 根据分类ID获取文章
     */
    @Select("SELECT * FROM articles WHERE category_id = #{categoryId} AND status = 'published' AND deleted = 0 ORDER BY publish_time DESC LIMIT #{limit}")
    List<Article> selectArticlesByCategoryId(@Param("categoryId") Long categoryId, @Param("limit") Integer limit);

    /**
     * 根据标签获取文章
     */
    List<Article> selectArticlesByTag(@Param("tag") String tag, @Param("limit") Integer limit);

    /**
     * 搜索文章
     */
    IPage<Article> searchArticles(Page<Article> page, @Param("keyword") String keyword);

    /**
     * 增加浏览量
     */
    @Update("UPDATE articles SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);

    /**
     * 增加点赞量
     */
    @Update("UPDATE articles SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(@Param("id") Long id);

    /**
     * 减少点赞量
     */
    @Update("UPDATE articles SET like_count = like_count - 1 WHERE id = #{id} AND like_count > 0")
    int decrementLikeCount(@Param("id") Long id);

    /**
     * 获取文章统计信息
     */
    @Select("SELECT COUNT(*) as total, " +
            "SUM(CASE WHEN status = 'published' THEN 1 ELSE 0 END) as published, " +
            "SUM(CASE WHEN status = 'draft' THEN 1 ELSE 0 END) as draft " +
            "FROM articles WHERE deleted = 0")
    Object getArticleStats();
}