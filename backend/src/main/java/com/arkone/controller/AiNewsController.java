package com.arkone.controller;

import com.arkone.entity.AiNews;
import com.arkone.service.AiNewsService;
import com.arkone.dto.PageQuery;
import com.arkone.dto.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * AI新闻控制器
 */
@Tag(name = "AI新闻管理", description = "AI新闻相关接口")
@RestController
@RequestMapping("/api/ai-news")
@RequiredArgsConstructor
@Validated
public class AiNewsController {

    private final AiNewsService aiNewsService;

    @Operation(summary = "分页查询AI新闻")
    @GetMapping
    public Result<Page<AiNews>> getAiNewsPage(@Valid PageQuery query) {
        return aiNewsService.getAiNewsPage(query);
    }

    @Operation(summary = "获取AI新闻详情")
    @GetMapping("/{id}")
    public Result<AiNews> getAiNewsById(
            @Parameter(description = "新闻ID") @PathVariable @NotNull Long id) {
        return aiNewsService.getAiNewsById(id);
    }

    @Operation(summary = "创建AI新闻")
    @PostMapping
    public Result<Void> createAiNews(@Valid @RequestBody AiNews aiNews) {
        boolean success = aiNewsService.saveAiNews(aiNews);
        return success ? Result.success() : Result.error("AI新闻创建失败");
    }

    @Operation(summary = "更新AI新闻")
    @PutMapping("/{id}")
    public Result<Void> updateAiNews(
            @Parameter(description = "新闻ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody AiNews aiNews) {
        aiNews.setId(id);
        boolean success = aiNewsService.updateAiNews(aiNews);
        return success ? Result.success() : Result.error("AI新闻更新失败");
    }

    @Operation(summary = "删除AI新闻")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAiNews(
            @Parameter(description = "新闻ID") @PathVariable @NotNull Long id) {
        boolean success = aiNewsService.deleteAiNews(id);
        return success ? Result.success() : Result.error("AI新闻删除失败");
    }

    @Operation(summary = "批量删除AI新闻")
    @DeleteMapping("/batch")
    public Result<Void> deleteAiNews(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的新闻");
        }
        boolean success = aiNewsService.deleteAiNews(ids);
        return success ? Result.success() : Result.error("AI新闻批量删除失败");
    }

    @Operation(summary = "发布AI新闻")
    @PostMapping("/{id}/publish")
    public Result<Void> publishAiNews(
            @Parameter(description = "新闻ID") @PathVariable @NotNull Long id) {
        boolean success = aiNewsService.publishAiNews(id);
        return success ? Result.success() : Result.error("AI新闻发布失败");
    }

    @Operation(summary = "取消发布AI新闻")
    @PostMapping("/{id}/unpublish")
    public Result<Void> unpublishAiNews(
            @Parameter(description = "新闻ID") @PathVariable @NotNull Long id) {
        boolean success = aiNewsService.unpublishAiNews(id);
        return success ? Result.success() : Result.error("AI新闻取消发布失败");
    }

    @Operation(summary = "设置热门")
    @PostMapping("/{id}/hot")
    public Result<Void> setHot(
            @Parameter(description = "新闻ID") @PathVariable @NotNull Long id) {
        boolean success = aiNewsService.setHot(id);
        return success ? Result.success() : Result.error("设置热门失败");
    }

    @Operation(summary = "取消热门")
    @DeleteMapping("/{id}/hot")
    public Result<Void> unsetHot(
            @Parameter(description = "新闻ID") @PathVariable @NotNull Long id) {
        boolean success = aiNewsService.unsetHot(id);
        return success ? Result.success() : Result.error("取消热门失败");
    }

    @Operation(summary = "点赞AI新闻")
    @PostMapping("/{id}/like")
    public Result<Void> likeAiNews(
            @Parameter(description = "新闻ID") @PathVariable @NotNull Long id) {
        boolean success = aiNewsService.likeAiNews(id);
        return success ? Result.success() : Result.error("点赞失败");
    }

    @Operation(summary = "取消点赞AI新闻")
    @DeleteMapping("/{id}/like")
    public Result<Void> unlikeAiNews(
            @Parameter(description = "新闻ID") @PathVariable @NotNull Long id) {
        boolean success = aiNewsService.unlikeAiNews(id);
        return success ? Result.success() : Result.error("取消点赞失败");
    }

    @Operation(summary = "获取热门AI新闻")
    @GetMapping("/hot")
    public Result<List<AiNews>> getHotAiNews(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        return aiNewsService.getHotAiNews(limit);
    }

    @Operation(summary = "获取最新AI新闻")
    @GetMapping("/latest")
    public Result<List<AiNews>> getLatestAiNews(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        return aiNewsService.getLatestAiNews(limit);
    }

    @Operation(summary = "根据分类获取AI新闻")
    @GetMapping("/category/{category}")
    public Result<Page<AiNews>> getAiNewsByCategory(
            @Parameter(description = "分类") @PathVariable String category,
            @Valid PageQuery query) {
        return aiNewsService.getAiNewsByCategory(category, query);
    }

    @Operation(summary = "根据来源获取AI新闻")
    @GetMapping("/source/{source}")
    public Result<Page<AiNews>> getAiNewsBySource(
            @Parameter(description = "来源") @PathVariable String source,
            @Valid PageQuery query) {
        return aiNewsService.getAiNewsBySource(source, query);
    }

    @Operation(summary = "根据标签获取AI新闻")
    @GetMapping("/tag/{tag}")
    public Result<Page<AiNews>> getAiNewsByTag(
            @Parameter(description = "标签") @PathVariable String tag,
            @Valid PageQuery query) {
        return aiNewsService.getAiNewsByTag(tag, query);
    }

    @Operation(summary = "搜索AI新闻")
    @GetMapping("/search")
    public Result<Page<AiNews>> searchAiNews(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Valid PageQuery query) {
        return aiNewsService.searchAiNews(keyword, query);
    }

    @Operation(summary = "获取AI新闻分类列表")
    @GetMapping("/categories")
    public Result<List<String>> getAiNewsCategories() {
        return aiNewsService.getAiNewsCategories();
    }

    @Operation(summary = "获取AI新闻来源列表")
    @GetMapping("/sources")
    public Result<List<String>> getAiNewsSources() {
        return aiNewsService.getAiNewsSources();
    }

    @Operation(summary = "获取AI新闻统计信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getAiNewsStats() {
        return aiNewsService.getAiNewsStats();
    }

    @Operation(summary = "同步AI新闻")
    @PostMapping("/sync")
    public Result<Void> syncAiNews() {
        boolean success = aiNewsService.syncAiNews();
        return success ? Result.success() : Result.error("同步失败");
    }
}