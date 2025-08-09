<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getLatestArticles, getRecommendedArticles } from '@/api/article'
import { getLatestAiNews, getRecommendedAiNews } from '@/api/aiNews'
import type { Article, AiNews } from '@/api/types'

const latestArticles = ref<Article[]>([])
const recommendedArticles = ref<Article[]>([])
const latestAiNews = ref<AiNews[]>([])
const recommendedAiNews = ref<AiNews[]>([])
const loading = ref(true)

const loadData = async () => {
  try {
    loading.value = true
    const [latestArticlesRes, recommendedArticlesRes, latestAiNewsRes, recommendedAiNewsRes] = await Promise.all([
      getLatestArticles(6),
      getRecommendedArticles(4),
      getLatestAiNews(6),
      getRecommendedAiNews(4)
    ])
    
    latestArticles.value = latestArticlesRes.data
    recommendedArticles.value = recommendedArticlesRes.data
    latestAiNews.value = latestAiNewsRes.data
    recommendedAiNews.value = recommendedAiNewsRes.data
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const truncateText = (text: string, maxLength: number = 100) => {
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">欢迎来到 ArkOne</h1>
        <p class="hero-subtitle">探索人工智能的无限可能，分享技术洞察与创新思维</p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="$router.push('/articles')">
            <el-icon><Document /></el-icon>
            浏览文章
          </el-button>
          <el-button size="large" @click="$router.push('/ai-news')">
            <el-icon><TrendCharts /></el-icon>
            AI 资讯
          </el-button>
        </div>
      </div>
    </section>

    <!-- Content Sections -->
    <div class="content-container">
      <!-- 推荐文章 -->
      <section class="content-section">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><Star /></el-icon>
            推荐文章
          </h2>
          <el-button text type="primary" @click="$router.push('/articles')">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        
        <div v-loading="loading" class="article-grid">
          <div 
            v-for="article in recommendedArticles" 
            :key="article.id" 
            class="article-card"
            @click="$router.push(`/articles/${article.id}`)"
          >
            <div class="article-image" v-if="article.coverImage">
              <img :src="article.coverImage" :alt="article.title" />
            </div>
            <div class="article-content">
              <h3 class="article-title">{{ article.title }}</h3>
              <p class="article-summary">{{ truncateText(article.summary) }}</p>
              <div class="article-meta">
                <span class="article-date">{{ formatDate(article.createTime) }}</span>
                <div class="article-stats">
                  <span><el-icon><View /></el-icon> {{ article.viewCount }}</span>
                  <span><el-icon><ChatDotRound /></el-icon> {{ article.commentCount }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 最新文章 -->
      <section class="content-section">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><Document /></el-icon>
            最新文章
          </h2>
          <el-button text type="primary" @click="$router.push('/articles')">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        
        <div v-loading="loading" class="article-list">
          <div 
            v-for="article in latestArticles" 
            :key="article.id" 
            class="article-item"
            @click="$router.push(`/articles/${article.id}`)"
          >
            <div class="article-item-content">
              <h4 class="article-item-title">{{ article.title }}</h4>
              <p class="article-item-summary">{{ truncateText(article.summary, 80) }}</p>
              <div class="article-item-meta">
                <el-tag size="small" v-if="article.categoryName">{{ article.categoryName }}</el-tag>
                <span class="article-item-date">{{ formatDate(article.createTime) }}</span>
              </div>
            </div>
            <div class="article-item-image" v-if="article.coverImage">
              <img :src="article.coverImage" :alt="article.title" />
            </div>
          </div>
        </div>
      </section>

      <!-- AI 资讯 -->
      <section class="content-section">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><TrendCharts /></el-icon>
            AI 资讯
          </h2>
          <el-button text type="primary" @click="$router.push('/ai-news')">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        
        <div v-loading="loading" class="news-grid">
          <div 
            v-for="news in latestAiNews" 
            :key="news.id" 
            class="news-card"
            @click="$router.push(`/ai-news/${news.id}`)"
          >
            <div class="news-image" v-if="news.coverImage">
              <img :src="news.coverImage" :alt="news.title" />
            </div>
            <div class="news-content">
              <h4 class="news-title">{{ news.title }}</h4>
              <p class="news-summary">{{ truncateText(news.summary, 60) }}</p>
              <div class="news-meta">
                <span class="news-source">{{ news.sourceName }}</span>
                <span class="news-date">{{ formatDate(news.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
}

.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 120px 0 80px;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  background: linear-gradient(45deg, #fff, #e0e7ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 1.25rem;
  margin-bottom: 2rem;
  opacity: 0.9;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.content-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.content-section {
  margin: 60px 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #f0f0f0;
}

.section-title {
  font-size: 1.75rem;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.article-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.article-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.article-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.article-image {
  height: 200px;
  overflow: hidden;
}

.article-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.article-card:hover .article-image img {
  transform: scale(1.05);
}

.article-content {
  padding: 1.5rem;
}

.article-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: #333;
  line-height: 1.4;
}

.article-summary {
  color: #666;
  line-height: 1.6;
  margin-bottom: 1rem;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.875rem;
  color: #999;
}

.article-stats {
  display: flex;
  gap: 1rem;
}

.article-stats span {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.article-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.article-item {
  display: flex;
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.article-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.article-item-content {
  flex: 1;
  margin-right: 1rem;
}

.article-item-title {
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #333;
}

.article-item-summary {
  color: #666;
  line-height: 1.5;
  margin-bottom: 0.75rem;
}

.article-item-meta {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.875rem;
}

.article-item-date {
  color: #999;
}

.article-item-image {
  width: 120px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.article-item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
}

.news-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.news-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.news-image {
  height: 150px;
  overflow: hidden;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.news-content {
  padding: 1rem;
}

.news-title {
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #333;
  line-height: 1.4;
}

.news-summary {
  color: #666;
  font-size: 0.875rem;
  line-height: 1.5;
  margin-bottom: 0.75rem;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.75rem;
  color: #999;
}

.news-source {
  background: #f0f0f0;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-weight: 500;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-subtitle {
    font-size: 1.125rem;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .article-grid {
    grid-template-columns: 1fr;
  }
  
  .article-item {
    flex-direction: column;
  }
  
  .article-item-content {
    margin-right: 0;
    margin-bottom: 1rem;
  }
  
  .article-item-image {
    width: 100%;
    height: 150px;
  }
}
</style>
