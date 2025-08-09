package com.arkone.service;

import com.arkone.dto.PageQuery;
import com.arkone.dto.Result;
import com.arkone.entity.Tag;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

/**
 * 标签服务接口
 *
 * @author arkone
 * @since 2024-01-01
 */
public interface TagService {

    /**
     * 分页查询标签
     */
    Result<Page<Tag>> getTagPage(PageQuery pageQuery);

    /**
     * 根据ID获取标签详情
     */
    Result<Tag> getTagById(Long id);

    /**
     * 保存标签
     */
    boolean saveTag(Tag tag);

    /**
     * 更新标签
     */
    boolean updateTag(Tag tag);

    /**
     * 删除标签
     */
    boolean deleteTag(Long id);

    /**
     * 批量删除标签
     */
    boolean deleteTags(List<Long> ids);

    /**
     * 获取所有启用的标签
     */
    Result<List<Tag>> getAllEnabledTags();

    /**
     * 根据类型获取标签列表
     */
    Result<List<Tag>> getTagsByType(Tag.TagType type);

    /**
     * 搜索标签
     */
    Result<List<Tag>> searchTags(String keyword);

    /**
     * 检查标签名称是否存在
     */
    boolean existsByName(String name, Tag.TagType type, Long excludeId);

    /**
     * 获取标签下的内容数量
     */
    Result<Long> getContentCountByTagId(Long tagId);

    /**
     * 获取热门标签
     */
    Result<List<Tag>> getHotTags(Tag.TagType type, Integer limit);

    /**
     * 获取标签统计信息
     */
    Result<Map<String, Object>> getTagStats();

    /**
     * 根据名称获取或创建标签
     */
    Result<Tag> getOrCreateTagByName(String name, Tag.TagType type);

    /**
     * 批量根据名称获取或创建标签
     */
    Result<List<Tag>> getOrCreateTagsByNames(List<String> names, Tag.TagType type);
}