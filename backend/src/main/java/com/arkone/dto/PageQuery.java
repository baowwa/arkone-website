package com.arkone.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.io.Serializable;

/**
 * 分页查询基础类
 *
 * @author ArkOne
 * @since 2024-01-01
 */
@Data
@Schema(description = "分页查询参数")
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 10;

    @Schema(description = "排序字段")
    private String sortField;

    @Schema(description = "排序方向", example = "desc")
    private String sortOrder = "desc";

    @Schema(description = "关键词搜索")
    private String keyword;

    /**
     * 获取偏移量
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }

    /**
     * 是否升序
     */
    public boolean isAsc() {
        return "asc".equalsIgnoreCase(sortOrder);
    }
}