package com.arkone.service;

import com.arkone.dto.ArticleQuery;
import com.arkone.dto.ArticleSaveDTO;
import com.arkone.entity.Article;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 文章服务接口
 *
 * @author ArkOne
 * @since 2024-01-01
 */
public interface ArticleService extends IService<Article> {

    /**
     * 分页查询文章
     */
    IPage<Article> getArticlePage(ArticleQuery query);

    /**
     * 根据ID获取文章详情
     */
    Article getArticleDetail(Long id);

    /**
     * 保存文章
     */
    boolean saveArticle(ArticleSaveDTO dto);

    /**
     * 更新文章
     */
    boolean updateArticle(ArticleSaveDTO dto);

    /**
     * 删除文章
     */
    boolean deleteArticle(Long id);

    /**
     * 批量删除文章
     */
    boolean deleteArticles(List<Long> ids);

    /**
     * 发布文章
     */
    boolean publishArticle(Long id);

    /**
     * 取消发布文章
     */
    boolean unpublishArticle(Long id);

    /**
     * 置顶文章
     */
    boolean topArticle(Long id);

    /**
     * 取消置顶文章
     */
    boolean untopArticle(Long id);

    /**
     * 增加浏览量
     */
    boolean incrementViewCount(Long id);

    /**
     * 点赞文章
     */
    boolean likeArticle(Long id);

    /**
     * 取消点赞文章
     */
    boolean unlikeArticle(Long id);

    /**
     * 获取热门文章
     */
    List<Article> getHotArticles(Integer limit);

    /**
     * 获取最新文章
     */
    List<Article> getLatestArticles(Integer limit);

    /**
     * 获取推荐文章
     */
    List<Article> getRecommendArticles(Integer limit);

    /**
     * 根据分类获取文章
     */
    List<Article> getArticlesByCategory(Long categoryId, Integer limit);

    /**
     * 根据标签获取文章
     */
    List<Article> getArticlesByTag(String tag, Integer limit);

    /**
     * 搜索文章
     */
    IPage<Article> searchArticles(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 获取文章统计信息
     */
    Object getArticleStats();

    /**
     * 同步微信公众号文章
     */
    boolean syncWechatArticles();
}