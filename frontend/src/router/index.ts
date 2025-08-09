import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/articles',
      name: 'articles',
      component: () => import('../views/ArticleListView.vue'),
    },
    {
      path: '/articles/:id',
      name: 'article-detail',
      component: () => import('../views/ArticleDetailView.vue'),
    },
    {
      path: '/ai-news',
      name: 'ai-news',
      component: () => import('../views/AiNewsListView.vue'),
    },
    {
      path: '/ai-news/:id',
      name: 'ai-news-detail',
      component: () => import('../views/AiNewsDetailView.vue'),
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
    },
  ],
})

export default router
