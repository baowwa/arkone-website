package com.arkone.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 标签实体类
 *
 * @author ArkOne
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tags")
@Schema(description = "标签实体")
public class Tag {

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "标签名称")
    @TableField("name")
    private String name;

    @Schema(description = "标签描述")
    @TableField("description")
    private String description;

    @Schema(description = "标签类型")
    @TableField("type")
    private TagType type;

    @Schema(description = "标签颜色")
    @TableField("color")
    private String color;

    @Schema(description = "使用次数")
    @TableField("usage_count")
    private Integer usageCount;

    @Schema(description = "排序")
    @TableField("sort_order")
    private Integer sortOrder;

    @Schema(description = "状态")
    @TableField("status")
    private TagStatus status;

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
     * 标签类型枚举
     */
    public enum TagType {
        ARTICLE("article", "文章标签"),
        NEWS("news", "新闻标签"),
        GENERAL("general", "通用标签");

        private final String code;
        private final String description;

        TagType(String code, String description) {
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
     * 标签状态枚举
     */
    public enum TagStatus {
        ACTIVE("active", "启用"),
        INACTIVE("inactive", "禁用");

        private final String code;
        private final String description;

        TagStatus(String code, String description) {
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