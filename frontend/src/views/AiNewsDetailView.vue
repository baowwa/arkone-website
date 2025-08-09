<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  ArrowLeft, 
  View, 
  Star, 
  StarFilled, 
  Share, 
  Calendar, 
  PriceTag,
  Link,
  TrendCharts
} from '@element-plus/icons-vue'
import { getAiNewsById, incrementAiNewsViewCount, likeAiNews, unlikeAiNews, getRecommendedAiNews } from '@/api/aiNews'
import type { AiNews } from '@/api/types'

const route = useRoute()
const router = useRouter()

const aiNews = ref<AiNews | null>(null)
const recommendedNews = ref<AiNews[]>([])
const loading = ref(false)
const isLiked = ref(false)
const likeCount = ref(0)

const newsId = computed(() => Number(route.params.id))

const loadAiNews = async () => {
  try {
    loading.value = true
    const response = await getAiNewsById(newsId.value)
    aiNews.value = response.data
    isLiked.value = false // 默认未点赞，实际应该从用户状态获取
    likeCount.value = response.data.likeCount
    
    // 增加浏览量
    await incrementAiNewsViewCount(newsId.value)
    if (aiNews.value) {
      aiNews.value.viewCount += 1
    }
  } catch (error) {
    console.error('加载AI新闻详情失败:', error)
    ElMessage.error('加载AI新闻详情失败')
    router.push('/ai-news')
  } finally {
    loading.value = false
  }
}

const loadRecommendedNews = async () => {
  try {
    const response = await getRecommendedAiNews(6)
    recommendedNews.value = response.data.filter((news: AiNews) => news.id !== newsId.value)
  } catch (error) {
    console.error('加载推荐AI新闻失败:', error)
  }
}

const handleLike = async () => {
  try {
    if (isLiked.value) {
      await unlikeAiNews(newsId.value)
      isLiked.value = false
      likeCount.value -= 1
      ElMessage.success('已取消点赞')
    } else {
      await likeAiNews(newsId.value)
      isLiked.value = true
      likeCount.value += 1
      ElMessage.success('点赞成功')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const handleShare = () => {
  if (navigator.share) {
    navigator.share({
      title: aiNews.value?.title,
      text: aiNews.value?.summary,
      url: window.location.href
    })
  } else {
    // 复制链接到剪贴板
    navigator.clipboard.writeText(window.location.href).then(() => {
      ElMessage.success('链接已复制到剪贴板')
    })
  }
}

const goBack = () => {
  router.back()
}

const goToNews = (id: number) => {
  router.push(`/ai-news/${id}`)
}

const openSourceUrl = () => {
  if (aiNews.value?.sourceUrl) {
    window.open(aiNews.value.sourceUrl, '_blank')
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const formatContent = (content: string) => {
  // 简单的内容格式化，将换行符转换为段落
  return content.split('\n').filter(p => p.trim()).map(p => `<p>${p}</p>`).join('')
}

onMounted(() => {
  loadAiNews()
  loadRecommendedNews()
})
</script>

<template>
  <div class="ai-news-detail-page" v-loading="loading">
    <div class="container">
      <!-- 返回按钮 -->
      <div class="back-button">
        <el-button @click="goBack" :icon="ArrowLeft" type="primary" plain>
          返回列表
        </el-button>
      </div>

      <div class="content-wrapper" v-if="aiNews">
        <!-- 主内容区 -->
        <article class="main-content">
          <!-- 新闻头部 -->
          <header class="news-header">
            <div class="news-badges" v-if="aiNews.isTop || aiNews.isRecommend">
              <el-tag v-if="aiNews.isTop" type="danger" size="large">置顶</el-tag>
              <el-tag v-if="aiNews.isRecommend" type="warning" size="large">推荐</el-tag>
            </div>
            
            <h1 class="news-title">{{ aiNews.title }}</h1>
            
            <div class="news-meta">
              <div class="meta-left">
                <el-tag v-if="aiNews.categoryName" type="info">{{ aiNews.categoryName }}</el-tag>
                <span class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  {{ formatDate(aiNews.createTime) }}
                </span>
                <span class="meta-item">
                  <el-icon><View /></el-icon>
                  {{ aiNews.viewCount }} 次浏览
                </span>
                <span class="meta-item source-link" @click="openSourceUrl" v-if="aiNews.sourceUrl">
                  <el-icon><Link /></el-icon>
                  {{ aiNews.sourceName }}
                </span>
              </div>
              
              <div class="meta-right">
                <el-button 
                  @click="handleLike" 
                  :type="isLiked ? 'primary' : 'default'"
                  :icon="isLiked ? StarFilled : Star"
                  size="small"
                >
                  {{ likeCount }}
                </el-button>
                <el-button @click="handleShare" :icon="Share" size="small">
                  分享
                </el-button>
              </div>
            </div>
          </header>

          <!-- 封面图片 -->
          <div class="news-cover" v-if="aiNews.coverImage">
            <img :src="aiNews.coverImage" :alt="aiNews.title" />
          </div>

          <!-- 摘要 -->
          <div class="news-summary" v-if="aiNews.summary">
            <h3>摘要</h3>
            <p>{{ aiNews.summary }}</p>
          </div>

          <!-- 正文内容 -->
          <div class="news-content">
            <div v-html="formatContent(aiNews.content)"></div>
          </div>

          <!-- 标签 -->
          <div class="news-tags" v-if="aiNews.tags && aiNews.tags.length">
            <h4><el-icon><PriceTag /></el-icon> 相关标签</h4>
            <div class="tag-list">
              <el-tag 
                v-for="tag in aiNews.tags" 
                :key="tag" 
                size="large"
                type="info"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>

          <!-- 原文链接 -->
          <div class="source-section" v-if="aiNews.sourceUrl">
            <el-divider />
            <div class="source-info">
              <p>本文来源：<strong>{{ aiNews.sourceName }}</strong></p>
              <el-button @click="openSourceUrl" type="primary" :icon="Link">
                查看原文
              </el-button>
            </div>
          </div>
        </article>

        <!-- 侧边栏 -->
        <aside class="sidebar">
          <!-- 推荐新闻 -->
          <div class="recommended-section">
            <h3 class="section-title">
              <el-icon><TrendCharts /></el-icon>
              推荐阅读
            </h3>
            <div class="recommended-list">
              <div 
                v-for="news in recommendedNews.slice(0, 5)" 
                :key="news.id"
                class="recommended-item"
                @click="goToNews(news.id)"
              >
                <div class="item-image" v-if="news.coverImage">
                  <img :src="news.coverImage" :alt="news.title" />
                </div>
                <div class="item-content">
                  <h4 class="item-title">{{ news.title }}</h4>
                  <div class="item-meta">
                    <span><el-icon><View /></el-icon> {{ news.viewCount }}</span>
                    <span><el-icon><Star /></el-icon> {{ news.likeCount }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ai-news-detail-page {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 2rem 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.back-button {
  margin-bottom: 2rem;
}

.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 2rem;
}

.main-content {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.news-header {
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #f0f0f0;
}

.news-badges {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.news-title {
  font-size: 2rem;
  font-weight: 700;
  line-height: 1.3;
  color: #333;
  margin-bottom: 1rem;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.meta-left {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  color: #666;
  font-size: 0.875rem;
}

.source-link {
  color: #ff6b6b;
  cursor: pointer;
  font-weight: 500;
}

.source-link:hover {
  color: #ee5a24;
}

.meta-right {
  display: flex;
  gap: 0.5rem;
}

.news-cover {
  margin-bottom: 2rem;
  border-radius: 8px;
  overflow: hidden;
}

.news-cover img {
  width: 100%;
  height: auto;
  display: block;
}

.news-summary {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 2rem;
  border-left: 4px solid #ff6b6b;
}

.news-summary h3 {
  margin: 0 0 1rem 0;
  color: #333;
  font-size: 1.125rem;
}

.news-summary p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.news-content {
  font-size: 1rem;
  line-height: 1.8;
  color: #333;
  margin-bottom: 2rem;
}

.news-content :deep(p) {
  margin-bottom: 1rem;
}

.news-content :deep(h1),
.news-content :deep(h2),
.news-content :deep(h3),
.news-content :deep(h4),
.news-content :deep(h5),
.news-content :deep(h6) {
  margin: 1.5rem 0 1rem 0;
  color: #333;
}

.news-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 1rem 0;
}

.news-content :deep(blockquote) {
  border-left: 4px solid #ff6b6b;
  padding-left: 1rem;
  margin: 1rem 0;
  color: #666;
  font-style: italic;
}

.news-tags {
  margin-bottom: 2rem;
}

.news-tags h4 {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
  color: #333;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.source-section {
  margin-top: 2rem;
}

.source-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.source-info p {
  margin: 0;
  color: #666;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.recommended-section {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 2rem;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #333;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 0.5rem;
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
  transform: translateX(4px);
}

.item-image {
  flex-shrink: 0;
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-content {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 0.875rem;
  font-weight: 500;
  line-height: 1.4;
  margin-bottom: 0.5rem;
  color: #333;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-meta {
  display: flex;
  gap: 0.75rem;
  font-size: 0.75rem;
  color: #999;
}

.item-meta span {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

@media (max-width: 768px) {
  .content-wrapper {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .sidebar {
    order: -1;
  }
  
  .main-content {
    padding: 1.5rem;
  }
  
  .news-title {
    font-size: 1.5rem;
  }
  
  .news-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }
  
  .meta-left {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .source-info {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
}
</style>