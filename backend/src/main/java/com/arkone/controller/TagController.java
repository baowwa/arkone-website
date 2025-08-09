package com.arkone.controller;

import com.arkone.dto.PageQuery;
import com.arkone.dto.Result;
import com.arkone.entity.Tag;
import com.arkone.service.TagService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 标签控制器
 *
 * @author arkone
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "标签管理", description = "标签相关接口")
public class TagController {

    private final TagService tagService;

    /**
     * 分页查询标签
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询标签")
    public Result<Page<Tag>> getTagPage(@Valid @RequestBody PageQuery pageQuery) {
        return tagService.getTagPage(pageQuery);
    }

    /**
     * 根据ID获取标签详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取标签详情")
    public Result<Tag> getTagById(
            @Parameter(description = "标签ID") @PathVariable Long id) {
        return tagService.getTagById(id);
    }

    /**
     * 创建标签
     */
    @PostMapping
    @Operation(summary = "创建标签")
    public Result<Void> createTag(@Valid @RequestBody Tag tag) {
        boolean success = tagService.saveTag(tag);
        return success ? Result.success() : Result.error("创建失败");
    }

    /**
     * 更新标签
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新标签")
    public Result<Void> updateTag(
            @Parameter(description = "标签ID") @PathVariable Long id,
            @Valid @RequestBody Tag tag) {
        tag.setId(id);
        boolean success = tagService.updateTag(tag);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签")
    public Result<Void> deleteTag(
            @Parameter(description = "标签ID") @PathVariable Long id) {
        boolean success = tagService.deleteTag(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 批量删除标签
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除标签")
    public Result<Void> deleteTags(
            @Parameter(description = "标签ID列表") @RequestBody List<Long> ids) {
        boolean success = tagService.deleteTags(ids);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 获取所有启用的标签
     */
    @GetMapping("/enabled")
    @Operation(summary = "获取所有启用的标签")
    public Result<List<Tag>> getAllEnabledTags() {
        return tagService.getAllEnabledTags();
    }

    /**
     * 根据类型获取标签列表
     */
    @GetMapping("/type/{type}")
    @Operation(summary = "根据类型获取标签列表")
    public Result<List<Tag>> getTagsByType(
            @Parameter(description = "标签类型") @PathVariable String type) {
        Tag.TagType tagType = Tag.TagType.valueOf(type.toUpperCase());
        return tagService.getTagsByType(tagType);
    }

    /**
     * 搜索标签
     */
    @GetMapping("/search")
    @Operation(summary = "搜索标签")
    public Result<List<Tag>> searchTags(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        return tagService.searchTags(keyword);
    }

    /**
     * 检查标签名称是否存在
     */
    @GetMapping("/check-name")
    @Operation(summary = "检查标签名称是否存在")
    public Result<Boolean> checkTagNameExists(
            @Parameter(description = "标签名称") @RequestParam String name,
            @Parameter(description = "标签类型") @RequestParam String type,
            @Parameter(description = "排除的标签ID") @RequestParam(required = false) Long excludeId) {
        Tag.TagType tagType = Tag.TagType.valueOf(type.toUpperCase());
        boolean exists = tagService.existsByName(name, tagType, excludeId);
        return Result.success(exists);
    }

    /**
     * 获取标签下的内容数量
     */
    @GetMapping("/{id}/content-count")
    @Operation(summary = "获取标签下的内容数量")
    public Result<Long> getTagContentCount(
            @Parameter(description = "标签ID") @PathVariable Long id) {
        return tagService.getContentCountByTagId(id);
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门标签")
    public Result<List<Tag>> getHotTags(
            @Parameter(description = "标签类型") @RequestParam(required = false) String type,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        Tag.TagType tagType = type != null ? Tag.TagType.valueOf(type.toUpperCase()) : null;
        return tagService.getHotTags(tagType, limit);
    }

    /**
     * 获取标签统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取标签统计信息")
    public Result<Map<String, Object>> getTagStats() {
        return tagService.getTagStats();
    }

    /**
     * 根据名称获取或创建标签
     */
    @PostMapping("/get-or-create")
    @Operation(summary = "根据名称获取或创建标签")
    public Result<Tag> getOrCreateTagByName(
            @Parameter(description = "标签名称") @RequestParam String name,
            @Parameter(description = "标签类型") @RequestParam String type) {
        Tag.TagType tagType = Tag.TagType.valueOf(type.toUpperCase());
        return tagService.getOrCreateTagByName(name, tagType);
    }

    /**
     * 批量根据名称获取或创建标签
     */
    @PostMapping("/batch-get-or-create")
    @Operation(summary = "批量根据名称获取或创建标签")
    public Result<List<Tag>> getOrCreateTagsByNames(
            @Parameter(description = "标签名称列表") @RequestBody List<String> names,
            @Parameter(description = "标签类型") @RequestParam String type) {
        Tag.TagType tagType = Tag.TagType.valueOf(type.toUpperCase());
        return tagService.getOrCreateTagsByNames(names, tagType);
    }
}