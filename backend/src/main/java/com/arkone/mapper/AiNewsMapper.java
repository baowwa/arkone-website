package com.arkone.mapper;

import com.arkone.entity.AiNews;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * AI新闻Mapper接口
 *
 * @author ArkOne
 * @since 2024-01-01
 */
@Mapper
public interface AiNewsMapper extends BaseMapper<AiNews> {

    /**
     * 分页查询AI新闻
     */
    IPage<AiNews> selectNewsPage(Page<AiNews> page, @Param("query") Object query);

    /**
     * 获取热门新闻
     */
    @Select("SELECT * FROM ai_news WHERE status = 'published' AND is_hot = 1 AND deleted = 0 ORDER BY published_at DESC LIMIT #{limit}")
    List<AiNews> selectHotNews(@Param("limit") Integer limit);

    /**
     * 获取最新新闻
     */
    @Select("SELECT * FROM ai_news WHERE status = 'published' AND deleted = 0 ORDER BY published_at DESC LIMIT #{limit}")
    List<AiNews> selectLatestNews(@Param("limit") Integer limit);

    /**
     * 根据分类获取新闻
     */
    @Select("SELECT * FROM ai_news WHERE category = #{category} AND status = 'published' AND deleted = 0 ORDER BY published_at DESC LIMIT #{limit}")
    List<AiNews> selectNewsByCategory(@Param("category") String category, @Param("limit") Integer limit);

    /**
     * 根据来源获取新闻
     */
    @Select("SELECT * FROM ai_news WHERE source = #{source} AND status = 'published' AND deleted = 0 ORDER BY published_at DESC LIMIT #{limit}")
    List<AiNews> selectNewsBySource(@Param("source") String source, @Param("limit") Integer limit);

    /**
     * 根据标签获取新闻
     */
    List<AiNews> selectNewsByTag(@Param("tag") String tag, @Param("limit") Integer limit);

    /**
     * 搜索新闻
     */
    IPage<AiNews> searchNews(Page<AiNews> page, @Param("keyword") String keyword);

    /**
     * 增加浏览量
     */
    @Update("UPDATE ai_news SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);

    /**
     * 增加点赞量
     */
    @Update("UPDATE ai_news SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(@Param("id") Long id);

    /**
     * 减少点赞量
     */
    @Update("UPDATE ai_news SET like_count = like_count - 1 WHERE id = #{id} AND like_count > 0")
    int decrementLikeCount(@Param("id") Long id);

    /**
     * 获取新闻分类列表
     */
    @Select("SELECT DISTINCT category FROM ai_news WHERE category IS NOT NULL AND category != '' AND deleted = 0")
    List<String> selectDistinctCategories();

    /**
     * 获取新闻来源列表
     */
    @Select("SELECT DISTINCT source FROM ai_news WHERE source IS NOT NULL AND source != '' AND deleted = 0")
    List<String> selectDistinctSources();

    /**
     * 获取新闻统计信息
     */
    @Select("SELECT COUNT(*) as total, " +
            "SUM(CASE WHEN status = 'published' THEN 1 ELSE 0 END) as published, " +
            "SUM(CASE WHEN is_hot = 1 THEN 1 ELSE 0 END) as hot " +
            "FROM ai_news WHERE deleted = 0")
    Object getNewsStats();

    /**
     * 检查新闻是否已存在（根据标题和来源URL）
     */
    @Select("SELECT COUNT(*) FROM ai_news WHERE title = #{title} AND source_url = #{sourceUrl} AND deleted = 0")
    int checkNewsExists(@Param("title") String title, @Param("sourceUrl") String sourceUrl);
}