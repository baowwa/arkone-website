package com.arkone.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章实体类
 *
 * @author ArkOne
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("articles")
@Schema(description = "文章实体")
public class Article {

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "文章标题")
    @TableField("title")
    private String title;

    @Schema(description = "文章内容")
    @TableField("content")
    private String content;

    @Schema(description = "文章摘要")
    @TableField("summary")
    private String summary;

    @Schema(description = "封面图片URL")
    @TableField("cover_image")
    private String coverImage;

    @Schema(description = "来源类型")
    @TableField("source_type")
    private SourceType sourceType;

    @Schema(description = "原文链接")
    @TableField("source_url")
    private String sourceUrl;

    @Schema(description = "标签列表")
    @TableField("tags")
    private List<String> tags;

    @Schema(description = "分类ID")
    @TableField("category_id")
    private Long categoryId;

    @Schema(description = "浏览次数")
    @TableField("view_count")
    private Integer viewCount;

    @Schema(description = "点赞次数")
    @TableField("like_count")
    private Integer likeCount;

    @Schema(description = "状态")
    @TableField("status")
    private ArticleStatus status;

    @Schema(description = "是否置顶")
    @TableField("is_top")
    private Boolean isTop;

    @Schema(description = "发布时间")
    @TableField("publish_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

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

    // 非数据库字段
    @Schema(description = "分类名称")
    @TableField(exist = false)
    private String categoryName;

    /**
     * 来源类型枚举
     */
    public enum SourceType {
        WECHAT("wechat", "微信公众号"),
        MANUAL("manual", "手动创建");

        private final String code;
        private final String description;

        SourceType(String code, String description) {
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

    /**
     * 文章状态枚举
     */
    public enum ArticleStatus {
        DRAFT("draft", "草稿"),
        PUBLISHED("published", "已发布"),
        ARCHIVED("archived", "已归档");

        private final String code;
        private final String description;

        ArticleStatus(String code, String description) {
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