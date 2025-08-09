<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, View, ChatDotRound, Star, Share, Calendar, PriceTag } from '@element-plus/icons-vue'
import { getArticleById, incrementViewCount, likeArticle, unlikeArticle } from '@/api/article'
import { getRecommendedArticles } from '@/api/article'
import type { Article } from '@/api/types'

const route = useRoute()
const router = useRouter()

const article = ref<Article | null>(null)
const recommendedArticles = ref<Article[]>([])
const loading = ref(true)
const liking = ref(false)
const isLiked = ref(false)

const loadArticle = async () => {
  try {
    loading.value = true
    const id = Number(route.params.id)
    
    if (!id) {
      ElMessage.error('文章ID无效')
      router.push('/articles')
      return
    }
    
    const response = await getArticleById(id)
    article.value = response.data
    
    // 增加浏览量
    await incrementViewCount(id)
    
    // 加载推荐文章
    loadRecommendedArticles()
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('文章加载失败')
    router.push('/articles')
  } finally {
    loading.value = false
  }
}

const loadRecommendedArticles = async () => {
  try {
    const response = await getRecommendedArticles(6)
    recommendedArticles.value = response.data.filter(item => item.id !== article.value?.id)
  } catch (error) {
    console.error('加载推荐文章失败:', error)
  }
}

const handleLike = async () => {
  if (!article.value || liking.value) return
  
  try {
    liking.value = true
    
    if (isLiked.value) {
      await unlikeArticle(article.value.id)
      article.value.likeCount--
      isLiked.value = false
      ElMessage.success('取消点赞')
    } else {
      await likeArticle(article.value.id)
      article.value.likeCount++
      isLiked.value = true
      ElMessage.success('点赞成功')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    liking.value = false
  }
}

const handleShare = () => {
  if (navigator.share) {
    navigator.share({
      title: article.value?.title,
      text: article.value?.summary,
      url: window.location.href
    })
  } else {
    // 复制链接到剪贴板
    navigator.clipboard.writeText(window.location.href)
    ElMessage.success('链接已复制到剪贴板')
  }
}

const goBack = () => {
  router.back()
}

const goToArticle = (id: number) => {
  router.push(`/articles/${id}`)
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const formatDateTime = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const truncateText = (text: string, maxLength: number = 60) => {
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

onMounted(() => {
  loadArticle()
})
</script>

<template>
  <div class="article-detail-page">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>

    <!-- 文章内容 -->
    <div v-else-if="article" class="article-container">
      <!-- 返回按钮 -->
      <div class="back-button">
        <el-button @click="goBack" :icon="ArrowLeft" circle />
      </div>

      <!-- 文章头部 -->
      <header class="article-header">
        <div class="article-cover" v-if="article.coverImage">
          <img :src="article.coverImage" :alt="article.title" />
        </div>
        
        <div class="article-info">
          <div class="article-badges">
            <el-tag v-if="article.isTop" type="danger" size="large">置顶</el-tag>
            <el-tag v-if="article.isRecommend" type="warning" size="large">推荐</el-tag>
            <el-tag v-if="article.categoryName" size="large">{{ article.categoryName }}</el-tag>
          </div>
          
          <h1 class="article-title">{{ article.title }}</h1>
          
          <div class="article-meta">
            <div class="meta-item">
              <el-icon><Calendar /></el-icon>
              <span>发布于 {{ formatDate(article.createTime) }}</span>
            </div>
            <div class="meta-item" v-if="article.updateTime !== article.createTime">
              <el-icon><Calendar /></el-icon>
              <span>更新于 {{ formatDate(article.updateTime) }}</span>
            </div>
            <div class="meta-item">
              <el-icon><View /></el-icon>
              <span>{{ article.viewCount }} 次阅读</span>
            </div>
            <div class="meta-item">
              <el-icon><ChatDotRound /></el-icon>
              <span>{{ article.commentCount }} 条评论</span>
            </div>
          </div>
          
          <p class="article-summary">{{ article.summary }}</p>
          
          <!-- 标签 -->
          <div class="article-tags" v-if="article.tags && article.tags.length">
            <el-icon><PriceTag /></el-icon>
            <el-tag 
              v-for="tag in article.tags" 
              :key="tag" 
              type="info"
              @click="$router.push(`/articles?keyword=${tag}`)"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </header>

      <!-- 文章内容 -->
      <main class="article-content">
        <div class="content-wrapper">
          <div class="article-body" v-html="article.content"></div>
          
          <!-- 文章操作 -->
          <div class="article-actions">
            <el-button 
              :type="isLiked ? 'primary' : 'default'"
              :loading="liking"
              @click="handleLike"
              size="large"
            >
              <el-icon><Star /></el-icon>
              {{ isLiked ? '已点赞' : '点赞' }} ({{ article.likeCount }})
            </el-button>
            
            <el-button @click="handleShare" size="large">
              <el-icon><Share /></el-icon>
              分享
            </el-button>
          </div>
        </div>
        
        <!-- 侧边栏 -->
        <aside class="article-sidebar">
          <!-- 文章目录 -->
          <div class="toc-section">
            <h3 class="sidebar-title">文章目录</h3>
            <div class="toc-content">
              <!-- 这里可以添加文章目录生成逻辑 -->
              <p class="toc-placeholder">目录将在这里显示</p>
            </div>
          </div>
          
          <!-- 推荐文章 -->
          <div class="recommended-section" v-if="recommendedArticles.length">
            <h3 class="sidebar-title">推荐阅读</h3>
            <div class="recommended-list">
              <div 
                v-for="item in recommendedArticles.slice(0, 5)" 
                :key="item.id"
                class="recommended-item"
                @click="goToArticle(item.id)"
              >
                <div class="recommended-image" v-if="item.coverImage">
                  <img :src="item.coverImage" :alt="item.title" />
                </div>
                <div class="recommended-content">
                  <h4 class="recommended-title">{{ truncateText(item.title, 40) }}</h4>
                  <div class="recommended-meta">
                    <span><el-icon><View /></el-icon> {{ item.viewCount }}</span>
                    <span>{{ formatDate(item.createTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </aside>
      </main>
    </div>

    <!-- 错误状态 -->
    <div v-else class="error-container">
      <el-empty description="文章不存在或已被删除">
        <el-button type="primary" @click="$router.push('/articles')">返回文章列表</el-button>
      </el-empty>
    </div>
  </div>
</template>

<style scoped>
.article-detail-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.loading-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem;
}

.article-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  position: relative;
}

.back-button {
  position: fixed;
  top: 2rem;
  left: 2rem;
  z-index: 100;
}

.article-header {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.article-cover {
  height: 400px;
  overflow: hidden;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.article-info {
  padding: 2rem;
}

.article-badges {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.article-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #333;
  line-height: 1.3;
  margin-bottom: 1rem;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
  color: #666;
  font-size: 0.875rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.article-summary {
  font-size: 1.125rem;
  color: #666;
  line-height: 1.6;
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #2196f3;
}

.article-tags {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.article-tags .el-tag {
  cursor: pointer;
  transition: all 0.3s ease;
}

.article-tags .el-tag:hover {
  transform: scale(1.05);
}

.article-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 2rem;
}

.content-wrapper {
  background: white;
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.article-body {
  font-size: 1.125rem;
  line-height: 1.8;
  color: #333;
  margin-bottom: 2rem;
}

.article-body :deep(h1),
.article-body :deep(h2),
.article-body :deep(h3),
.article-body :deep(h4),
.article-body :deep(h5),
.article-body :deep(h6) {
  margin: 2rem 0 1rem;
  color: #333;
  font-weight: 600;
}

.article-body :deep(h1) { font-size: 2rem; }
.article-body :deep(h2) { font-size: 1.75rem; }
.article-body :deep(h3) { font-size: 1.5rem; }
.article-body :deep(h4) { font-size: 1.25rem; }

.article-body :deep(p) {
  margin-bottom: 1rem;
}

.article-body :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 1rem 0;
}

.article-body :deep(blockquote) {
  border-left: 4px solid #2196f3;
  padding-left: 1rem;
  margin: 1rem 0;
  color: #666;
  font-style: italic;
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 0 8px 8px 0;
}

.article-body :deep(code) {
  background: #f1f3f4;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-family: 'Monaco', 'Consolas', monospace;
  font-size: 0.875rem;
}

.article-body :deep(pre) {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
  overflow-x: auto;
  margin: 1rem 0;
}

.article-body :deep(pre code) {
  background: none;
  padding: 0;
}

.article-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  padding-top: 2rem;
  border-top: 1px solid #f0f0f0;
}

.article-sidebar {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.toc-section,
.recommended-section {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 2rem;
}

.sidebar-title {
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #333;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 0.5rem;
}

.toc-placeholder {
  color: #999;
  font-size: 0.875rem;
  text-align: center;
  padding: 1rem 0;
}

.recommended-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.recommended-item {
  display: flex;
  gap: 0.75rem;
  padding: 0.75rem;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.recommended-item:hover {
  background: #f8f9fa;
  border-color: #e9ecef;
  transform: translateY(-1px);
}

.recommended-image {
  width: 60px;
  height: 45px;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
}

.recommended-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.recommended-content {
  flex: 1;
  min-width: 0;
}

.recommended-title {
  font-size: 0.875rem;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
  margin-bottom: 0.5rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.recommended-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.75rem;
  color: #999;
}

.recommended-meta span {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.error-container {
  max-width: 600px;
  margin: 4rem auto;
  padding: 2rem;
  text-align: center;
}

@media (max-width: 768px) {
  .article-container {
    padding: 1rem;
  }
  
  .back-button {
    position: static;
    margin-bottom: 1rem;
  }
  
  .article-title {
    font-size: 1.75rem;
  }
  
  .article-meta {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .article-content {
    grid-template-columns: 1fr;
  }
  
  .article-sidebar {
    order: -1;
  }
  
  .toc-section,
  .recommended-section {
    position: static;
  }
  
  .article-actions {
    flex-direction: column;
    align-items: center;
  }
}
</style>