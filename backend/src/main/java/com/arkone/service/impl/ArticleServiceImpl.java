package com.arkone.service.impl;

import com.arkone.dto.ArticleQuery;
import com.arkone.dto.ArticleSaveDTO;
import com.arkone.entity.Article;
import com.arkone.mapper.ArticleMapper;
import com.arkone.service.ArticleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章服务实现类
 *
 * @author ArkOne
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final ArticleMapper articleMapper;

    @Override
    public IPage<Article> getArticlePage(ArticleQuery query) {
        Page<Article> page = new Page<>(query.getPageNum(), query.getPageSize());
        return articleMapper.selectArticlePageWithCategory(page, query);
    }

    @Override
    public Article getArticleDetail(Long id) {
        Article article = articleMapper.selectArticleWithCategoryById(id);
        if (article != null) {
            // 异步增加浏览量
            incrementViewCount(id);
        }
        return article;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveArticle(ArticleSaveDTO dto) {
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        
        // 如果是发布状态且没有设置发布时间，则设置为当前时间
        if (Article.ArticleStatus.PUBLISHED.equals(dto.getStatus()) && dto.getPublishTime() == null) {
            article.setPublishTime(LocalDateTime.now());
        }
        
        // 自动生成摘要（如果没有提供）
        if (!StringUtils.hasText(dto.getSummary()) && StringUtils.hasText(dto.getContent())) {
            article.setSummary(generateSummary(dto.getContent()));
        }
        
        return save(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticle(ArticleSaveDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("文章ID不能为空");
        }
        
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        
        // 如果是发布状态且没有设置发布时间，则设置为当前时间
        if (Article.ArticleStatus.PUBLISHED.equals(dto.getStatus()) && dto.getPublishTime() == null) {
            Article existingArticle = getById(dto.getId());
            if (existingArticle != null && !Article.ArticleStatus.PUBLISHED.equals(existingArticle.getStatus())) {
                article.setPublishTime(LocalDateTime.now());
            }
        }
        
        // 自动生成摘要（如果没有提供）
        if (!StringUtils.hasText(dto.getSummary()) && StringUtils.hasText(dto.getContent())) {
            article.setSummary(generateSummary(dto.getContent()));
        }
        
        return updateById(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteArticle(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteArticles(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishArticle(Long id) {
        Article article = new Article();
        article.setId(id);
        article.setStatus(Article.ArticleStatus.PUBLISHED);
        article.setPublishTime(LocalDateTime.now());
        return updateById(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unpublishArticle(Long id) {
        Article article = new Article();
        article.setId(id);
        article.setStatus(Article.ArticleStatus.DRAFT);
        return updateById(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean topArticle(Long id) {
        Article article = new Article();
        article.setId(id);
        article.setIsTop(true);
        return updateById(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean untopArticle(Long id) {
        Article article = new Article();
        article.setId(id);
        article.setIsTop(false);
        return updateById(article);
    }

    @Override
    public boolean incrementViewCount(Long id) {
        return articleMapper.incrementViewCount(id) > 0;
    }

    @Override
    public boolean likeArticle(Long id) {
        return articleMapper.incrementLikeCount(id) > 0;
    }

    @Override
    public boolean unlikeArticle(Long id) {
        return articleMapper.decrementLikeCount(id) > 0;
    }

    @Override
    public List<Article> getHotArticles(Integer limit) {
        return articleMapper.selectHotArticles(limit != null ? limit : 10);
    }

    @Override
    public List<Article> getLatestArticles(Integer limit) {
        return articleMapper.selectLatestArticles(limit != null ? limit : 10);
    }

    @Override
    public List<Article> getRecommendArticles(Integer limit) {
        return articleMapper.selectRecommendArticles(limit != null ? limit : 5);
    }

    @Override
    public List<Article> getArticlesByCategory(Long categoryId, Integer limit) {
        return articleMapper.selectArticlesByCategoryId(categoryId, limit != null ? limit : 10);
    }

    @Override
    public List<Article> getArticlesByTag(String tag, Integer limit) {
        return articleMapper.selectArticlesByTag(tag, limit != null ? limit : 10);
    }

    @Override
    public IPage<Article> searchArticles(String keyword, Integer pageNum, Integer pageSize) {
        Page<Article> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        return articleMapper.searchArticles(page, keyword);
    }

    @Override
    public Object getArticleStats() {
        return articleMapper.getArticleStats();
    }

    @Override
    public boolean syncWechatArticles() {
        // TODO: 实现微信公众号文章同步逻辑
        log.info("开始同步微信公众号文章");
        try {
            // 这里应该调用微信公众号API获取文章列表
            // 然后解析文章内容并保存到数据库
            log.info("微信公众号文章同步完成");
            return true;
        } catch (Exception e) {
            log.error("微信公众号文章同步失败", e);
            return false;
        }
    }

    /**
     * 生成文章摘要
     */
    private String generateSummary(String content) {
        if (!StringUtils.hasText(content)) {
            return "";
        }
        
        // 移除Markdown标记和HTML标签
        String plainText = content.replaceAll("[#*`>\\[\\]()!-]", "")
                                 .replaceAll("<[^>]+>", "")
                                 .replaceAll("\\s+", " ")
                                 .trim();
        
        // 截取前200个字符作为摘要
        if (plainText.length() > 200) {
            return plainText.substring(0, 200) + "...";
        }
        
        return plainText;
    }
}