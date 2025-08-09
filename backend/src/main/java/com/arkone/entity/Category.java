package com.arkone.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 分类实体类
 *
 * @author ArkOne
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("categories")
@Schema(description = "分类实体")
public class Category {

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "分类名称")
    @TableField("name")
    private String name;

    @Schema(description = "分类描述")
    @TableField("description")
    private String description;

    @Schema(description = "分类类型")
    @TableField("type")
    private CategoryType type;

    @Schema(description = "父分类ID")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "排序")
    @TableField("sort_order")
    private Integer sortOrder;

    @Schema(description = "状态")
    @TableField("status")
    private CategoryStatus status;

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
     * 分类类型枚举
     */
    public enum CategoryType {
        ARTICLE("article", "文章分类"),
        NEWS("news", "新闻分类");

        private final String code;
        private final String description;

        CategoryType(String code, String description) {
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
     * 分类状态枚举
     */
    public enum CategoryStatus {
        ACTIVE("active", "启用"),
        INACTIVE("inactive", "禁用");

        private final String code;
        private final String description;

        CategoryStatus(String code, String description) {
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