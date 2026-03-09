# 智能简历筛选系统

基于 RAG 技术的 AI 简历匹配平台

## 项目简介

智能简历筛选系统通过 AI 技术实现简历与岗位的智能匹配，帮助 HR 提高招聘效率。

### 核心功能

- 📄 简历上传与解析（支持 PDF/Word）
- 📋 岗位管理与要求配置
- 🔍 RAG 智能检索匹配
- 🤖 AI 匹配度评分与理由分析
- 📊 数据统计与可视化

## 技术栈

### 后端
- Spring Boot 3.2
- MyBatis-Plus 3.5
- Spring AI 0.8
- Chroma 向量数据库
- GLM-4（智谱AI）

### 前端
- Vue 3
- Element Plus
- ECharts

## 项目结构

```
resume-screening-system/
├── backend/           # 后端代码
├── frontend/          # 前端代码
├── docs/              # 项目文档
├── sql/               # 数据库脚本
└── data/              # 测试数据
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Docker（运行 Chroma）

### 后端启动

```bash
cd backend
mvn spring-boot:run
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

## 开发进度

- [x] 项目初始化
- [ ] 后端基础框架
- [ ] 前端基础框架
- [ ] 简历上传解析
- [ ] RAG 检索模块
- [ ] AI 匹配模块
- [ ] 数据统计模块

## 作者

M4 AI Assistant & 山哥

## 许可证

MIT
