package com.arkone.controller;

import com.arkone.dto.ArticleQuery;
import com.arkone.dto.ArticleSaveDTO;
import com.arkone.dto.Result;
import com.arkone.entity.Article;
import com.arkone.service.ArticleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 文章控制器
 *
 * @author ArkOne
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Validated
@Tag(name = "文章管理", description = "文章相关接口")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/page")
    @Operation(summary = "分页查询文章")
    public Result<IPage<Article>> getArticlePage(@Valid ArticleQuery query) {
        IPage<Article> page = articleService.getArticlePage(query);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文章详情")
    public Result<Article> getArticleDetail(
            @Parameter(description = "文章ID") @PathVariable @NotNull Long id) {
        Article article = articleService.getArticleDetail(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        return Result.success(article);
    }

    @PostMapping
    @Operation(summary = "创建文章")
    public Result<Void> createArticle(@Valid @RequestBody ArticleSaveDTO dto) {
        boolean success = articleService.saveArticle(dto);
        return success ? Result.success() : Result.error("文章创建失败");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新文章")
    public Result<Void> updateArticle(
            @Parameter(description = "文章ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody ArticleSaveDTO dto) {
        dto.setId(id);
        boolean success = articleService.updateArticle(dto);
        return success ? Result.success() : Result.error("文章更新失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文章")
    public Result<Void> deleteArticle(
            @Parameter(description = "文章ID") @PathVariable @NotNull Long id) {
        boolean success = articleService.deleteArticle(id);
        return success ? Result.success() : Result.error("文章删除失败");
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除文章")
    public Result<Void> deleteArticles(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的文章");
        }
        boolean success = articleService.deleteArticles(ids);
        return success ? Result.success() : Result.error("文章批量删除失败");
    }

    @PutMapping("/{id}/publish")
    @Operation(summary = "发布文章")
    public Result<Void> publishArticle(
            @Parameter(description = "文章ID") @PathVariable @NotNull Long id) {
        boolean success = articleService.publishArticle(id);
        return success ? Result.success() : Result.error("文章发布失败");
    }

    @PutMapping("/{id}/unpublish")
    @Operation(summary = "取消发布文章")
    public Result<Void> unpublishArticle(
            @Parameter(description = "文章ID") @PathVariable @NotNull Long id) {
        boolean success = articleService.unpublishArticle(id);
        return success ? Result.success() : Result.error("文章取消发布失败");
    }

    @PutMapping("/{id}/top")
    @Operation(summary = "置顶文章")
    public Result<Void> topArticle(
            @Parameter(description = "文章ID") @PathVariable @NotNull Long id) {
        boolean success = articleService.topArticle(id);
        return success ? Result.success() : Result.error("文章置顶失败");
    }

    @PutMapping("/{id}/untop")
    @Operation(summary = "取消置顶文章")
    public Result<Void> untopArticle(
            @Parameter(description = "文章ID") @PathVariable @NotNull Long id) {
        boolean success = articleService.untopArticle(id);
        return success ? Result.success() : Result.error("文章取消置顶失败");
    }

    @PutMapping("/{id}/like")
    @Operation(summary = "点赞文章")
    public Result<Void> likeArticle(
            @Parameter(description = "文章ID") @PathVariable @NotNull Long id) {
        boolean success = articleService.likeArticle(id);
        return success ? Result.success() : Result.error("点赞失败");
    }

    @PutMapping("/{id}/unlike")
    @Operation(summary = "取消点赞文章")
    public Result<Void> unlikeArticle(
            @Parameter(description = "文章ID") @PathVariable @NotNull Long id) {
        boolean success = articleService.unlikeArticle(id);
        return success ? Result.success() : Result.error("取消点赞失败");
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门文章")
    public Result<List<Article>> getHotArticles(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<Article> articles = articleService.getHotArticles(limit);
        return Result.success(articles);
    }

    @GetMapping("/latest")
    @Operation(summary = "获取最新文章")
    public Result<List<Article>> getLatestArticles(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<Article> articles = articleService.getLatestArticles(limit);
        return Result.success(articles);
    }

    @GetMapping("/recommend")
    @Operation(summary = "获取推荐文章")
    public Result<List<Article>> getRecommendArticles(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "5") Integer limit) {
        List<Article> articles = articleService.getRecommendArticles(limit);
        return Result.success(articles);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根据分类获取文章")
    public Result<List<Article>> getArticlesByCategory(
            @Parameter(description = "分类ID") @PathVariable @NotNull Long categoryId,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<Article> articles = articleService.getArticlesByCategory(categoryId, limit);
        return Result.success(articles);
    }

    @GetMapping("/tag/{tag}")
    @Operation(summary = "根据标签获取文章")
    public Result<List<Article>> getArticlesByTag(
            @Parameter(description = "标签") @PathVariable String tag,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<Article> articles = articleService.getArticlesByTag(tag, limit);
        return Result.success(articles);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索文章")
    public Result<IPage<Article>> searchArticles(
            @Parameter(description = "关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Article> page = articleService.searchArticles(keyword, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取文章统计信息")
    public Result<Object> getArticleStats() {
        Object stats = articleService.getArticleStats();
        return Result.success(stats);
    }

    @PostMapping("/sync/wechat")
    @Operation(summary = "同步微信公众号文章")
    public Result<Void> syncWechatArticles() {
        boolean success = articleService.syncWechatArticles();
        return success ? Result.success() : Result.error("同步失败");
    }
}