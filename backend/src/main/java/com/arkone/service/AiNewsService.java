package com.arkone.service;

import com.arkone.entity.AiNews;
import com.arkone.dto.PageQuery;
import com.arkone.dto.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

/**
 * AI新闻服务接口
 */
public interface AiNewsService {

    /**
     * 分页查询AI新闻
     */
    Result<Page<AiNews>> getAiNewsPage(PageQuery query);

    /**
     * 根据ID获取AI新闻详情
     */
    Result<AiNews> getAiNewsById(Long id);

    /**
     * 创建AI新闻
     */
    boolean saveAiNews(AiNews aiNews);

    /**
     * 更新AI新闻
     */
    boolean updateAiNews(AiNews aiNews);

    /**
     * 删除AI新闻
     */
    boolean deleteAiNews(Long id);

    /**
     * 批量删除AI新闻
     */
    boolean deleteAiNews(List<Long> ids);

    /**
     * 发布AI新闻
     */
    boolean publishAiNews(Long id);

    /**
     * 取消发布AI新闻
     */
    boolean unpublishAiNews(Long id);

    /**
     * 设置热门
     */
    boolean setHot(Long id);

    /**
     * 取消热门
     */
    boolean unsetHot(Long id);

    /**
     * 增加浏览量
     */
    boolean increaseViewCount(Long id);

    /**
     * 点赞
     */
    boolean likeAiNews(Long id);

    /**
     * 取消点赞
     */
    boolean unlikeAiNews(Long id);

    /**
     * 获取热门AI新闻
     */
    Result<List<AiNews>> getHotAiNews(Integer limit);

    /**
     * 获取最新AI新闻
     */
    Result<List<AiNews>> getLatestAiNews(Integer limit);

    /**
     * 根据分类获取AI新闻
     */
    Result<Page<AiNews>> getAiNewsByCategory(String category, PageQuery query);

    /**
     * 根据来源获取AI新闻
     */
    Result<Page<AiNews>> getAiNewsBySource(String source, PageQuery query);

    /**
     * 根据标签获取AI新闻
     */
    Result<Page<AiNews>> getAiNewsByTag(String tag, PageQuery query);

    /**
     * 搜索AI新闻
     */
    Result<Page<AiNews>> searchAiNews(String keyword, PageQuery query);

    /**
     * 获取AI新闻分类列表
     */
    Result<List<String>> getAiNewsCategories();

    /**
     * 获取AI新闻来源列表
     */
    Result<List<String>> getAiNewsSources();

    /**
     * 获取AI新闻统计信息
     */
    Result<Map<String, Object>> getAiNewsStats();

    /**
     * 同步AI新闻（从RSS源）
     */
    boolean syncAiNews();

    /**
     * 检查新闻是否已存在
     */
    boolean existsByOriginalUrl(String originalUrl);
}