package com.arkone.service.impl;

import com.arkone.dto.PageQuery;
import com.arkone.dto.Result;
import com.arkone.entity.Tag;
import com.arkone.mapper.TagMapper;
import com.arkone.service.TagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签服务实现类
 *
 * @author arkone
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    private final TagMapper tagMapper;

    @Override
    public Result<Page<Tag>> getTagPage(PageQuery pageQuery) {
        try {
            Page<Tag> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
            LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Tag::getDeleted, 0)
                   .orderByDesc(Tag::getUsageCount)
                   .orderByAsc(Tag::getSortOrder)
                   .orderByDesc(Tag::getCreatedAt);
            
            Page<Tag> result = tagMapper.selectPage(page, wrapper);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询标签失败", e);
            return Result.error("查询失败");
        }
    }

    @Override
    public Result<Tag> getTagById(Long id) {
        try {
            if (id == null) {
                return Result.error("标签ID不能为空");
            }
            
            Tag tag = tagMapper.selectById(id);
            if (tag == null || tag.getDeleted() == 1) {
                return Result.error("标签不存在");
            }
            
            return Result.success(tag);
        } catch (Exception e) {
            log.error("根据ID获取标签失败，ID: {}", id, e);
            return Result.error("获取失败");
        }
    }

    @Override
    @Transactional
    public boolean saveTag(Tag tag) {
        try {
            if (tag == null || !StringUtils.hasText(tag.getName())) {
                return false;
            }
            
            // 检查名称是否重复
            if (existsByName(tag.getName(), tag.getType(), null)) {
                log.warn("标签名称已存在: {}", tag.getName());
                return false;
            }
            
            // 设置默认值
            if (tag.getStatus() == null) {
                tag.setStatus(Tag.TagStatus.ACTIVE);
            }
            
            if (tag.getSortOrder() == null) {
                tag.setSortOrder(0);
            }
            
            if (tag.getUsageCount() == null) {
                tag.setUsageCount(0);
            }
            
            tag.setCreatedAt(LocalDateTime.now());
            tag.setUpdatedAt(LocalDateTime.now());
            
            return tagMapper.insert(tag) > 0;
        } catch (Exception e) {
            log.error("保存标签失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateTag(Tag tag) {
        try {
            Tag existing = tagMapper.selectById(tag.getId());
            if (existing == null || existing.getDeleted() == 1) {
                return false;
            }
            
            // 检查名称是否重复（排除自己）
            if (existsByName(tag.getName(), tag.getType(), tag.getId())) {
                log.warn("标签名称已存在: {}", tag.getName());
                return false;
            }
            
            tag.setUpdatedAt(LocalDateTime.now());
            return tagMapper.updateById(tag) > 0;
        } catch (Exception e) {
            log.error("更新标签失败，ID: {}", tag.getId(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteTag(Long id) {
        try {
            // 检查标签下是否有内容
            Long contentCount = getContentCountByTagId(id).getData();
            if (contentCount != null && contentCount > 0) {
                log.warn("标签下存在内容，无法删除，ID: {}", id);
                return false;
            }
            
            LambdaUpdateWrapper<Tag> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Tag::getId, id)
                   .set(Tag::getDeleted, 1)
                   .set(Tag::getUpdatedAt, LocalDateTime.now());
            
            return tagMapper.update(null, wrapper) > 0;
        } catch (Exception e) {
            log.error("删除标签失败，ID: {}", id, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteTags(List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return false;
            }
            
            // 检查每个标签下是否有内容
            for (Long id : ids) {
                Long contentCount = getContentCountByTagId(id).getData();
                if (contentCount != null && contentCount > 0) {
                    log.warn("标签下存在内容，无法删除，ID: {}", id);
                    return false;
                }
            }
            
            LambdaUpdateWrapper<Tag> wrapper = new LambdaUpdateWrapper<>();
            wrapper.in(Tag::getId, ids)
                   .set(Tag::getDeleted, 1)
                   .set(Tag::getUpdatedAt, LocalDateTime.now());
            
            return tagMapper.update(null, wrapper) > 0;
        } catch (Exception e) {
            log.error("批量删除标签失败，IDs: {}", ids, e);
            return false;
        }
    }

    @Override
    public Result<List<Tag>> getAllEnabledTags() {
        try {
            LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Tag::getDeleted, 0)
                   .eq(Tag::getStatus, Tag.TagStatus.ACTIVE)
                   .orderByDesc(Tag::getUsageCount)
                   .orderByAsc(Tag::getSortOrder)
                   .orderByDesc(Tag::getCreatedAt);
            
            List<Tag> tags = tagMapper.selectList(wrapper);
            return Result.success(tags);
        } catch (Exception e) {
            log.error("获取所有启用标签失败", e);
            return Result.error("获取失败");
        }
    }

    @Override
    public Result<List<Tag>> getTagsByType(Tag.TagType type) {
        try {
            if (type == null) {
                return Result.error("标签类型不能为空");
            }
            
            LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Tag::getDeleted, 0)
                   .eq(Tag::getStatus, Tag.TagStatus.ACTIVE)
                   .eq(Tag::getType, type)
                   .orderByDesc(Tag::getUsageCount)
                   .orderByAsc(Tag::getSortOrder)
                   .orderByDesc(Tag::getCreatedAt);
            
            List<Tag> tags = tagMapper.selectList(wrapper);
            return Result.success(tags);
        } catch (Exception e) {
            log.error("根据类型获取标签失败，类型: {}", type, e);
            return Result.error("获取失败");
        }
    }

    @Override
    public Result<List<Tag>> searchTags(String keyword) {
        try {
            if (!StringUtils.hasText(keyword)) {
                return Result.success(new ArrayList<>());
            }
            
            LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Tag::getDeleted, 0)
                   .eq(Tag::getStatus, Tag.TagStatus.ACTIVE)
                   .and(w -> w.like(Tag::getName, keyword)
                            .or()
                            .like(Tag::getDescription, keyword))
                   .orderByDesc(Tag::getUsageCount)
                   .orderByAsc(Tag::getSortOrder)
                   .orderByDesc(Tag::getCreatedAt);
            
            List<Tag> tags = tagMapper.selectList(wrapper);
            return Result.success(tags);
        } catch (Exception e) {
            log.error("搜索标签失败，关键词: {}", keyword, e);
            return Result.error("搜索失败");
        }
    }

    @Override
    public boolean existsByName(String name, Tag.TagType type, Long excludeId) {
        try {
            if (!StringUtils.hasText(name)) {
                return false;
            }
            
            LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Tag::getName, name)
                   .eq(Tag::getType, type)
                   .eq(Tag::getDeleted, 0);
            
            if (excludeId != null) {
                wrapper.ne(Tag::getId, excludeId);
            }
            
            return tagMapper.selectCount(wrapper) > 0;
        } catch (Exception e) {
            log.error("检查标签名称是否存在失败，名称: {}", name, e);
            return false;
        }
    }

    @Override
    public Result<Long> getContentCountByTagId(Long tagId) {
        try {
            if (tagId == null) {
                return Result.error("标签ID不能为空");
            }
            
            // 这里需要根据实际业务逻辑统计标签下的内容数量
            // 可能需要查询文章表、新闻表等
            // 暂时返回0
            return Result.success(0L);
        } catch (Exception e) {
            log.error("获取标签内容数量失败，标签ID: {}", tagId, e);
            return Result.error("获取失败");
        }
    }

    @Override
    public Result<List<Tag>> getHotTags(Tag.TagType type, Integer limit) {
        try {
            LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Tag::getDeleted, 0)
                   .eq(Tag::getStatus, Tag.TagStatus.ACTIVE)
                   .gt(Tag::getUsageCount, 0)
                   .orderByDesc(Tag::getUsageCount)
                   .orderByDesc(Tag::getCreatedAt);
            
            if (type != null) {
                wrapper.eq(Tag::getType, type);
            }
            
            if (limit != null && limit > 0) {
                wrapper.last("LIMIT " + limit);
            }
            
            List<Tag> tags = tagMapper.selectList(wrapper);
            return Result.success(tags);
        } catch (Exception e) {
            log.error("获取热门标签失败，类型: {}, 限制: {}", type, limit, e);
            return Result.error("获取失败");
        }
    }

    @Override
    public Result<Map<String, Object>> getTagStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 总标签数
            LambdaQueryWrapper<Tag> totalWrapper = new LambdaQueryWrapper<>();
            totalWrapper.eq(Tag::getDeleted, 0);
            Long totalCount = tagMapper.selectCount(totalWrapper);
            stats.put("totalCount", totalCount);
            
            // 启用标签数
            LambdaQueryWrapper<Tag> activeWrapper = new LambdaQueryWrapper<>();
            activeWrapper.eq(Tag::getDeleted, 0)
                        .eq(Tag::getStatus, Tag.TagStatus.ACTIVE);
            Long activeCount = tagMapper.selectCount(activeWrapper);
            stats.put("activeCount", activeCount);
            
            // 各类型标签数
            for (Tag.TagType type : Tag.TagType.values()) {
                LambdaQueryWrapper<Tag> typeWrapper = new LambdaQueryWrapper<>();
                typeWrapper.eq(Tag::getDeleted, 0)
                          .eq(Tag::getType, type);
                Long typeCount = tagMapper.selectCount(typeWrapper);
                stats.put(type.getCode() + "Count", typeCount);
            }
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取标签统计信息失败", e);
            return Result.error("获取失败");
        }
    }

    @Override
    @Transactional
    public Result<Tag> getOrCreateTagByName(String name, Tag.TagType type) {
        try {
            if (!StringUtils.hasText(name) || type == null) {
                return Result.error("标签名称和类型不能为空");
            }
            
            // 先查询是否存在
            LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Tag::getName, name)
                   .eq(Tag::getType, type)
                   .eq(Tag::getDeleted, 0);
            
            Tag existingTag = tagMapper.selectOne(wrapper);
            if (existingTag != null) {
                return Result.success(existingTag);
            }
            
            // 不存在则创建
            Tag newTag = new Tag();
            newTag.setName(name);
            newTag.setType(type);
            newTag.setStatus(Tag.TagStatus.ACTIVE);
            newTag.setSortOrder(0);
            newTag.setUsageCount(0);
            newTag.setCreatedAt(LocalDateTime.now());
            newTag.setUpdatedAt(LocalDateTime.now());
            
            if (tagMapper.insert(newTag) > 0) {
                return Result.success(newTag);
            } else {
                return Result.error("创建标签失败");
            }
        } catch (Exception e) {
            log.error("根据名称获取或创建标签失败，名称: {}, 类型: {}", name, type, e);
            return Result.error("操作失败");
        }
    }

    @Override
    @Transactional
    public Result<List<Tag>> getOrCreateTagsByNames(List<String> names, Tag.TagType type) {
        try {
            if (names == null || names.isEmpty() || type == null) {
                return Result.error("标签名称列表和类型不能为空");
            }
            
            List<Tag> result = new ArrayList<>();
            
            for (String name : names) {
                if (StringUtils.hasText(name)) {
                    Result<Tag> tagResult = getOrCreateTagByName(name.trim(), type);
                    if (tagResult.isSuccess() && tagResult.getData() != null) {
                        result.add(tagResult.getData());
                    }
                }
            }
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量根据名称获取或创建标签失败，名称: {}, 类型: {}", names, type, e);
            return Result.error("操作失败");
        }
    }
}