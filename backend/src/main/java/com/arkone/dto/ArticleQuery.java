package com.arkone.dto;

import com.arkone.entity.Article;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章查询DTO
 *
 * @author ArkOne
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文章查询参数")
public class ArticleQuery extends PageQuery {

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "文章状态")
    private Article.ArticleStatus status;

    @Schema(description = "来源类型")
    private Article.SourceType sourceType;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "是否置顶")
    private Boolean isTop;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}