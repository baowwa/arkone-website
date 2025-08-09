// 通用响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页响应类型
export interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 分页查询参数
export interface PageQuery {
  current?: number
  size?: number
}

// 文章相关类型
export interface Article {
  id: number
  title: string
  content: string
  summary: string
  coverImage?: string
  categoryId: number
  categoryName?: string
  tags: string[]
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'
  viewCount: number
  likeCount: number
  commentCount: number
  isTop: boolean
  isRecommend: boolean
  publishTime?: string
  createTime: string
  updateTime: string
}

export interface ArticleQuery extends PageQuery {
  title?: string
  categoryId?: number
  status?: string
  isTop?: boolean
  isRecommend?: boolean
  startTime?: string
  endTime?: string
}

export interface ArticleSaveDTO {
  title: string
  content: string
  summary: string
  coverImage?: string
  categoryId: number
  tags: string[]
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'
  isTop?: boolean
  isRecommend?: boolean
  publishTime?: string
}

// AI新闻相关类型
export interface AiNews {
  id: number
  title: string
  content: string
  summary: string
  sourceUrl: string
  sourceName: string
  coverImage?: string
  categoryId: number
  categoryName?: string
  tags: string[]
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'
  viewCount: number
  likeCount: number
  commentCount: number
  isTop: boolean
  isRecommend: boolean
  publishTime?: string
  createTime: string
  updateTime: string
}

export interface AiNewsQuery extends PageQuery {
  title?: string
  categoryId?: number
  status?: string
  sourceName?: string
  isTop?: boolean
  isRecommend?: boolean
  startTime?: string
  endTime?: string
}

export interface AiNewsSaveDTO {
  title: string
  content: string
  summary: string
  sourceUrl: string
  sourceName: string
  coverImage?: string
  categoryId: number
  tags: string[]
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'
  isTop?: boolean
  isRecommend?: boolean
  publishTime?: string
}

// 分类相关类型
export interface Category {
  id: number
  name: string
  description?: string
  type: 'ARTICLE' | 'AI_NEWS'
  parentId?: number
  sortOrder: number
  status: 'ACTIVE' | 'INACTIVE'
  createTime: string
  updateTime: string
  children?: Category[]
}

export interface CategorySaveDTO {
  name: string
  description?: string
  type: 'ARTICLE' | 'AI_NEWS'
  parentId?: number
  sortOrder?: number
  status?: 'ACTIVE' | 'INACTIVE'
}

// 标签相关类型
export interface Tag {
  id: number
  name: string
  description?: string
  type: 'ARTICLE' | 'AI_NEWS' | 'COMMON'
  color?: string
  usageCount: number
  sortOrder: number
  status: 'ACTIVE' | 'INACTIVE'
  createTime: string
  updateTime: string
}

export interface TagQuery extends PageQuery {
  name?: string
  type?: string
  status?: string
}

export interface TagSaveDTO {
  name: string
  description?: string
  type: 'ARTICLE' | 'AI_NEWS' | 'COMMON'
  color?: string
  sortOrder?: number
  status?: 'ACTIVE' | 'INACTIVE'
}

// 统计信息类型
export interface TagStats {
  totalTags: number
  activeTags: number
  totalUsage: number
  averageUsage: number
}