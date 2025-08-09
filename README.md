# ArkOne Website

一个基于Spring Boot + Vue.js的现代化内容管理系统，专注于文章发布和AI资讯聚合。

## 🚀 技术栈

### 后端技术栈
- **Spring Boot 3.x** - 现代化Java应用框架
- **MyBatis Plus** - 高效的ORM框架
- **MySQL** - 关系型数据库
- **Swagger/OpenAPI 3** - API文档生成
- **Maven** - 项目构建管理
- **Lombok** - 简化Java代码

### 前端技术栈
- **Vue 3** - 渐进式JavaScript框架
- **TypeScript** - 类型安全的JavaScript超集
- **Element Plus** - 企业级UI组件库
- **Vue Router** - 官方路由管理器
- **Pinia** - 现代化状态管理
- **Axios** - HTTP客户端
- **Vite** - 快速构建工具

## 📋 功能模块

### 文章管理
- ✅ 文章的增删改查
- ✅ 文章分类管理
- ✅ 标签系统
- ✅ 文章发布/草稿状态
- ✅ 置顶和推荐功能
- ✅ 浏览量统计
- ✅ 点赞功能
- ✅ 文章搜索
- ✅ 分页查询

### AI资讯管理
- ✅ AI新闻的增删改查
- ✅ 新闻来源管理
- ✅ 分类和标签支持
- ✅ 发布状态控制
- ✅ 置顶和推荐
- ✅ 浏览量和点赞统计
- ✅ 原文链接跳转
- ✅ 搜索和筛选

### 分类管理
- ✅ 树形分类结构
- ✅ 分类启用/禁用
- ✅ 排序功能
- ✅ 分类统计

### 标签管理
- ✅ 标签的增删改查
- ✅ 标签颜色自定义
- ✅ 热门标签统计
- ✅ 标签云展示

### 前端页面
- ✅ 响应式首页设计
- ✅ 文章列表和详情页
- ✅ AI资讯列表和详情页
- ✅ 搜索和筛选功能
- ✅ 分页导航
- ✅ 移动端适配

## 🏗️ 项目结构

```
arkone-website/
├── backend/                 # 后端Spring Boot项目
│   ├── src/main/java/
│   │   └── com/arkone/
│   │       ├── controller/  # 控制器层
│   │       ├── service/     # 服务层
│   │       ├── entity/      # 实体类
│   │       ├── dto/         # 数据传输对象
│   │       └── config/      # 配置类
│   └── pom.xml             # Maven配置
├── frontend/               # 前端Vue.js项目
│   ├── src/
│   │   ├── api/            # API接口定义
│   │   ├── components/     # 公共组件
│   │   ├── views/          # 页面组件
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # 状态管理
│   │   └── utils/          # 工具函数
│   └── package.json        # 依赖配置
├── docker-compose.yml      # Docker编排配置
├── nginx.conf             # Nginx配置
└── README.md              # 项目说明
```

## 🛠️ 开发环境搭建

### 环境要求
- **Java**: JDK 17+
- **Node.js**: 18+
- **MySQL**: 8.0+
- **Maven**: 3.6+
- **Git**: 最新版本

### 后端启动

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd arkone-website
   ```

2. **配置数据库**
   ```sql
   CREATE DATABASE arkone_website CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **修改配置文件**
   ```bash
   cd backend/src/main/resources
   cp application.yml.example application.yml
   # 编辑application.yml，配置数据库连接信息
   ```

4. **启动后端服务**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

   后端服务将在 `http://localhost:8080` 启动

### 前端启动

1. **安装依赖**
   ```bash
   cd frontend
   npm install
   ```

2. **启动开发服务器**
   ```bash
   npm run dev
   ```

   前端服务将在 `http://localhost:5173` 启动

## 🚀 生产环境部署

### 方式一：Docker部署（推荐）

1. **构建和启动服务**
   ```bash
   # 构建前端
   cd frontend
   npm run build
   
   # 返回项目根目录
   cd ..
   
   # 使用Docker Compose启动所有服务
   docker-compose up -d
   ```

2. **服务访问**
   - 前端应用: `http://localhost`
   - 后端API: `http://localhost/api`
   - API文档: `http://localhost/api/swagger-ui.html`

### 方式二：传统部署

#### 后端部署

1. **打包应用**
   ```bash
   cd backend
   mvn clean package -DskipTests
   ```

2. **部署JAR文件**
   ```bash
   java -jar target/arkone-website-backend-1.0.0.jar
   ```

#### 前端部署

1. **构建生产版本**
   ```bash
   cd frontend
   npm run build
   ```

2. **部署到Web服务器**
   将 `dist/` 目录下的文件部署到Nginx或Apache等Web服务器

3. **Nginx配置示例**
   ```nginx
   server {
       listen 80;
       server_name your-domain.com;
       
       # 前端静态文件
       location / {
           root /path/to/frontend/dist;
           try_files $uri $uri/ /index.html;
       }
       
       # 后端API代理
       location /api {
           proxy_pass http://localhost:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ```

## 📖 API文档

启动后端服务后，可以通过以下地址访问API文档：
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 🔧 配置说明

### 后端配置
主要配置文件：`backend/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/arkone_website
    username: your_username
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

server:
  port: 8080

logging:
  level:
    com.arkone: debug
```

### 前端配置
环境配置文件：
- 开发环境: `frontend/.env.development`
- 生产环境: `frontend/.env.production`

```env
# API基础URL
VITE_API_BASE_URL=http://localhost:8080/api

# 应用标题
VITE_APP_TITLE=ArkOne Website
```

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📝 开发规范

### 后端规范
- 遵循RESTful API设计原则
- 使用统一的Result返回格式
- 添加适当的注释和文档
- 编写单元测试

### 前端规范
- 使用TypeScript进行类型检查
- 遵循Vue 3 Composition API规范
- 组件命名使用PascalCase
- 使用ESLint和Prettier保持代码风格一致

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 🆘 问题反馈

如果您在使用过程中遇到问题，请通过以下方式反馈：
- 提交 [Issue](../../issues)
- 发送邮件至：support@arkone.com

## 🎯 路线图

- [ ] 用户认证和权限管理
- [ ] 评论系统
- [ ] 文件上传功能
- [ ] 邮件通知
- [ ] 数据统计和分析
- [ ] 多语言支持
- [ ] 移动端APP

---

**感谢您使用 ArkOne Website！** 🎉