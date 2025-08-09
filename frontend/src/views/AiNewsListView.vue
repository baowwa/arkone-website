<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, Refresh, TrendCharts, View, ChatDotRound, Star } from '@element-plus/icons-vue'
import { getAiNews, searchAiNews } from '@/api/aiNews'
import { getAllEnabledCategories } from '@/api/category'
import { getPopularTags } from '@/api/tag'
import type { AiNews, AiNewsQuery, Category, Tag } from '@/api/types'

const route = useRoute()
const router = useRouter()

const aiNews = ref<AiNews[]>([])
const categories = ref<Category[]>([])
const tags = ref<Tag[]>([])
const loading = ref(false)
const total = ref(0)

const queryParams = ref<AiNewsQuery>({
  current: 1,
  size: 12,
  title: '',
  categoryId: undefined,
  status: 'PUBLISHED',
  sourceName: '',
  isTop: undefined,
  isRecommend: undefined
})

const searchKeyword = ref('')
const selectedCategory = ref<number | undefined>()
const selectedSource = ref('')
const sortBy = ref('createTime')
const sortOrder = ref('desc')

const loadAiNews = async () => {
  try {
    loading.value = true
    const params = {
      ...queryParams.value,
      categoryId: selectedCategory.value,
      title: searchKeyword.value || undefined,
      sourceName: selectedSource.value || undefined
    }
    
    let response
    if (searchKeyword.value) {
      response = await searchAiNews(searchKeyword.value, params)
    } else {
      response = await getAiNews(params)
    }
    
    aiNews.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('加载AI新闻失败:', error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const response = await getAllEnabledCategories()
    categories.value = response.data.filter(cat => cat.type === 'AI_NEWS')
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadTags = async () => {
  try {
    const response = await getPopularTags(20)
    tags.value = response.data.filter(tag => tag.type === 'AI_NEWS' || tag.type === 'COMMON')
  } catch (error) {
    console.error('加载标签失败:', error)
  }
}

const handleSearch = () => {
  queryParams.value.current = 1
  loadAiNews()
}

const handleCategoryChange = () => {
  queryParams.value.current = 1
  loadAiNews()
}

const handlePageChange = (page: number) => {
  queryParams.value.current = page
  loadAiNews()
}

const handleSizeChange = (size: number) => {
  queryParams.value.size = size
  queryParams.value.current = 1
  loadAiNews()
}

const clearFilters = () => {
  searchKeyword.value = ''
  selectedCategory.value = undefined
  selectedSource.value = ''
  queryParams.value.current = 1
  loadAiNews()
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const truncateText = (text: string, maxLength: number = 150) => {
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

const goToNews = (id: number) => {
  router.push(`/ai-news/${id}`)
}

const handleTagClick = (tagName: string) => {
  searchKeyword.value = tagName
  handleSearch()
}

const openSourceUrl = (url: string, event: Event) => {
  event.stopPropagation()
  window.open(url, '_blank')
}

watch([selectedCategory, selectedSource], () => {
  handleCategoryChange()
})

onMounted(() => {
  // 从路由参数中获取初始搜索条件
  if (route.query.keyword) {
    searchKeyword.value = route.query.keyword as string
  }
  if (route.query.categoryId) {
    selectedCategory.value = Number(route.query.categoryId)
  }
  if (route.query.source) {
    selectedSource.value = route.query.source as string
  }
  
  loadCategories()
  loadTags()
  loadAiNews()
})
</script>

<template>
  <div class="ai-news-list-page">
    <div class="page-header">
      <div class="container">
        <h1 class="page-title">
          <el-icon><TrendCharts /></el-icon>
          AI 资讯
        </h1>
        <p class="page-subtitle">最新的人工智能资讯与动态</p>
      </div>
    </div>

    <div class="container">
      <div class="content-wrapper">
        <!-- 侧边栏 -->
        <aside class="sidebar">
          <!-- 搜索框 -->
          <div class="search-section">
            <h3 class="section-title">搜索资讯</h3>
            <el-input
              v-model="searchKeyword"
              placeholder="输入关键词搜索..."
              @keyup.enter="handleSearch"
              clearable
            >
              <template #append>
                <el-button @click="handleSearch" :icon="Search" />
              </template>
            </el-input>
          </div>

          <!-- 分类筛选 -->
          <div class="filter-section">
            <h3 class="section-title">资讯分类</h3>
            <div class="category-list">
              <div 
                class="category-item"
                :class="{ active: !selectedCategory }"
                @click="selectedCategory = undefined"
              >
                <span>全部分类</span>
              </div>
              <div 
                v-for="category in categories"
                :key="category.id"
                class="category-item"
                :class="{ active: selectedCategory === category.id }"
                @click="selectedCategory = category.id"
              >
                <span>{{ category.name }}</span>
              </div>
            </div>
          </div>

          <!-- 来源筛选 -->
          <div class="filter-section">
            <h3 class="section-title">资讯来源</h3>
            <el-input
              v-model="selectedSource"
              placeholder="输入来源名称..."
              clearable
            />
          </div>

          <!-- 热门标签 -->
          <div class="filter-section">
            <h3 class="section-title">热门标签</h3>
            <div class="tag-cloud">
              <el-tag
                v-for="tag in tags"
                :key="tag.id"
                class="tag-item"
                @click="handleTagClick(tag.name)"
                :color="tag.color"
              >
                {{ tag.name }}
              </el-tag>
            </div>
          </div>

          <!-- 清除筛选 -->
          <div class="filter-actions">
            <el-button @click="clearFilters" :icon="Refresh">清除筛选</el-button>
          </div>
        </aside>

        <!-- 主内容区 -->
        <main class="main-content">
          <!-- 工具栏 -->
          <div class="toolbar">
            <div class="result-info">
              <span v-if="searchKeyword">搜索 "{{ searchKeyword }}" 的结果：</span>
              共找到 {{ total }} 条资讯
            </div>
            <div class="sort-controls">
              <el-select v-model="sortBy" placeholder="排序方式" style="width: 120px">
                <el-option label="发布时间" value="createTime" />
                <el-option label="浏览量" value="viewCount" />
                <el-option label="点赞数" value="likeCount" />
              </el-select>
              <el-select v-model="sortOrder" placeholder="排序" style="width: 100px">
                <el-option label="降序" value="desc" />
                <el-option label="升序" value="asc" />
              </el-select>
            </div>
          </div>

          <!-- 资讯列表 -->
          <div v-loading="loading" class="news-list">
            <div 
              v-for="news in aiNews"
              :key="news.id"
              class="news-card"
              @click="goToNews(news.id)"
            >
              <div class="news-image" v-if="news.coverImage">
                <img :src="news.coverImage" :alt="news.title" />
                <div class="news-badges">
                  <el-tag v-if="news.isTop" type="danger" size="small">置顶</el-tag>
                  <el-tag v-if="news.isRecommend" type="warning" size="small">推荐</el-tag>
                </div>
              </div>
              
              <div class="news-content">
                <div class="news-header">
                  <h3 class="news-title">{{ news.title }}</h3>
                  <div class="news-meta">
                    <el-tag size="small" v-if="news.categoryName">{{ news.categoryName }}</el-tag>
                    <span class="news-source" @click="openSourceUrl(news.sourceUrl, $event)">{{ news.sourceName }}</span>
                    <span class="news-date">{{ formatDate(news.createTime) }}</span>
                  </div>
                </div>
                
                <p class="news-summary">{{ truncateText(news.summary) }}</p>
                
                <div class="news-tags" v-if="news.tags && news.tags.length">
                  <el-tag 
                    v-for="tag in news.tags.slice(0, 3)" 
                    :key="tag" 
                    size="small" 
                    type="info"
                    @click.stop="handleTagClick(tag)"
                  >
                    {{ tag }}
                  </el-tag>
                  <span v-if="news.tags.length > 3" class="more-tags">+{{ news.tags.length - 3 }}</span>
                </div>
                
                <div class="news-stats">
                  <span><el-icon><View /></el-icon> {{ news.viewCount }}</span>
                  <span><el-icon><ChatDotRound /></el-icon> {{ news.commentCount }}</span>
                  <span><el-icon><Star /></el-icon> {{ news.likeCount }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div class="pagination-wrapper" v-if="total > 0">
            <el-pagination
              v-model:current-page="queryParams.current"
              v-model:page-size="queryParams.size"
              :total="total"
              :page-sizes="[12, 24, 48]"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange"
              @size-change="handleSizeChange"
            />
          </div>

          <!-- 空状态 -->
          <el-empty v-if="!loading && aiNews.length === 0" description="暂无资讯" />
        </main>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ai-news-list-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.page-header {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  color: white;
  padding: 60px 0 40px;
  text-align: center;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.page-subtitle {
  font-size: 1.125rem;
  opacity: 0.9;
}

.content-wrapper {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 2rem;
  margin-top: 2rem;
}

.sidebar {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  height: fit-content;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 2rem;
}

.section-title {
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #333;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 0.5rem;
}

.search-section {
  margin-bottom: 2rem;
}

.filter-section {
  margin-bottom: 2rem;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.category-item {
  padding: 0.75rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.category-item:hover {
  background: #f8f9fa;
  border-color: #e9ecef;
}

.category-item.active {
  background: #fff3e0;
  border-color: #ff9800;
  color: #f57c00;
  font-weight: 500;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag-item {
  cursor: pointer;
  transition: all 0.3s ease;
}

.tag-item:hover {
  transform: scale(1.05);
}

.filter-actions {
  text-align: center;
}

.main-content {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f0f0f0;
}

.result-info {
  color: #666;
  font-size: 0.875rem;
}

.sort-controls {
  display: flex;
  gap: 0.5rem;
}

.news-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.news-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #f0f0f0;
}

.news-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  border-color: #e0e0e0;
}

.news-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.news-card:hover .news-image img {
  transform: scale(1.05);
}

.news-badges {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  display: flex;
  gap: 0.5rem;
}

.news-content {
  padding: 1.5rem;
}

.news-header {
  margin-bottom: 1rem;
}

.news-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-meta {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.875rem;
  flex-wrap: wrap;
}

.news-source {
  color: #ff6b6b;
  cursor: pointer;
  font-weight: 500;
  text-decoration: underline;
}

.news-source:hover {
  color: #ee5a24;
}

.news-date {
  color: #999;
}

.news-summary {
  color: #666;
  line-height: 1.6;
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
  align-items: center;
}

.more-tags {
  color: #999;
  font-size: 0.75rem;
}

.news-stats {
  display: flex;
  gap: 1rem;
  font-size: 0.875rem;
  color: #999;
}

.news-stats span {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}

@media (max-width: 768px) {
  .content-wrapper {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .sidebar {
    order: 2;
  }
  
  .main-content {
    order: 1;
  }
  
  .news-list {
    grid-template-columns: 1fr;
  }
  
  .toolbar {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .sort-controls {
    justify-content: center;
  }
  
  .news-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
}
</style>