package com.arkone.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI新闻实体类
 *
 * @author ArkOne
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_news")
@Schema(description = "AI新闻实体")
public class AiNews {

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "新闻标题")
    @TableField("title")
    private String title;

    @Schema(description = "新闻内容")
    @TableField("content")
    private String content;

    @Schema(description = "新闻摘要")
    @TableField("summary")
    private String summary;

    @Schema(description = "新闻来源")
    @TableField("source")
    private String source;

    @Schema(description = "原文链接")
    @TableField("source_url")
    private String sourceUrl;

    @Schema(description = "新闻分类")
    @TableField("category")
    private String category;

    @Schema(description = "标签列表")
    @TableField("tags")
    private List<String> tags;

    @Schema(description = "封面图片")
    @TableField("cover_image")
    private String coverImage;

    @Schema(description = "是否热门")
    @TableField("is_hot")
    private Boolean isHot;

    @Schema(description = "浏览次数")
    @TableField("view_count")
    private Integer viewCount;

    @Schema(description = "点赞次数")
    @TableField("like_count")
    private Integer likeCount;

    @Schema(description = "状态")
    @TableField("status")
    private NewsStatus status;

    @Schema(description = "发布时间")
    @TableField("published_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedAt;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "逻辑删除标记")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    /**
     * 新闻状态枚举
     */
    public enum NewsStatus {
        DRAFT("draft", "草稿"),
        PUBLISHED("published", "已发布"),
        ARCHIVED("archived", "已归档");

        private final String code;
        private final String description;

        NewsStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}