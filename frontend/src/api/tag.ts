import request from '@/utils/request'
import type { ApiResponse, PageResponse, Tag, TagQuery, TagSaveDTO, TagStats } from './types'

// 分页查询标签
export const getTags = (params: TagQuery): Promise<ApiResponse<PageResponse<Tag>>> => {
  return request({
    url: '/tags',
    method: 'get',
    params
  })
}

// 根据ID获取标签详情
export const getTagById = (id: number): Promise<ApiResponse<Tag>> => {
  return request({
    url: `/tags/${id}`,
    method: 'get'
  })
}

// 创建标签
export const createTag = (data: TagSaveDTO): Promise<ApiResponse<Tag>> => {
  return request({
    url: '/tags',
    method: 'post',
    data
  })
}

// 更新标签
export const updateTag = (id: number, data: TagSaveDTO): Promise<ApiResponse<Tag>> => {
  return request({
    url: `/tags/${id}`,
    method: 'put',
    data
  })
}

// 删除标签
export const deleteTag = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/tags/${id}`,
    method: 'delete'
  })
}

// 批量删除标签
export const batchDeleteTags = (ids: number[]): Promise<ApiResponse<void>> => {
  return request({
    url: '/tags/batch',
    method: 'delete',
    data: ids
  })
}

// 获取所有启用的标签
export const getAllEnabledTags = (): Promise<ApiResponse<Tag[]>> => {
  return request({
    url: '/tags/enabled',
    method: 'get'
  })
}

// 根据类型获取标签列表
export const getTagsByType = (type: 'ARTICLE' | 'AI_NEWS' | 'COMMON'): Promise<ApiResponse<Tag[]>> => {
  return request({
    url: '/tags/type',
    method: 'get',
    params: { type }
  })
}

// 搜索标签
export const searchTags = (keyword: string): Promise<ApiResponse<Tag[]>> => {
  return request({
    url: '/tags/search',
    method: 'get',
    params: { keyword }
  })
}

// 检查标签名称是否存在
export const checkTagNameExists = (name: string, excludeId?: number): Promise<ApiResponse<boolean>> => {
  return request({
    url: '/tags/exists',
    method: 'get',
    params: { name, excludeId }
  })
}

// 获取标签下的内容数量
export const getTagContentCount = (tagId: number): Promise<ApiResponse<number>> => {
  return request({
    url: `/tags/${tagId}/count`,
    method: 'get'
  })
}

// 获取热门标签
export const getPopularTags = (limit: number = 20): Promise<ApiResponse<Tag[]>> => {
  return request({
    url: '/tags/popular',
    method: 'get',
    params: { limit }
  })
}

// 获取标签统计信息
export const getTagStats = (): Promise<ApiResponse<TagStats>> => {
  return request({
    url: '/tags/stats',
    method: 'get'
  })
}

// 根据名称获取或创建标签
export const getOrCreateTagByName = (name: string, type: 'ARTICLE' | 'AI_NEWS' | 'COMMON' = 'COMMON'): Promise<ApiResponse<Tag>> => {
  return request({
    url: '/tags/get-or-create',
    method: 'post',
    data: { name, type }
  })
}

// 批量根据名称获取或创建标签
export const batchGetOrCreateTagsByNames = (names: string[], type: 'ARTICLE' | 'AI_NEWS' | 'COMMON' = 'COMMON'): Promise<ApiResponse<Tag[]>> => {
  return request({
    url: '/tags/batch-get-or-create',
    method: 'post',
    data: { names, type }
  })
}

// 更新标签状态
export const updateTagStatus = (id: number, status: 'ACTIVE' | 'INACTIVE'): Promise<ApiResponse<void>> => {
  return request({
    url: `/tags/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 更新标签排序
export const updateTagSort = (id: number, sortOrder: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/tags/${id}/sort`,
    method: 'put',
    data: { sortOrder }
  })
}

// 更新标签颜色
export const updateTagColor = (id: number, color: string): Promise<ApiResponse<void>> => {
  return request({
    url: `/tags/${id}/color`,
    method: 'put',
    data: { color }
  })
}

// 获取标签云数据
export const getTagCloud = (limit: number = 50): Promise<ApiResponse<Array<{ name: string; value: number; color?: string }>>> => {
  return request({
    url: '/tags/cloud',
    method: 'get',
    params: { limit }
  })
}