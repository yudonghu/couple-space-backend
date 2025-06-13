# 情侣空间 - 前端网页

这是情侣空间项目的网页前端，采用原生HTML + CSS + JavaScript开发。

## 功能特色

- 🎨 现代化渐变设计
- 📱 响应式布局，支持手机和电脑
- 🎯 多页面导航结构
- 📊 实时数据展示
- 💫 流畅的动画效果

## 部署方法

### 使用Nginx部署

1. 将index.html文件上传到服务器
2. 配置Nginx指向该文件
3. 设置API代理到后端服务

详细部署步骤请参考项目根目录的README.md文件。

## 技术栈

- HTML5
- CSS3 (Flexbox + Grid)
- JavaScript (ES6+)
- Nginx (Web服务器)

## API接口

前端通过相对路径 `/api` 调用后端接口，由Nginx代理到Spring Boot服务（8080端口）。
