package com.arkone.dto;

import com.arkone.entity.Article;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章保存DTO
 *
 * @author ArkOne
 * @since 2024-01-01
 */
@Data
@Schema(description = "文章保存参数")
public class ArticleSaveDTO {

    @Schema(description = "文章ID（更新时必填）")
    private Long id;

    @Schema(description = "文章标题")
    @NotBlank(message = "文章标题不能为空")
    private String title;

    @Schema(description = "文章内容")
    @NotBlank(message = "文章内容不能为空")
    private String content;

    @Schema(description = "文章摘要")
    private String summary;

    @Schema(description = "封面图片URL")
    private String coverImage;

    @Schema(description = "来源类型")
    private Article.SourceType sourceType = Article.SourceType.MANUAL;

    @Schema(description = "原文链接")
    private String sourceUrl;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "分类ID")
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @Schema(description = "文章状态")
    private Article.ArticleStatus status = Article.ArticleStatus.DRAFT;

    @Schema(description = "是否置顶")
    private Boolean isTop = false;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;
}