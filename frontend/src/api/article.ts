import request from '@/utils/request'
import type { ApiResponse, PageResponse, Article, ArticleQuery, ArticleSaveDTO } from './types'

// 分页查询文章
export const getArticles = (params: ArticleQuery): Promise<ApiResponse<PageResponse<Article>>> => {
  return request({
    url: '/articles',
    method: 'get',
    params
  })
}

// 根据ID获取文章详情
export const getArticleById = (id: number): Promise<ApiResponse<Article>> => {
  return request({
    url: `/articles/${id}`,
    method: 'get'
  })
}

// 创建文章
export const createArticle = (data: ArticleSaveDTO): Promise<ApiResponse<Article>> => {
  return request({
    url: '/articles',
    method: 'post',
    data
  })
}

// 更新文章
export const updateArticle = (id: number, data: ArticleSaveDTO): Promise<ApiResponse<Article>> => {
  return request({
    url: `/articles/${id}`,
    method: 'put',
    data
  })
}

// 删除文章
export const deleteArticle = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/articles/${id}`,
    method: 'delete'
  })
}

// 批量删除文章
export const batchDeleteArticles = (ids: number[]): Promise<ApiResponse<void>> => {
  return request({
    url: '/articles/batch',
    method: 'delete',
    data: ids
  })
}

// 发布文章
export const publishArticle = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/articles/${id}/publish`,
    method: 'put'
  })
}

// 取消发布文章
export const unpublishArticle = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/articles/${id}/unpublish`,
    method: 'put'
  })
}

// 置顶文章
export const topArticle = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/articles/${id}/top`,
    method: 'put'
  })
}

// 取消置顶文章
export const untopArticle = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/articles/${id}/untop`,
    method: 'put'
  })
}

// 推荐文章
export const recommendArticle = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/articles/${id}/recommend`,
    method: 'put'
  })
}

// 取消推荐文章
export const unrecommendArticle = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/articles/${id}/unrecommend`,
    method: 'put'
  })
}

// 增加文章浏览量
export const incrementViewCount = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/articles/${id}/view`,
    method: 'put'
  })
}

// 点赞文章
export const likeArticle = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/articles/${id}/like`,
    method: 'put'
  })
}

// 取消点赞文章
export const unlikeArticle = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/articles/${id}/unlike`,
    method: 'put'
  })
}

// 获取推荐文章
export const getRecommendedArticles = (limit: number = 10): Promise<ApiResponse<Article[]>> => {
  return request({
    url: '/articles/recommended',
    method: 'get',
    params: { limit }
  })
}

// 获取热门文章
export const getPopularArticles = (limit: number = 10): Promise<ApiResponse<Article[]>> => {
  return request({
    url: '/articles/popular',
    method: 'get',
    params: { limit }
  })
}

// 获取最新文章
export const getLatestArticles = (limit: number = 10): Promise<ApiResponse<Article[]>> => {
  return request({
    url: '/articles/latest',
    method: 'get',
    params: { limit }
  })
}

// 搜索文章
export const searchArticles = (keyword: string, params?: ArticleQuery): Promise<ApiResponse<PageResponse<Article>>> => {
  return request({
    url: '/articles/search',
    method: 'get',
    params: {
      keyword,
      ...params
    }
  })
}