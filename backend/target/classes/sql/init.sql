-- ArkOne数据库初始化脚本

CREATE DATABASE IF NOT EXISTS arkone DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE arkone;

-- 文章表
CREATE TABLE IF NOT EXISTS articles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(255) NOT NULL COMMENT '文章标题',
    content LONGTEXT COMMENT '文章内容',
    summary TEXT COMMENT '文章摘要',
    cover_image VARCHAR(500) COMMENT '封面图片URL',
    source_type ENUM('wechat', 'manual') DEFAULT 'manual' COMMENT '来源类型',
    source_url VARCHAR(500) COMMENT '原文链接',
    tags JSON COMMENT '标签列表',
    category_id BIGINT COMMENT '分类ID',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    status ENUM('draft', 'published', 'archived') DEFAULT 'draft' COMMENT '状态',
    is_top BOOLEAN DEFAULT FALSE COMMENT '是否置顶',
    publish_time TIMESTAMP NULL COMMENT '发布时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_status (status),
    INDEX idx_category (category_id),
    INDEX idx_publish_time (publish_time),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- AI新闻表
CREATE TABLE IF NOT EXISTS ai_news (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(255) NOT NULL COMMENT '新闻标题',
    content LONGTEXT COMMENT '新闻内容',
    summary TEXT COMMENT '新闻摘要',
    source VARCHAR(100) COMMENT '新闻来源',
    source_url VARCHAR(500) COMMENT '原文链接',
    category VARCHAR(50) COMMENT '新闻分类',
    tags JSON COMMENT '标签列表',
    cover_image VARCHAR(500) COMMENT '封面图片',
    is_hot BOOLEAN DEFAULT FALSE COMMENT '是否热门',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    status ENUM('draft', 'published', 'archived') DEFAULT 'published' COMMENT '状态',
    published_at TIMESTAMP NULL COMMENT '发布时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_is_hot (is_hot),
    INDEX idx_published_at (published_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI新闻表';

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    type ENUM('article', 'news') NOT NULL COMMENT '分类类型',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    UNIQUE KEY uk_name_type (name, type),
    INDEX idx_type (type),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(100) COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像URL',
    role ENUM('admin', 'user') DEFAULT 'user' COMMENT '角色',
    status ENUM('active', 'inactive', 'banned') DEFAULT 'active' COMMENT '状态',
    last_login_time TIMESTAMP NULL COMMENT '最后登录时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 评论表
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    content TEXT NOT NULL COMMENT '评论内容',
    target_type ENUM('article', 'news') NOT NULL COMMENT '评论目标类型',
    target_id BIGINT NOT NULL COMMENT '评论目标ID',
    user_id BIGINT COMMENT '用户ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父评论ID',
    reply_to_user_id BIGINT COMMENT '回复用户ID',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending' COMMENT '审核状态',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_target (target_type, target_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type ENUM('string', 'number', 'boolean', 'json') DEFAULT 'string' COMMENT '配置类型',
    description VARCHAR(255) COMMENT '配置描述',
    is_system BOOLEAN DEFAULT FALSE COMMENT '是否系统配置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 文件上传记录表
CREATE TABLE IF NOT EXISTS upload_files (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_name VARCHAR(255) NOT NULL COMMENT '存储文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size BIGINT NOT NULL COMMENT '文件大小',
    file_type VARCHAR(100) COMMENT '文件类型',
    mime_type VARCHAR(100) COMMENT 'MIME类型',
    upload_user_id BIGINT COMMENT '上传用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_upload_user (upload_user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件上传记录表';

-- 插入默认数据

-- 默认管理员用户
INSERT INTO users (username, email, password, nickname, role, status) VALUES 
('admin', 'admin@arkone.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLO.aqd0Oqb6', '管理员', 'admin', 'active');

-- 默认分类
INSERT INTO categories (name, description, type, sort_order) VALUES 
('技术分享', '技术相关的文章分享', 'article', 1),
('生活随笔', '日常生活的感悟和记录', 'article', 2),
('AI资讯', 'AI领域的最新资讯', 'news', 1),
('行业动态', '科技行业的发展动态', 'news', 2);

-- 系统配置
INSERT INTO system_config (config_key, config_value, config_type, description, is_system) VALUES 
('site_name', 'ArkOne', 'string', '网站名称', TRUE),
('site_description', 'ArkOne个人网站 - 技术分享与AI资讯', 'string', '网站描述', TRUE),
('site_keywords', 'ArkOne,个人网站,技术博客,AI资讯', 'string', '网站关键词', TRUE),
('site_logo', '/uploads/logo.png', 'string', '网站Logo', TRUE),
('site_favicon', '/uploads/favicon.ico', 'string', '网站图标', TRUE),
('comment_enabled', 'true', 'boolean', '是否开启评论', FALSE),
('comment_audit', 'true', 'boolean', '评论是否需要审核', FALSE),
('upload_max_size', '10485760', 'number', '文件上传最大大小(字节)', FALSE);

-- 示例文章
INSERT INTO articles (title, content, summary, category_id, status, publish_time) VALUES 
('欢迎来到ArkOne', '# 欢迎来到ArkOne\n\n这是我的个人网站，主要用于分享技术文章和AI相关资讯。\n\n## 网站功能\n\n- 📝 技术文章分享\n- 🤖 AI资讯聚合\n- 💬 评论互动\n- 🔍 内容搜索\n\n希望这个网站能为大家带来有价值的内容！', '欢迎来到ArkOne个人网站，这里有技术分享和AI资讯', 1, 'published', NOW());

-- 示例AI新闻
INSERT INTO ai_news (title, content, summary, source, category, status, published_at) VALUES 
('AI技术发展趋势', '# AI技术发展趋势\n\n人工智能技术正在快速发展，从机器学习到深度学习，再到大语言模型，AI正在改变我们的生活和工作方式。\n\n## 主要趋势\n\n1. **大语言模型的普及**\n2. **多模态AI的发展**\n3. **AI在各行业的应用**\n\n未来AI将会更加智能化和人性化。', 'AI技术发展的主要趋势和未来展望', 'ArkOne', 'AI资讯', 'published', NOW());

COMMIT;