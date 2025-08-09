import request from '@/utils/request'
import type { ApiResponse, Category, CategorySaveDTO } from './types'

// 根据类型获取分类列表
export const getCategoriesByType = (type: 'ARTICLE' | 'AI_NEWS'): Promise<ApiResponse<Category[]>> => {
  return request({
    url: '/categories/type',
    method: 'get',
    params: { type }
  })
}

// 获取所有启用的分类
export const getAllEnabledCategories = (): Promise<ApiResponse<Category[]>> => {
  return request({
    url: '/categories/enabled',
    method: 'get'
  })
}

// 根据父分类ID获取子分类
export const getCategoriesByParentId = (parentId: number): Promise<ApiResponse<Category[]>> => {
  return request({
    url: '/categories/children',
    method: 'get',
    params: { parentId }
  })
}

// 根据ID获取分类详情
export const getCategoryById = (id: number): Promise<ApiResponse<Category>> => {
  return request({
    url: `/categories/${id}`,
    method: 'get'
  })
}

// 创建分类
export const createCategory = (data: CategorySaveDTO): Promise<ApiResponse<Category>> => {
  return request({
    url: '/categories',
    method: 'post',
    data
  })
}

// 更新分类
export const updateCategory = (id: number, data: CategorySaveDTO): Promise<ApiResponse<Category>> => {
  return request({
    url: `/categories/${id}`,
    method: 'put',
    data
  })
}

// 删除分类
export const deleteCategory = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/categories/${id}`,
    method: 'delete'
  })
}

// 检查分类名称是否存在
export const checkCategoryNameExists = (name: string, excludeId?: number): Promise<ApiResponse<boolean>> => {
  return request({
    url: '/categories/exists',
    method: 'get',
    params: { name, excludeId }
  })
}

// 获取分类下的内容数量
export const getCategoryContentCount = (categoryId: number): Promise<ApiResponse<number>> => {
  return request({
    url: `/categories/${categoryId}/count`,
    method: 'get'
  })
}

// 构建分类树
export const buildCategoryTree = (type?: 'ARTICLE' | 'AI_NEWS'): Promise<ApiResponse<Category[]>> => {
  return request({
    url: '/categories/tree',
    method: 'get',
    params: type ? { type } : {}
  })
}

// 获取所有分类（平铺结构）
export const getAllCategories = (): Promise<ApiResponse<Category[]>> => {
  return request({
    url: '/categories',
    method: 'get'
  })
}

// 批量删除分类
export const batchDeleteCategories = (ids: number[]): Promise<ApiResponse<void>> => {
  return request({
    url: '/categories/batch',
    method: 'delete',
    data: ids
  })
}

// 更新分类状态
export const updateCategoryStatus = (id: number, status: 'ACTIVE' | 'INACTIVE'): Promise<ApiResponse<void>> => {
  return request({
    url: `/categories/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 更新分类排序
export const updateCategorySort = (id: number, sortOrder: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/categories/${id}/sort`,
    method: 'put',
    data: { sortOrder }
  })
}

// 移动分类到新的父分类下
export const moveCategoryToParent = (id: number, parentId: number | null): Promise<ApiResponse<void>> => {
  return request({
    url: `/categories/${id}/move`,
    method: 'put',
    data: { parentId }
  })
}