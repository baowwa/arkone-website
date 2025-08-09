import request from '@/utils/request'
import type { ApiResponse, PageResponse, AiNews, AiNewsQuery, AiNewsSaveDTO } from './types'

// 分页查询AI新闻
export const getAiNews = (params: AiNewsQuery): Promise<ApiResponse<PageResponse<AiNews>>> => {
  return request({
    url: '/ai-news',
    method: 'get',
    params
  })
}

// 根据ID获取AI新闻详情
export const getAiNewsById = (id: number): Promise<ApiResponse<AiNews>> => {
  return request({
    url: `/ai-news/${id}`,
    method: 'get'
  })
}

// 创建AI新闻
export const createAiNews = (data: AiNewsSaveDTO): Promise<ApiResponse<AiNews>> => {
  return request({
    url: '/ai-news',
    method: 'post',
    data
  })
}

// 更新AI新闻
export const updateAiNews = (id: number, data: AiNewsSaveDTO): Promise<ApiResponse<AiNews>> => {
  return request({
    url: `/ai-news/${id}`,
    method: 'put',
    data
  })
}

// 删除AI新闻
export const deleteAiNews = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/ai-news/${id}`,
    method: 'delete'
  })
}

// 批量删除AI新闻
export const batchDeleteAiNews = (ids: number[]): Promise<ApiResponse<void>> => {
  return request({
    url: '/ai-news/batch',
    method: 'delete',
    data: ids
  })
}

// 发布AI新闻
export const publishAiNews = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/ai-news/${id}/publish`,
    method: 'put'
  })
}

// 取消发布AI新闻
export const unpublishAiNews = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/ai-news/${id}/unpublish`,
    method: 'put'
  })
}

// 置顶AI新闻
export const topAiNews = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/ai-news/${id}/top`,
    method: 'put'
  })
}

// 取消置顶AI新闻
export const untopAiNews = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/ai-news/${id}/untop`,
    method: 'put'
  })
}

// 推荐AI新闻
export const recommendAiNews = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/ai-news/${id}/recommend`,
    method: 'put'
  })
}

// 取消推荐AI新闻
export const unrecommendAiNews = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/ai-news/${id}/unrecommend`,
    method: 'put'
  })
}

// 增加AI新闻浏览量
export const incrementAiNewsViewCount = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/ai-news/${id}/view`,
    method: 'put'
  })
}

// 点赞AI新闻
export const likeAiNews = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/ai-news/${id}/like`,
    method: 'put'
  })
}

// 取消点赞AI新闻
export const unlikeAiNews = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/ai-news/${id}/unlike`,
    method: 'put'
  })
}

// 获取推荐AI新闻
export const getRecommendedAiNews = (limit: number = 10): Promise<ApiResponse<AiNews[]>> => {
  return request({
    url: '/ai-news/recommended',
    method: 'get',
    params: { limit }
  })
}

// 获取热门AI新闻
export const getPopularAiNews = (limit: number = 10): Promise<ApiResponse<AiNews[]>> => {
  return request({
    url: '/ai-news/popular',
    method: 'get',
    params: { limit }
  })
}

// 获取最新AI新闻
export const getLatestAiNews = (limit: number = 10): Promise<ApiResponse<AiNews[]>> => {
  return request({
    url: '/ai-news/latest',
    method: 'get',
    params: { limit }
  })
}

// 搜索AI新闻
export const searchAiNews = (keyword: string, params?: AiNewsQuery): Promise<ApiResponse<PageResponse<AiNews>>> => {
  return request({
    url: '/ai-news/search',
    method: 'get',
    params: {
      keyword,
      ...params
    }
  })
}

// 根据来源获取AI新闻
export const getAiNewsBySource = (sourceName: string, params?: AiNewsQuery): Promise<ApiResponse<PageResponse<AiNews>>> => {
  return request({
    url: '/ai-news/source',
    method: 'get',
    params: {
      sourceName,
      ...params
    }
  })
}