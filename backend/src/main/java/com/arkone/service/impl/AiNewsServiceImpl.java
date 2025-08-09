package com.arkone.service.impl;

import com.arkone.entity.AiNews;
import com.arkone.mapper.AiNewsMapper;
import com.arkone.service.AiNewsService;
import com.arkone.dto.PageQuery;
import com.arkone.dto.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI新闻服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiNewsServiceImpl extends ServiceImpl<AiNewsMapper, AiNews> implements AiNewsService {

    private final AiNewsMapper aiNewsMapper;
    private final WebClient webClient = WebClient.builder().build();

    @Value("${app.ai.news.rss-sources}")
    private List<String> rssSources;

    @Override
    public Result<Page<AiNews>> getAiNewsPage(PageQuery query) {
        try {
            Page<AiNews> page = new Page<>(query.getPageNum(), query.getPageSize());
            
            LambdaQueryWrapper<AiNews> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AiNews::getDeleted, 0)
                   .eq(AiNews::getStatus, AiNews.NewsStatus.PUBLISHED)
                   .orderBy(true, query.isAsc(), 
                           StringUtils.hasText(query.getSortField()) ? 
                           getColumnByField(query.getSortField()) : AiNews::getPublishedAt);
            
            if (StringUtils.hasText(query.getKeyword())) {
                wrapper.and(w -> w.like(AiNews::getTitle, query.getKeyword())
                                .or().like(AiNews::getContent, query.getKeyword())
                                .or().like(AiNews::getSummary, query.getKeyword()));
            }
            
            Page<AiNews> result = aiNewsMapper.selectPage(page, wrapper);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询AI新闻失败", e);
            return Result.error("查询失败");
        }
    }

    @Override
    public Result<AiNews> getAiNewsById(Long id) {
        try {
            AiNews aiNews = aiNewsMapper.selectById(id);
            if (aiNews == null || aiNews.getDeleted() == 1) {
                return Result.error("AI新闻不存在");
            }
            
            // 增加浏览量
            increaseViewCount(id);
            
            return Result.success(aiNews);
        } catch (Exception e) {
            log.error("获取AI新闻详情失败，ID: {}", id, e);
            return Result.error("获取失败");
        }
    }

    @Override
    @Transactional
    public boolean saveAiNews(AiNews aiNews) {
        try {
            aiNews.setCreatedAt(LocalDateTime.now());
            aiNews.setUpdatedAt(LocalDateTime.now());
            aiNews.setDeleted(0);
            
            if (aiNews.getStatus() == null) {
                aiNews.setStatus(AiNews.NewsStatus.DRAFT);
            }
            
            if (aiNews.getViewCount() == null) {
                aiNews.setViewCount(0);
            }
            
            if (aiNews.getLikeCount() == null) {
                aiNews.setLikeCount(0);
            }
            
            if (aiNews.getIsHot() == null) {
                aiNews.setIsHot(false);
            }
            
            // 自动生成摘要
            if (!StringUtils.hasText(aiNews.getSummary()) && StringUtils.hasText(aiNews.getContent())) {
                aiNews.setSummary(generateSummary(aiNews.getContent()));
            }
            
            return aiNewsMapper.insert(aiNews) > 0;
        } catch (Exception e) {
            log.error("创建AI新闻失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateAiNews(AiNews aiNews) {
        try {
            AiNews existing = aiNewsMapper.selectById(aiNews.getId());
            if (existing == null || existing.getDeleted() == 1) {
                return false;
            }
            
            aiNews.setUpdatedAt(LocalDateTime.now());
            
            // 自动生成摘要
            if (!StringUtils.hasText(aiNews.getSummary()) && StringUtils.hasText(aiNews.getContent())) {
                aiNews.setSummary(generateSummary(aiNews.getContent()));
            }
            
            return aiNewsMapper.updateById(aiNews) > 0;
        } catch (Exception e) {
            log.error("更新AI新闻失败，ID: {}", aiNews.getId(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteAiNews(Long id) {
        try {
            LambdaUpdateWrapper<AiNews> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(AiNews::getId, id)
                   .set(AiNews::getDeleted, 1)
                   .set(AiNews::getUpdatedAt, LocalDateTime.now());
            
            return aiNewsMapper.update(null, wrapper) > 0;
        } catch (Exception e) {
            log.error("删除AI新闻失败，ID: {}", id, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteAiNews(List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return false;
            }
            
            LambdaUpdateWrapper<AiNews> wrapper = new LambdaUpdateWrapper<>();
            wrapper.in(AiNews::getId, ids)
                   .set(AiNews::getDeleted, 1)
                   .set(AiNews::getUpdatedAt, LocalDateTime.now());
            
            return aiNewsMapper.update(null, wrapper) > 0;
        } catch (Exception e) {
            log.error("批量删除AI新闻失败，IDs: {}", ids, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean publishAiNews(Long id) {
        try {
            LambdaUpdateWrapper<AiNews> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(AiNews::getId, id)
                   .eq(AiNews::getDeleted, 0)
                   .set(AiNews::getStatus, AiNews.NewsStatus.PUBLISHED)
                   .set(AiNews::getPublishedAt, LocalDateTime.now())
                   .set(AiNews::getUpdatedAt, LocalDateTime.now());
            
            return aiNewsMapper.update(null, wrapper) > 0;
        } catch (Exception e) {
            log.error("发布AI新闻失败，ID: {}", id, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean unpublishAiNews(Long id) {
        try {
            LambdaUpdateWrapper<AiNews> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(AiNews::getId, id)
                   .eq(AiNews::getDeleted, 0)
                   .set(AiNews::getStatus, AiNews.NewsStatus.DRAFT)
                   .set(AiNews::getUpdatedAt, LocalDateTime.now());
            
            return aiNewsMapper.update(null, wrapper) > 0;
        } catch (Exception e) {
            log.error("取消发布AI新闻失败，ID: {}", id, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean setHot(Long id) {
        try {
            LambdaUpdateWrapper<AiNews> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(AiNews::getId, id)
                   .eq(AiNews::getDeleted, 0)
                   .set(AiNews::getIsHot, true)
                   .set(AiNews::getUpdatedAt, LocalDateTime.now());
            
            return aiNewsMapper.update(null, wrapper) > 0;
        } catch (Exception e) {
            log.error("设置热门失败，ID: {}", id, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean unsetHot(Long id) {
        try {
            LambdaUpdateWrapper<AiNews> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(AiNews::getId, id)
                   .eq(AiNews::getDeleted, 0)
                   .set(AiNews::getIsHot, false)
                   .set(AiNews::getUpdatedAt, LocalDateTime.now());
            
            return aiNewsMapper.update(null, wrapper) > 0;
        } catch (Exception e) {
            log.error("取消热门失败，ID: {}", id, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean increaseViewCount(Long id) {
        try {
            LambdaUpdateWrapper<AiNews> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(AiNews::getId, id)
                   .setSql("view_count = view_count + 1");
            return aiNewsMapper.update(null, wrapper) > 0;
        } catch (Exception e) {
            log.error("增加浏览量失败，ID: {}", id, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean likeAiNews(Long id) {
        try {
            LambdaUpdateWrapper<AiNews> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(AiNews::getId, id)
                   .setSql("like_count = like_count + 1");
            return aiNewsMapper.update(null, wrapper) > 0;
        } catch (Exception e) {
            log.error("点赞失败，ID: {}", id, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean unlikeAiNews(Long id) {
        try {
            LambdaUpdateWrapper<AiNews> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(AiNews::getId, id)
                   .setSql("like_count = like_count - 1");
            return aiNewsMapper.update(null, wrapper) > 0;
        } catch (Exception e) {
            log.error("取消点赞失败，ID: {}", id, e);
            return false;
        }
    }

    @Override
    public Result<List<AiNews>> getHotAiNews(Integer limit) {
        try {
            if (limit == null || limit <= 0) {
                limit = 10;
            }
            
            LambdaQueryWrapper<AiNews> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AiNews::getDeleted, 0)
                   .eq(AiNews::getStatus, AiNews.NewsStatus.PUBLISHED)
                   .eq(AiNews::getIsHot, true)
                   .orderByDesc(AiNews::getPublishedAt)
                   .last("LIMIT " + limit);
            List<AiNews> hotNews = aiNewsMapper.selectList(wrapper);
            return Result.success(hotNews);
        } catch (Exception e) {
            log.error("获取热门AI新闻失败", e);
            return Result.error("获取失败");
        }
    }

    @Override
    public Result<List<AiNews>> getLatestAiNews(Integer limit) {
        try {
            if (limit == null || limit <= 0) {
                limit = 10;
            }
            
            LambdaQueryWrapper<AiNews> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AiNews::getDeleted, 0)
                   .eq(AiNews::getStatus, AiNews.NewsStatus.PUBLISHED)
                   .orderByDesc(AiNews::getPublishedAt)
                   .last("LIMIT " + limit);
            List<AiNews> latestNews = aiNewsMapper.selectList(wrapper);
            return Result.success(latestNews);
        } catch (Exception e) {
            log.error("获取最新AI新闻失败", e);
            return Result.error("获取失败");
        }
    }

    @Override
    public Result<Page<AiNews>> getAiNewsByCategory(String category, PageQuery query) {
        try {
            Page<AiNews> page = new Page<>(query.getPageNum(), query.getPageSize());
            LambdaQueryWrapper<AiNews> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AiNews::getDeleted, 0)
                   .eq(AiNews::getStatus, AiNews.NewsStatus.PUBLISHED)
                   .eq(AiNews::getCategory, category)
                   .orderByDesc(AiNews::getPublishedAt);
            Page<AiNews> result = aiNewsMapper.selectPage(page, wrapper);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据分类获取AI新闻失败，分类: {}", category, e);
            return Result.error("获取失败");
        }
    }

    @Override
    public Result<Page<AiNews>> getAiNewsBySource(String source, PageQuery query) {
        try {
            Page<AiNews> page = new Page<>(query.getPageNum(), query.getPageSize());
            LambdaQueryWrapper<AiNews> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AiNews::getDeleted, 0)
                   .eq(AiNews::getStatus, AiNews.NewsStatus.PUBLISHED)
                   .eq(AiNews::getSource, source)
                   .orderByDesc(AiNews::getPublishedAt);
            Page<AiNews> result = aiNewsMapper.selectPage(page, wrapper);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据来源获取AI新闻失败，来源: {}", source, e);
            return Result.error("获取失败");
        }
    }

    @Override
    public Result<Page<AiNews>> getAiNewsByTag(String tag, PageQuery query) {
        try {
            Page<AiNews> page = new Page<>(query.getPageNum(), query.getPageSize());
            LambdaQueryWrapper<AiNews> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AiNews::getDeleted, 0)
                   .eq(AiNews::getStatus, AiNews.NewsStatus.PUBLISHED)
                   .like(AiNews::getTags, tag)
                   .orderByDesc(AiNews::getPublishedAt);
            Page<AiNews> result = aiNewsMapper.selectPage(page, wrapper);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据标签获取AI新闻失败，标签: {}", tag, e);
            return Result.error("获取失败");
        }
    }

    @Override
    public Result<Page<AiNews>> searchAiNews(String keyword, PageQuery query) {
        try {
            Page<AiNews> page = new Page<>(query.getPageNum(), query.getPageSize());
            LambdaQueryWrapper<AiNews> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AiNews::getDeleted, 0)
                   .eq(AiNews::getStatus, AiNews.NewsStatus.PUBLISHED)
                   .and(w -> w.like(AiNews::getTitle, keyword)
                            .or().like(AiNews::getContent, keyword)
                            .or().like(AiNews::getSummary, keyword))
                   .orderByDesc(AiNews::getPublishedAt);
            Page<AiNews> result = aiNewsMapper.selectPage(page, wrapper);
            return Result.success(result);
        } catch (Exception e) {
            log.error("搜索AI新闻失败，关键词: {}", keyword, e);
            return Result.error("搜索失败");
        }
    }

    @Override
    public Result<List<String>> getAiNewsCategories() {
        try {
            LambdaQueryWrapper<AiNews> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AiNews::getDeleted, 0)
                   .eq(AiNews::getStatus, AiNews.NewsStatus.PUBLISHED)
                   .select(AiNews::getCategory)
                   .groupBy(AiNews::getCategory);
            List<AiNews> newsList = aiNewsMapper.selectList(wrapper);
            List<String> categories = newsList.stream()
                    .map(AiNews::getCategory)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取AI新闻分类列表失败", e);
            return Result.error("获取失败");
        }
    }

    @Override
    public Result<List<String>> getAiNewsSources() {
        try {
            LambdaQueryWrapper<AiNews> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AiNews::getDeleted, 0)
                   .eq(AiNews::getStatus, AiNews.NewsStatus.PUBLISHED)
                   .select(AiNews::getSource)
                   .groupBy(AiNews::getSource);
            List<AiNews> newsList = aiNewsMapper.selectList(wrapper);
            List<String> sources = newsList.stream()
                    .map(AiNews::getSource)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            return Result.success(sources);
        } catch (Exception e) {
            log.error("获取AI新闻来源列表失败", e);
            return Result.error("获取失败");
        }
    }

    @Override
    public Result<Map<String, Object>> getAiNewsStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 总数统计
            LambdaQueryWrapper<AiNews> totalWrapper = new LambdaQueryWrapper<>();
            totalWrapper.eq(AiNews::getDeleted, 0);
            Long totalCount = aiNewsMapper.selectCount(totalWrapper);
            stats.put("totalCount", totalCount);
            
            // 已发布统计
            LambdaQueryWrapper<AiNews> publishedWrapper = new LambdaQueryWrapper<>();
            publishedWrapper.eq(AiNews::getDeleted, 0)
                           .eq(AiNews::getStatus, AiNews.NewsStatus.PUBLISHED);
            Long publishedCount = aiNewsMapper.selectCount(publishedWrapper);
            stats.put("publishedCount", publishedCount);
            
            // 热门统计
            LambdaQueryWrapper<AiNews> hotWrapper = new LambdaQueryWrapper<>();
            hotWrapper.eq(AiNews::getDeleted, 0)
                     .eq(AiNews::getIsHot, true);
            Long hotCount = aiNewsMapper.selectCount(hotWrapper);
            stats.put("hotCount", hotCount);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取AI新闻统计信息失败", e);
            return Result.error("获取失败");
        }
    }

    @Override
    @Transactional
    public boolean syncAiNews() {
        try {
            log.info("开始同步AI新闻");
            
            int syncCount = 0;
            for (String rssUrl : rssSources) {
                try {
                    // 这里应该实现RSS解析逻辑
                    // 由于篇幅限制，这里只是示例
                    log.info("同步RSS源: {}", rssUrl);
                    
                    // TODO: 实现RSS解析和新闻保存逻辑
                    
                } catch (Exception e) {
                    log.error("同步RSS源失败: {}", rssUrl, e);
                }
            }
            
            log.info("AI新闻同步完成，共同步 {} 条新闻", syncCount);
            return true;
        } catch (Exception e) {
            log.error("同步AI新闻失败", e);
            return false;
        }
    }

    @Override
    public boolean existsByOriginalUrl(String originalUrl) {
        try {
            LambdaQueryWrapper<AiNews> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AiNews::getSourceUrl, originalUrl)
                   .eq(AiNews::getDeleted, 0);
            return aiNewsMapper.selectCount(wrapper) > 0;
        } catch (Exception e) {
            log.error("检查新闻是否存在失败，URL: {}", originalUrl, e);
            return false;
        }
    }

    /**
     * 自动生成摘要
     */
    private String generateSummary(String content) {
        if (!StringUtils.hasText(content)) {
            return "";
        }
        
        // 简单的摘要生成逻辑：取前200个字符
        String plainText = content.replaceAll("<[^>]+>", ""); // 去除HTML标签
        if (plainText.length() <= 200) {
            return plainText;
        }
        
        return plainText.substring(0, 200) + "...";
    }

    /**
     * 根据字段名获取对应的列
     */
    private com.baomidou.mybatisplus.core.toolkit.support.SFunction<AiNews, ?> getColumnByField(String field) {
        switch (field) {
            case "publishedAt":
                return AiNews::getPublishedAt;
            case "createdAt":
                return AiNews::getCreatedAt;
            case "updatedAt":
                return AiNews::getUpdatedAt;
            case "viewCount":
                return AiNews::getViewCount;
            case "likeCount":
                return AiNews::getLikeCount;
            default:
                return AiNews::getPublishedAt;
        }
    }
}