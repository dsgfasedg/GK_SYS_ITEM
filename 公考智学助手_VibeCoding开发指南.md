# 公考智学助手 —— Vibe Coding 开发指南

> 本文档基于原《产品设计文档（开发实施版）》重构，调整为面向 **AI辅助开发（Vibe Coding）** 的格式。
> 每个模块都拆分为独立的 Prompt 单元，可直接作为 AI 编码助手的输入。

---

## 目录

- [一、项目概述与技术选型](#一项目概述与技术选型)
- [二、Vibe Coding 开发流程](#二vibe-coding-开发流程)
- [三、项目结构规范](#三项目结构规范)
- [四、Prompt 单元库 —— 按顺序开发](#四prompt-单元库--按顺序开发)
  - [Phase 1：项目骨架搭建](#phase-1项目骨架搭建)
  - [Phase 2：用户与会员体系](#phase-2用户与会员体系)
  - [Phase 3：学习计划模块](#phase-3学习计划模块)
  - [Phase 4：智能笔记模块](#phase-4智能笔记模块)
  - [Phase 5：错题本模块](#phase-5错题本模块)
  - [Phase 6：申论批改模块](#phase-6申论批改模块)
  - [Phase 7：个人题库与练习](#phase-7个人题库与练习)
  - [Phase 8：AI 额度管理与计费](#phase-8ai-额度管理与计费)
- [五、Prompt 编写规范](#五prompt-编写规范)
- [六、环境变量与配置清单](#六环境变量与配置清单)

---

## 一、项目概述与技术选型

### 产品定位
公考智学助手是一款**聚焦备考过程管理**的轻量级跨端工具，帮助考生实现学习计划执行、笔记体系化整理、错题复盘、申论批改、个人题库练习。

### 技术选型（已调整）

| 端 | 技术栈 | 说明 |
|----|--------|------|
| **Web 前端** | **Vue 3 + Vite + Element Plus + ECharts + Pinia + Vue Router** | 替代原设计的 React 18，组件生态更匹配功能需求 |
| **小程序端** | **微信小程序原生开发** + WeUI + 微信同声传译插件 | 拍照/语音/登录最佳原生体验 |
| **后端** | **Spring Boot 3.x + Spring Cloud Gateway + MyBatis-Plus** | 单体应用（MVP阶段） |
| **数据库** | MySQL 8.0 + Redis 7 + Elasticsearch 8 | |
| **AI 服务** | DeepSeek-V4-Flash API + 百度云OCR | |
| **消息队列** | RabbitMQ | 异步图片处理 |
| **文件存储** | 阿里云OSS | |
| **容器化** | Docker + Docker Compose | 本地开发环境一键启动 |

### 跨端分工

| 功能 | Web 端 | 小程序端 |
|------|--------|---------|
| 计划管理（日历视图/拖拽） | ✅ | ❌ |
| 计划任务执行 | ✅ | ✅ |
| 笔记编辑/知识树浏览 | ✅ | ❌ |
| 拍照/语音录入笔记 | ❌ | ✅ |
| 错题本看板/统计分析 | ✅ | ❌ |
| 拍照录入错题 | ❌ | ✅ |
| 练习模式 | ✅ | ✅ |
| 申论批改 | ✅ | ❌ |
| 题库管理/组卷导出 | ✅ | ❌ |
| 数据导出 | ✅ | ❌ |

---

## 二、Vibe Coding 开发流程

### 核心理念
每个模块按以下 4 步循环开发，每次只聚焦一个最小可运行单元：

```
┌─────────────────────────────────────────────────┐
│  STEP 1: 后端 API（Controller + Service + Mapper）│
│  STEP 2: 数据库表/迁移脚本                        │
│  STEP 3: Web 前端（Vue 页面 + 组件）              │
│  STEP 4: 小程序端（页面 + 组件，如适用）           │
└─────────────────────────────────────────────────┘
```

### 开发顺序（依赖关系图）

```
Phase 1 ──→ Phase 2 ──→ Phase 3 ──→ Phase 4 ──→ Phase 5
  │                        │            │            │
  │                        │            │            │
  └──→ Phase 8 ────────────┴──→ Phase 7 ←───────────┘
                                    │
                                    └──→ Phase 6
```

**推荐开发顺序：**

| 顺序 | Phase | 依赖 | 产出 |
|------|-------|------|------|
| 1 | **Phase 1** 项目骨架 | 无 | Spring Boot 项目 + Vue 项目 + Docker 环境 |
| 2 | **Phase 2** 用户体系 | Phase 1 | 注册/登录/JWT/微信授权 |
| 3 | **Phase 8** AI 额度管理 | Phase 2 | 额度校验中间件（所有 AI 功能依赖） |
| 4 | **Phase 3** 学习计划 | Phase 2 | 日历/拖拽/导入 |
| 5 | **Phase 4** 智能笔记 | Phase 2, Phase 8 | 知识树/OCR/AI整理 |
| 6 | **Phase 5** 错题本 | Phase 2, Phase 4(知识树), Phase 8 | 复习引擎/看板 |
| 7 | **Phase 7** 个人题库 | Phase 2, Phase 8 | 导入/练习/去重 |
| 8 | **Phase 6** 申论批改 | Phase 2, Phase 8 | 批改/历史趋势 |

---

## 三、项目结构规范

### 3.1 整体目录结构

```
gk-zhixue-assistant/
├── backend/                      # Spring Boot 后端
│   ├── src/main/java/com/gkzhixue/
│   │   ├── common/               # 通用（常量、工具、异常）
│   │   ├── config/               # 配置类
│   │   ├── module/
│   │   │   ├── user/             # 用户模块
│   │   │   ├── plan/             # 计划模块
│   │   │   ├── note/             # 笔记模块
│   │   │   ├── error/            # 错题模块
│   │   │   ├── question/         # 题库模块
│   │   │   ├── essay/            # 申论模块
│   │   │   └── ai/               # AI额度管理模块
│   │   ├── ai/                   # AI 能力层封装
│   │   └── GkZhixueApplication.java
│   ├── src/main/resources/
│   │   ├── mapper/               # MyBatis XML
│   │   ├── db/migration/         # Flyway 迁移脚本
│   │   └── application.yml
│   └── pom.xml
│
├── web/                          # Vue 3 前端
│   ├── src/
│   │   ├── api/                  # API 请求封装
│   │   ├── views/                # 页面组件
│   │   │   ├── plan/             # 计划管理
│   │   │   ├── note/             # 笔记/知识树
│   │   │   ├── error/            # 错题本
│   │   │   ├── essay/            # 申论批改
│   │   │   ├── question/         # 题库/练习
│   │   │   └── user/             # 用户/会员
│   │   ├── components/           # 通用组件
│   │   ├── stores/               # Pinia 状态管理
│   │   ├── router/               # 路由配置
│   │   ├── utils/                # 工具函数
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
│
├── miniapp/                      # 微信小程序
│   ├── pages/
│   │   ├── index/                # 首页/每日任务
│   │   ├── plan/                 # 计划执行
│   │   ├── note/                 # 笔记录入
│   │   ├── error/                # 错题录入/回顾
│   │   ├── practice/             # 练习
│   │   └── user/                 # 我的
│   ├── components/               # 通用组件
│   ├── utils/                    # 工具
│   ├── app.js / app.json / app.wxss
│   └── project.config.json
│
├── docker/
│   ├── docker-compose.yml        # MySQL + Redis + RabbitMQ + ES
│   └── sql/                      # 初始化 SQL
│
└── docs/                         # 文档
    └── 设计文档.md
```

### 3.2 后端模块分包原则

每个业务模块（如 `plan`）内部结构：

```
module/plan/
├── PlanController.java       # REST 接口
├── PlanService.java          # 业务接口
├── PlanServiceImpl.java      # 业务实现
├── PlanMapper.java           # MyBatis Mapper
├── entity/
│   ├── PlanTask.java         # 数据库实体
│   └── PlanTaskVO.java       # 前端视图对象
├── dto/
│   ├── PlanCreateDTO.java    # 创建请求
│   └── PlanUpdateDTO.java    # 更新请求
└── converter/
    └── PlanConverter.java    # 实体/DTO/VO 转换
```

### 3.3 前端页面路由（Vue 3）

```
/login                    # 登录页
/                         # 首页（重定向到计划）
/plan                     # 计划管理（日历视图）
/plan/create              # 手动创建任务
/plan/import              # 图片导入计划
/note                     # 笔记知识树
/note/create              # 新建笔记
/note/:id                 # 笔记详情/编辑
/error                    # 错题本列表
/error/dashboard          # 常错考点看板
/error/review             # 今日待复习
/essay                    # 申论批改
/essay/history            # 批改历史
/question                 # 个人题库
/question/import          # 导入题目
/practice                 # 练习模式
/practice/:sessionId      # 练习中
/practice/:sessionId/report  # 练习报告
/exam                     # 组卷
/user                     # 个人信息
/user/membership          # 会员中心
```

---

## 四、Prompt 单元库

> 每个 Prompt 单元是给 AI 编码助手的一条完整指令。
> 按顺序逐个丢给 AI 编码工具（Trae / Cursor / Windsurf 等）即可逐步构建整个项目。

---

### Phase 1：项目骨架搭建

#### Prompt 1.1 — Docker 开发环境

```markdown
# 请在项目根目录创建 docker/docker-compose.yml

创建 Docker Compose 文件，包含以下服务：

1. **MySQL 8.0**
   - 端口: 3306
   - 数据库: gk_zhixue
   - 用户: root / password
   - 挂载初始化 SQL 目录: ./sql
   - 字符集: utf8mb4

2. **Redis 7**
   - 端口: 6379
   - 密码: (空)

3. **RabbitMQ 3.12**
   - 端口: 5672 (AMQP), 15672 (管理后台)
   - 用户: guest/guest

4. **Elasticsearch 8**
   - 端口: 9200, 9300
   - 单节点模式
   - 禁用安全认证 (xpack.security.enabled=false)

同时创建 docker/sql/init.sql 作为初始化脚本，内容为空（后续用 Flyway 管理迁移）。
```

#### Prompt 1.2 — Spring Boot 后端项目

```markdown
# 在 backend/ 目录下创建 Spring Boot 3.x 项目

使用 Maven 构建，pom.xml 包含以下依赖：
- spring-boot-starter-web
- spring-boot-starter-data-redis
- spring-boot-starter-amqp (RabbitMQ)
- mybatis-plus-spring-boot3-starter (最新版)
- mysql-connector-j
- flyway-core + flyway-mysql (数据库迁移)
- spring-boot-starter-validation
- spring-boot-starter-websocket
- springdoc-openapi-starter-webmvc-ui (API文档)
- lombok
- hutool-all (工具类)
- java-jwt (或 auth0/java-jwt)
- elasticsearch-rest-client (或 spring-boot-starter-data-elasticsearch)
- aliyun-sdk-oss
- jackson-databind

启动类：com.gkzhixue.GkZhixueApplication

application.yml 配置：
- 数据源: localhost:3306/gk_zhixue
- Redis: localhost:6379
- RabbitMQ: localhost:5672
- ES: localhost:9200
- 服务端口: 8080
- 配置项全部从环境变量读取（参考下方 environment 列表）

# 同时创建以下公共基础类：

1. common/Result.java — 统一 API 响应格式 {code, message, data}
2. common/PageResult.java — 分页响应 {records, total, page, size}
3. common/BusinessException.java — 业务异常，带错误码
4. common/GlobalExceptionHandler.java — @RestControllerAdvice 全局异常处理
5. common/ErrorCode.java — 错误码枚举（1001参数错误、2001用户不存在、3001AI额度不足等）
6. config/CorsConfig.java — 跨域配置，允许 localhost:3000 和 localhost:5173
7. config/RedisConfig.java — RedisTemplate 配置（JSON序列化）
8. config/RabbitConfig.java — RabbitMQ 队列配置（ai.task.queue）
9. config/MyBatisPlusConfig.java — 分页插件
```

#### Prompt 1.3 — Vue 3 前端项目

```markdown
# 在 web/ 目录下创建 Vue 3 + Vite 项目

使用 npm create vite@latest 创建，模板选 vue。
然后安装以下依赖：

生产依赖：
- element-plus (UI 组件库)
- vue-router@4
- pinia (状态管理)
- pinia-plugin-persistedstate (持久化)
- axios (HTTP 请求)
- vue-echarts + echarts (图表)
- @tiptap/vue-3 + @tiptap/starter-kit (富文本编辑器)
- vuedraggable@next (拖拽，基于 SortableJS)
- vue-cal (日历组件，或 v-calendar)
- jsPDF + html2canvas (PDF 导出)
- @element-plus/icons-vue

开发依赖：
- @vitejs/plugin-vue
- unplugin-auto-import (自动导入)
- unplugin-vue-components (自动导入组件)

配置 vite.config.js:
- 端口: 5173
- 代理: /api → http://localhost:8080
- 自动导入 Element Plus

创建 src/main.js 入口文件，注册 Element Plus、Router、Pinia。

创建 src/App.vue 根组件，包含 <router-view />。

创建 src/router/index.js 路由配置文件（路由列表先空，后续添加）。
创建 src/stores/ 目录（后续添加 store）。
创建 src/api/request.js — axios 封装，含：
- baseURL: '/api'
- 请求拦截器：从 localStorage 取 token 添加 Authorization 头
- 响应拦截器：401 跳转登录页，统一错误提示
```

---

### Phase 2：用户与会员体系

#### Prompt 2.1 — 后端：用户表 + 注册登录 API

```markdown
# 用户模块后端开发

## Flyway 迁移脚本
在 src/main/resources/db/migration/ 创建 V1__create_users.sql：

```sql
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20),
    wechat_unionid VARCHAR(64),
    nickname VARCHAR(64),
    avatar_url VARCHAR(255),
    membership_type ENUM('free','vip') DEFAULT 'free',
    membership_expire DATETIME,
    monthly_ai_calls_used INT DEFAULT 0,
    monthly_essay_used INT DEFAULT 0,
    last_login DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_phone (phone),
    UNIQUE KEY uk_wechat (wechat_unionid)
);
```

## 创建 module/user/entity/User.java
- 对应 users 表所有字段
- 使用 @TableName("users") + @Data

## 创建 module/user/dto/ 下三个 DTO：
- LoginRequest.java: {code: String} (微信登录code)
- LoginResponse.java: {token: String, userInfo: UserVO}
- UserVO.java: 返回给前端的用户信息（脱敏，不暴露敏感字段）

## 创建 module/user/UserMapper.java
- MyBatis-Plus 的 BaseMapper<User>
- 自定义方法: selectByUnionId(String unionid)

## 创建 module/user/UserService.java + UserServiceImpl.java
业务方法：
1. wxLogin(String code) — 调用微信接口获取 unionid
   - 根据 unionid 查找用户，不存在则创建新用户
   - 生成 JWT token（user_id 存入 payload）
   - 更新 last_login
   - 返回 LoginResponse
2. getUserInfo(Long userId) — 返回 UserVO

## 创建 module/user/UserController.java
接口：
- POST /api/user/login — 微信登录 {code}
- POST /api/user/logout — 退出（客户端删除token即可）
- GET /api/user/info — 获取当前用户信息（需登录）

## JWT 工具类
创建 common/JwtUtil.java：
- generateToken(Long userId): 生成 token，过期时间 24h
- validateToken(String token): 验证并返回 userId
- 密钥从环境变量 JWT_SECRET 读取

## 登录拦截器
创建 config/AuthInterceptor.java：
- 从请求头 Authorization 取 token
- 调用 JwtUtil.validateToken
- 将 userId 放入请求属性
- 放过 /api/user/login 路径

注册到 WebMvcConfig 的 addInterceptors。
```

#### Prompt 2.2 — Web 端：登录页 + 用户信息页

```markdown
# Vue 3 前端 — 登录页 + 用户中心

## 1. 创建 src/api/user.js
```javascript
export function login(code) {
  return request.post('/user/login', { code })
}
export function getUserInfo() {
  return request.get('/user/info')
}
```

## 2. 创建 src/stores/user.js (Pinia)
```javascript
state: { token, userInfo }
actions: {
  login(code) — 调 API → 存 token 到 localStorage
  fetchUserInfo() — 调 API → 存 userInfo
  logout() — 清空 token 和 userInfo
}
getters: { isLoggedIn, isVip }
```

## 3. 创建 views/user/Login.vue
目的：用户点击登录按钮，弹出二维码/跳转微信授权
简易实现（MVP阶段）：
- 不需要真实的微信扫码，用一个输入框输入模拟 code
- 点击登录按钮调 login API
- 成功后跳转到首页 /plan
- 如果后续接真实微信登录再改
说明：MVP 阶段先模拟登录，真实微信授权后续集成

## 4. 创建 views/user/Profile.vue
展示：
- 用户头像、昵称
- 会员类型（免费/VIP）及到期时间
- 本月剩余 AI 调用次数
- "会员中心"按钮 → 跳转 /user/membership
- "退出登录"按钮

## 5. 路由配置添加到 router/index.js
- /login → Login.vue (不需要登录)
- /user → Profile.vue (需要登录)
- 添加路由守卫 router.beforeEach：未登录且目标需登录则跳转 /login
```

#### Prompt 2.3 — 小程序端：微信登录 + 我的页面

```markdown
# 微信小程序 — 登录 + 用户页面

## 1. 微信登录流程
在 miniapp/pages/index/ 首页或启动时：

步骤：
1. 调用 wx.login() 获取 code
2. 调用 wx.getUserProfile() 获取用户信息（昵称、头像）
3. 将 code 发送到后端 POST /api/user/login
4. 后端返回 token，存入 wx.setStorageSync('token', token)
5. 后续请求在 header 中带 Authorization: Bearer token

## 2. 创建 utils/request.js
- 封装 wx.request
- 自动添加 Authorization header
- 401 时清除 token 并跳转登录页
- 统一错误提示 wx.showToast

## 3. 创建 pages/user/index.wxml
"我的" 页面展示：
- 用户头像、昵称（从本地存储或全局数据取）
- 会员信息（免费/VIP）
- 本月剩余 AI 次数
- 功能入口：我的错题 / 我的笔记 / 练习记录
- 退出登录按钮

## 4. 全局数据管理
在 app.js 中:
```
globalData: {
  token: '',
  userInfo: {}
}
```
登录成功后存入 globalData 和 Storage。
```

---

### Phase 3：学习计划模块

#### Prompt 3.1 — 后端：计划 CRUD + 图片导入 API

```markdown
# 计划模块后端开发

## Flyway 迁移脚本 V2__create_plan_tasks.sql
```sql
CREATE TABLE plan_tasks (
    task_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    subject VARCHAR(50),
    planned_date DATE NOT NULL,
    planned_start_time TIME,
    planned_duration INT COMMENT '分钟',
    actual_duration INT COMMENT '实际耗时（分钟）',
    status ENUM('pending','completed','delayed') DEFAULT 'pending',
    delay_count INT DEFAULT 0,
    source ENUM('manual','image_import') DEFAULT 'manual',
    image_url VARCHAR(255),
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_date (user_id, planned_date)
);
```

## 创建 module/plan/ 下的文件

### entity/PlanTask.java
对应 plan_tasks 表。

### dto/PlanCreateDTO.java
{ title, subject, plannedDate, plannedStartTime, plannedDuration, remark }

### dto/PlanUpdateDTO.java
{ taskId, title?, subject?, plannedDate?, plannedStartTime?, plannedDuration?, status?, actualDuration? }

### dto/PlanImportDTO.java
{ imageUrl: String } — 图片导入请求

### dto/PlanImportItemVO.java
{ date, startTime, title, subject } — AI 识别结果 JSON 列表项

### PlanMapper.java
自定义方法：
- selectByUserAndDateRange(userId, startDate, endDate): 按日期范围查询
- selectDailyStats(userId, date): 查询某天各科完成情况
- updateStatus(taskId, status, actualDuration)

### PlanService.java + PlanServiceImpl.java
业务方法：
1. createTask(userId, dto) — 创建任务
2. updateTask(userId, dto) — 更新任务（校验 userId 归属）
3. deleteTask(userId, taskId)
4. getTasksByDateRange(userId, startDate, endDate) — 获取日期范围内的任务
5. completeTask(userId, taskId, actualDuration) — 标记完成
6. delayTask(userId, taskId, newDate?) — 延迟任务，delayCount+1，如无 newDate 则推迟到明天
7. importFromImage(userId, dto) — 图片导入计划
   - 上传图片 → 获取 OSS URL
   - 发送到 RabbitMQ (ai.task.queue)
   - 返回任务ID供轮询结果
8. getCompletionRate(userId, date, scope: daily/weekly/monthly) — 完成率统计

### PlanController.java
- GET /api/plans?startDate=xxx&endDate=xxx — 按日期范围获取任务
- POST /api/plans — 创建任务
- PATCH /api/plans/{taskId} — 更新任务
- DELETE /api/plans/{taskId} — 删除任务
- POST /api/plans/{taskId}/complete — 完成 {actualDuration}
- POST /api/plans/{taskId}/delay — 延迟 {newDate?}
- POST /api/plans/import-image — 图片导入 {imageUrl}
- GET /api/plans/stats?date=xxx&scope=daily — 完成率统计
```

#### Prompt 3.2 — Web 端：计划管理页面（日历 + 拖拽）

```markdown
# Vue 3 前端 — 计划管理页面

## 1. 创建 src/api/plan.js
封装所有计划相关 API 请求。

## 2. 创建 src/stores/plan.js (Pinia)
state: { tasks: [], currentDate: Date, viewType: 'month' }
actions: {
  fetchTasks(startDate, endDate),
  createTask(data),
  updateTask(taskId, data),
  deleteTask(taskId),
  completeTask(taskId, actualDuration),
  delayTask(taskId, newDate),
  uploadImage(file) — 上传 OSS
}

## 3. 创建 views/plan/PlanCalendar.vue （主页面）
使用 vue-cal 或 v-calendar 组件实现日/周/月视图。

要求：
- **工具栏**：日期切换（上一天/下一天、今日）、视图切换（日/周/月）、"新建任务"按钮、"导入图片"按钮
- **日历区域**：每个日期格内显示任务卡片列表
- **任务卡片**：显示标题、时间、科目标签（颜色区分）、状态标识（待办/已完成/已延迟）
- **拖拽**：用 vuedraggable 实现跨日期拖拽（拖到其他日期 = 修改 plannedDate）
- **日视图**：时间轴模式，按开始时间从上到下排列

## 4. 创建 views/plan/CreateTaskDialog.vue（弹窗）
使用 Element Plus 的 Dialog + Form：
- 标题（输入框）
- 所属科目（下拉选择：行测-言语理解/判断推理/数量关系/资料分析/常识判断、申论）
- 日期（日期选择器）
- 开始时间（时间选择器）
- 预计时长（数字输入框，分钟）
- 备注（文本域）
- 确定/取消按钮

## 5. 创建 views/plan/ImportImageDialog.vue（弹窗）
- 上传区域（Element Plus Upload 组件，拖拽或点击上传）
- 上传后预览图片
- 点击"识别"按钮 → 调 AI → 展示识别结果列表
- 识别结果列表可编辑（每行一个任务，可改日期/时间/标题/科目）
- 确认后批量保存

## 6. 交互细节
- 点击任务卡片 → 弹出操作菜单（完成/修改/延迟/删除）
- 点击"完成" → 弹出输入实际耗时
- 点击"修改" → 弹出 CreateTaskDialog 回填数据
- 点击"延迟" → 弹出选择：稍后今日/推迟到明天/自定义日期
- 顶部显示今日完成率环形图（使用 ECharts）
```

#### Prompt 3.3 — 小程序端：每日任务执行

```markdown
# 微信小程序 — 每日任务执行页面

## 创建 pages/plan/index.wxml
今日任务列表页，功能：

1. **顶部**：日期显示 + 完成率进度条
2. **任务列表**：按时间顺序排列今日待办任务
   - 每项显示：时间段、标题、科目标签
   - 左侧 checkbox 勾选完成
   - 右滑或点击进入详情
3. **已完成任务**：灰色样式，显示实际耗时
4. **快捷操作**：底部"添加任务"按钮（调用 Web 端创建？或小程序内简易创建）

## 交互
- 勾选 checkbox → 弹出输入实际耗时 → 提交完成
- 长按任务 → 弹出菜单：修改/延迟/删除
- 延迟操作：弹窗选择"稍后今日"或"推迟到明天"

## API 对接
- GET /api/plans?startDate=today&endDate=today — 获取今日任务
- POST /api/plans/{taskId}/complete
- POST /api/plans/{taskId}/delay
```

---

### Phase 4：智能笔记模块

#### Prompt 4.1 — 后端：笔记 CRUD + AI 整理 + 知识树 API

```markdown
# 笔记模块后端开发

## Flyway 迁移脚本 V3__create_notes_and_knowledge_tree.sql
```sql
CREATE TABLE notes (
    note_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('text','voice','image') DEFAULT 'text',
    raw_content TEXT,
    formatted_content TEXT,
    tags JSON,
    source_file_url VARCHAR(255),
    knowledge_node_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    FULLTEXT INDEX ft_content (raw_content, formatted_content)
);

CREATE TABLE knowledge_tree (
    node_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT NULL,
    name VARCHAR(100) NOT NULL,
    level INT COMMENT '层级: 1学科/2模块/3考点/4要点',
    sort_order INT DEFAULT 0,
    FOREIGN KEY (parent_id) REFERENCES knowledge_tree(node_id)
);
```

## 初始化知识树数据 V4__init_knowledge_tree.sql
```sql
-- 行测
INSERT INTO knowledge_tree VALUES (1, NULL, '行测', 1, 1);
INSERT INTO knowledge_tree VALUES (2, 1, '言语理解', 2, 1);
INSERT INTO knowledge_tree VALUES (3, 2, '中心理解', 3, 1);
INSERT INTO knowledge_tree VALUES (4, 2, '逻辑填空', 3, 2);
INSERT INTO knowledge_tree VALUES (5, 1, '判断推理', 2, 2);
INSERT INTO knowledge_tree VALUES (6, 5, '逻辑判断', 3, 1);
INSERT INTO knowledge_tree VALUES (7, 5, '图形推理', 3, 2);
INSERT INTO knowledge_tree VALUES (8, 5, '定义判断', 3, 3);
INSERT INTO knowledge_tree VALUES (9, 5, '类比推理', 3, 4);
INSERT INTO knowledge_tree VALUES (10, 1, '数量关系', 2, 3);
INSERT INTO knowledge_tree VALUES (11, 10, '数学运算', 3, 1);
INSERT INTO knowledge_tree VALUES (12, 1, '资料分析', 2, 4);
INSERT INTO knowledge_tree VALUES (13, 1, '常识判断', 2, 5);
-- 申论
INSERT INTO knowledge_tree VALUES (14, NULL, '申论', 1, 2);
INSERT INTO knowledge_tree VALUES (15, 14, '归纳概括', 2, 1);
INSERT INTO knowledge_tree VALUES (16, 14, '综合分析', 2, 2);
INSERT INTO knowledge_tree VALUES (17, 14, '提出对策', 2, 3);
INSERT INTO knowledge_tree VALUES (18, 14, '贯彻执行', 2, 4);
INSERT INTO knowledge_tree VALUES (19, 14, '申发论述', 2, 5);
```

## 创建 module/note/ 下的文件

### entity/Note.java + entity/KnowledgeTree.java

### dto/NoteCreateDTO.java
{ type, rawContent?, sourceFileUrl?, tags? }

### dto/NoteUpdateDTO.java
{ formattedContent?, tags? }

### dto/AiOrganizeDTO.java
{ noteId: Long } — 触发 AI 整理的请求

### dto/KnowledgeTreeVO.java
{ nodeId, parentId, name, level, noteCount, errorCount, children: [] }

### NoteMapper.java
- selectByUserId(userId, page)
- searchByKeyword(keyword, userId) — 全文搜索
- countByKnowledgeNodeId(nodeId) — 统计某节点笔记数

### KnowledgeTreeMapper.java
- selectAll() — 获取完整树
- selectByParentId(parentId)

### NoteService.java + NoteServiceImpl.java
业务方法：
1. createNote(userId, dto)
2. updateNote(userId, noteId, dto)
3. deleteNote(userId, noteId)
4. getNoteDetail(noteId)
5. listNotes(userId, page, tag?, keyword?)
6. organizeWithAi(userId, noteId) — AI 整理
   - 查询 note 的 rawContent
   - 检查用户 AI 额度
   - 调用 DeepSeek（Prompt 见下方）
   - 更新 formattedContent 和 tags
   - 扣减额度
7. searchNotes(userId, keyword)

## DeepSeek Prompt 模板（笔记整理）
```
你是一个公考学习助手。请将以下笔记内容进行整理：
1. 分段，每段提取小标题（用关键词或首句）
2. 匹配公考知识标签（格式：学科-模块-考点，如"行测-判断推理-逻辑判断"）

笔记内容：
{rawContent}

只输出 JSON，不要额外内容：
{"formatted_text": "## 小标题1\n内容...\n## 小标题2\n内容...", "tags": ["行测-判断推理-逻辑判断"]}
```

### NoteController.java
- POST /api/notes — 创建笔记
- GET /api/notes — 列表（支持 ?tag=xxx&keyword=xxx&page=1&size=20）
- GET /api/notes/{noteId} — 详情
- PATCH /api/notes/{noteId} — 更新
- DELETE /api/notes/{noteId} — 删除
- POST /api/notes/{noteId}/organize — 触发 AI 整理
- GET /api/knowledge-tree — 获取完整知识树 (含笔记数、错题数统计)
```

#### Prompt 4.2 — Web 端：笔记知识树页面

```markdown
# Vue 3 前端 — 笔记知识树页面

## 参考 Element Plus Tree 组件文档
https://element-plus.org/zh-CN/component/tree.html

## 1. 创建 views/note/KnowledgeTree.vue（主页面）
布局：左侧知识树 + 右侧笔记列表

**左侧知识树**：
- 使用 Element Plus Tree 组件
- 懒加载（按需加载子节点）
- 节点显示：名称 + 徽标（笔记数/错题数）
- 点击节点 → 右侧显示该节点下的笔记列表

**右侧笔记列表**：
- 搜索框（关键词搜索）
- 标签筛选（下拉多选）
- 列表展示：标题（取内容前20字）、类型图标、标签标签、创建时间
- 点击 → 跳转详情

## 2. 创建 views/note/NoteDetail.vue
- 展示 formatted_content（使用 v-html 或 tiptap 只读模式）
- 展示 tags（Element Plus Tag 组件）
- 编辑按钮 → 切换到 tiptap 编辑模式
- "AI 整理"按钮 → 触发 organize API → 展示整理结果
- 删除按钮

## 3. 创建 views/note/CreateNote.vue
- 类型选择：富文本/图片
- 富文本模式：tiptap 编辑器
- 提交后创建笔记
- 创建成功跳转到笔记详情页

## 交互
- 点击"AI 整理"时，显示加载状态，整理完成后刷新页面
- 知识树节点有笔记数变化时自动刷新
```

#### Prompt 4.3 — 小程序端：拍照/语音录入笔记

```markdown
# 微信小程序 — 笔记录入

## 创建 pages/note/create.wxml
两种录入方式：

### 方式一：拍照录入
1. 点击"拍照" → wx.chooseMedia({ type: 'image' })
2. 预览图片
3. 点击"保存" → 上传 OSS + 创建笔记（type=image）
4. 创建成功后跳转到笔记列表

### 方式二：语音录入
1. 使用微信同声传译插件
2. 长按录音按钮 → 开始录音
3. 松开 → 停止录音 → 插件返回转文字结果
4. 展示转写文本，可编辑
5. 点击"保存" → 创建笔记（type=voice）

## 创建 pages/note/list.wxml
- 按时间倒序展示用户笔记列表
- 每条显示：类型图标、内容摘要、创建时间
- 点击查看详情（只读展示 formatted_content）
- 点击"AI整理"按钮触发整理（需登录态校验额度）

## 微信同声传译插件配置
在 app.json 中：
```json
"plugins": {
  "WechatSI": {
    "version": "0.3.7",
    "provider": "wx069ba97219f66d99"
  }
}
```

使用方式：
```javascript
const plugin = requirePlugin('WechatSI')
plugin.voiceToText({
  lang: 'zh_CN',
  duration: 60000,
  success: (res) => { this.setData({ text: res.result }) }
})
```
```

---

### Phase 5：错题本模块

#### Prompt 5.1 — 后端：错题 CRUD + 复习引擎 API

```markdown
# 错题模块后端开发

## Flyway 迁移脚本 V5__create_errors.sql
```sql
CREATE TABLE errors (
    error_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_id BIGINT COMMENT '关联题库题目ID，可空',
    stem TEXT NOT NULL,
    options JSON,
    correct_answer VARCHAR(10),
    user_answer VARCHAR(10),
    analysis VARCHAR(300),
    knowledge_tags JSON,
    user_error_tag VARCHAR(30) COMMENT '知识盲区/审题失误/计算错误/方法错误/时间不足/其他',
    mastery TINYINT DEFAULT 0 COMMENT '掌握度0-100',
    next_review_date DATE,
    review_count INT DEFAULT 0,
    source ENUM('photo','manual','exercise') DEFAULT 'photo',
    image_url VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_review (user_id, next_review_date)
);
```

## 创建 module/error/ 下的文件

### entity/ErrorRecord.java

### dto/ErrorCreateDTO.java
{ stem, options?, correctAnswer?, userAnswer?, knowledgeTags?, source?, imageUrl? }

### dto/ErrorReviewDTO.java
{ isCorrect: boolean } — 重做结果

### dto/ErrorStatsVO.java
{ tag: String, errorCount: int, totalCount: int, errorRate: float }

### ErrorMapper.java
- selectByUserId(userId, page)
- selectByKnowledgeTag(userId, tag)
- selectDueForReview(userId, date) — 今日应复习的错题
- selectStatsByTag(userId) — 按考点统计错误情况
- countByKnowledgeTag(tag) — 用于知识树显示错题数

### ErrorService.java + ErrorServiceImpl.java
业务方法：
1. createError(userId, dto) — 创建错题记录
2. createFromPractice(userId, questionId, userAnswer) — 练习自动收录
   - 从 questions 表复制数据做快照
3. getErrorDetail(errorId)
4. listErrors(userId, page, tag?, source?)
5. updateErrorTag(userId, errorId, userErrorTag) — 更新错因标签
6. getDailyReview(userId) — 获取今日待复习错题列表
7. submitReview(userId, errorId, isCorrect) — 提交重做结果
   - 正确: mastery + 10 (上限100)
   - 错误: mastery - 20 (下限0)
   - 更新 next_review_date (基于艾宾浩斯: 1,2,4,7,15,30天)
   - reviewCount + 1
8. getStatsByTag(userId) — 统计各考点错题数/错误率

### ErrorController.java
- POST /api/errors — 创建错题
- GET /api/errors — 列表 (?tag=xxx&source=xxx&page=1&size=20)
- GET /api/errors/{errorId} — 详情
- PATCH /api/errors/{errorId}/tag — 更新错因标签
- GET /api/errors/review/today — 今日待复习
- POST /api/errors/{errorId}/review — 提交复习结果 {isCorrect}
- GET /api/errors/stats — 常错考点统计
- POST /api/errors/export — 导出PDF（返回PDF文件流）
```

#### Prompt 5.2 — Web 端：错题本 + 常错看板

```markdown
# Vue 3 前端 — 错题本页面

## 1. 创建 views/error/ErrorList.vue
- 左侧筛选区：按知识点标签、错因、来源筛选
- 右侧列表：题干摘要、知识点标签、掌握度进度条、下次复习日期
- 点击 → 跳转详情

## 2. 创建 views/error/ErrorDetail.vue
- 展示：题干、选项、正确答案、用户答案、解析（analysis）、错因标签
- 错因标签可修改（下拉选择）
- 复习记录（显示已复习次数、掌握度变化）
- 关联的题库题目链接（如果有 question_id）

## 3. 创建 views/error/ErrorDashboard.vue（常错考点看板）
使用 ECharts 实现：
- **柱状图**：各考点错误次数 Top 10
- **雷达图**：各模块掌握度评估
- 点击柱状图的柱子 → 跳转到该考点下的错题列表

## 4. 创建 views/error/ReviewPage.vue（今日复习）
- 展示今日应复习的错题列表
- 每道题先显示题目，用户作答后显示正误判断
- 提交结果后更新掌握度并移到下一题
- 今日复习完成后展示完成统计
```

#### Prompt 5.3 — 小程序端：错题录入 + 复习

```markdown
# 微信小程序 — 错题录入与复习

## 创建 pages/error/create.wxml
拍照录入错题流程：
1. 点击"拍照" → wx.chooseMedia
2. 上传图片至 OSS
3. 调 POST /api/errors 创建错题（先传 imageUrl，其他字段由AI解析后回填）
4. 显示"AI解析中..."状态
5. 轮询或 WebSocket 接收 AI 解析完成通知
6. 解析完成后展示识别结果（题干、选项、答案、解析），用户确认
7. 用户补充错因标签
8. 保存完整错题记录

## 创建 pages/error/review.wxml
每日复习：
1. 加载 GET /api/errors/review/today
2. 逐题展示
3. 用户选择答案
4. 提交结果 POST /api/errors/{id}/review
5. 显示正误和解析
6. 滑动到下一题
7. 全部完成后展示：本次复习了 N 题，正确率 X%
```

---

### Phase 6：申论批改模块

#### Prompt 6.1 — 后端：申论批改 API

```markdown
# 申论批改模块后端开发

## Flyway 迁移脚本 V6__create_essay_corrections.sql
```sql
CREATE TABLE essay_corrections (
    correction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    essay_text TEXT NOT NULL,
    logic_score TINYINT COMMENT '逻辑结构1-10',
    content_score TINYINT COMMENT '内容要点1-10',
    language_score TINYINT COMMENT '语言表达1-10',
    standard_score TINYINT COMMENT '规范评分1-10',
    suggestions JSON COMMENT '["建议1","建议2"]',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id)
);
```

## 创建 module/essay/ 下的文件

### entity/EssayCorrection.java

### dto/EssaySubmitDTO.java
{ questionText: String, essayText: String }

### EssayMapper.java
- selectByUserId(userId, page)
- selectById(correctionId)
- countByMonth(userId, year, month)

### EssayService.java + EssayServiceImpl.java
业务方法：
1. submitCorrection(userId, dto) — 提交批改
   - 检查用户申论额度（月度额度）
   - 调用 DeepSeek（Prompt 见下方）
   - 保存结果到 essay_corrections
   - 扣减申论批改次数
   - 返回评分结果
2. getHistory(userId, page) — 历史列表
3. getDetail(correctionId) — 详情

## DeepSeek Prompt 模板（申论批改）
```
你是一名资深申论阅卷官。请对以下申论作答进行评分。

题目：
{questionText}

考生作答：
{essayText}

请从以下四个维度评分（每项1-10分）：
1. logic_score（逻辑结构：层次清晰、逻辑严谨）
2. content_score（内容要点：要点齐全、分析透彻）
3. language_score（语言表达：用词准确、语句通畅）
4. standard_score（规范评分：符合申论格式要求）

并给出2-3条简明修改建议（每条不超过30字）。

只输出 JSON，不要额外内容：
{"logic_score": 7, "content_score": 6, "language_score": 8, "standard_score": 7, "brief_suggestion": ["建议1", "建议2"]}
```

## EssayController.java
- POST /api/essay/correct — 提交批改 {questionText, essayText}
- GET /api/essay/history?page=1&size=20 — 批改历史
- GET /api/essay/{correctionId} — 批改详情
```

#### Prompt 6.2 — Web 端：申论批改页面

```markdown
# Vue 3 前端 — 申论批改页面

## 1. 创建 views/essay/EssaySubmit.vue
布局：
- **左半屏**：题干输入区（textarea）+ 作答输入区（textarea）
- **右半屏**：评分结果展示区（提交前隐藏）
- 底部"提交批改"按钮

交互：
1. 用户输入题干和作答文本
2. 点击提交 → 调 API → 显示加载
3. 结果返回后，右侧展示：
   - 四项评分（logic/content/language/standard）用 ECharts 雷达图展示
   - 每项单独评分条（Element Plus Progress 组件）
   - 修改建议列表
   - 本次消耗：申论批改额度 1 次

## 2. 创建 views/essay/EssayHistory.vue
- 列表展示历次批改记录：日期、题干摘要（前20字）、总分（四项平均）
- 点击 → 跳转详情

## 3. 创建 views/essay/EssayDetail.vue
- 展示完整题干和作答
- 评分雷达图
- 详细评分 + 建议
- 底部"金句推荐"区域（基于题目关键词匹配内置金句库，手动规则）
```

---

### Phase 7：个人题库与练习

#### Prompt 7.1 — 后端：题库 CRUD + 去重 + 练习 API

```markdown
# 题库模块后端开发

## Flyway 迁移脚本 V7__create_questions.sql
```sql
CREATE TABLE questions (
    question_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('single_choice','multi_choice') DEFAULT 'single_choice',
    stem TEXT NOT NULL,
    options JSON,
    correct_answer VARCHAR(10),
    analysis VARCHAR(300),
    knowledge_tags JSON,
    source ENUM('import','manual') DEFAULT 'import',
    import_batch_id VARCHAR(64),
    hash_value VARCHAR(64) COMMENT 'SimHash用于去重',
    status ENUM('pending_confirm','active') DEFAULT 'active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_hash (hash_value)
);

CREATE TABLE practice_sessions (
    session_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status ENUM('in_progress','completed') DEFAULT 'in_progress',
    total_count INT DEFAULT 0,
    correct_count INT DEFAULT 0,
    tag_filter VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE practice_records (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    user_answer VARCHAR(10),
    is_correct TINYINT COMMENT '0错误 1正确',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session (session_id)
);
```

## 创建 module/question/ 下的文件

### entity/Question.java, PracticeSession.java, PracticeRecord.java

### dto/QuestionImportImageDTO.java
{ imageUrl: String }

### dto/QuestionImportTextDTO.java
{ text: String } — 按格式粘贴的文本

### dto/QuestionImportConfirmDTO.java
{ questionId: Long, stem, options, correctAnswer, analysis, knowledgeTags }

### dto/PracticeStartDTO.java
{ tagFilter?, batchId?, source? } — 选题条件

### dto/PracticeSubmitDTO.java
{ questionId: Long, answer: String }

### QuestionMapper.java
- selectByUserId(userId, condition)
- selectByTag(userId, tag) — 按知识点选题
- selectUnpracticed(userId, sessionId) — 未练习的题
- findByHash(hashValue) — 用于去重

### PracticeSessionMapper.java, PracticeRecordMapper.java

### QuestionService.java + QuestionServiceImpl.java
业务方法：
1. importImage(userId, imageUrl) — 图片导入（异步，返回 batchId）
2. importText(userId, text) — 文本导入
   - 解析格式：【题】...【A】...【B】...【C】...【D】...【答案】...
3. confirmQuestion(userId, dto) — 确认/编辑导入的题目
4. aiParse(userId, questionId) — AI 解析单题答案和解析
5. checkDuplicate(userId, stem) — SimHash去重检查，返回相似度>0.85的题目
6. listQuestions(userId, page, tag?, status?)
7. startPractice(userId, dto) — 创建练习会话
8. getNextQuestion(sessionId) — 获取下一题
9. submitAnswer(userId, sessionId, dto) — 提交答案
   - 判断对错
   - 如果错误 → 自动创建错题记录（source=exercise）
10. finishPractice(sessionId) — 结束练习
11. getPracticeReport(sessionId) — 练习报告（正确率、各知识点得分率）

## 文本解析逻辑（importText）
```
正则解析如下格式：
【题】...（题干）
【A】...（选项A）
【B】...（选项B）
【C】...（选项C）
【D】...（选项D）
【答案】A
【解析】...（可选）

返回结构化 QuestionCreateDTO 列表
```

## SimHash 去重逻辑
- 对题干文本计算 SimHash 值（64位）
- 查询数据库中 hash_value 与之的海明距离
- 距离 < 3 (相似度 > 0.85) 标记为疑似重复

### QuestionController.java
- POST /api/questions/import-image — 图片导入（返回 batchId）
- POST /api/questions/import-text — 文本导入
- GET /api/questions/import/{batchId}/items — 获取某批次的待确认题目
- POST /api/questions/{questionId}/confirm — 确认题目
- POST /api/questions/{questionId}/ai-parse — AI解析
- GET /api/questions — 列表 (?tag=xx&status=active&page=1&size=20)
- POST /api/practice/start — 开始练习
- GET /api/practice/{sessionId}/next — 下一题
- POST /api/practice/{sessionId}/submit — 提交答案
- POST /api/practice/{sessionId}/finish — 结束
- GET /api/practice/{sessionId}/report — 练习报告
```

#### Prompt 7.2 — Web 端：题库管理 + 练习

```markdown
# Vue 3 前端 — 题库与练习页面

## 1. 创建 views/question/QuestionList.vue
使用 Element Plus Table 组件：
- 表格列：题干摘要、题型、知识点标签、来源、状态、创建时间
- 操作列：编辑、删除、AI解析
- 上方筛选区：知识点下拉、状态筛选、搜索框
- 右上角按钮：导入题目、组卷

## 2. 创建 views/question/ImportDialog.vue（弹窗）
Tab 切换：
- Tab1 图片导入：上传图片 → 显示识别结果列表（可逐条编辑确认）
- Tab2 文本导入：粘贴文本 → 点击解析 → 显示解析结果列表

## 3. 创建 views/question/QuestionEdit.vue
编辑单题：题干、选项A/B/C/D、正确答案、解析、知识点标签

## 4. 创建 views/practice/PracticeSetup.vue
练习配置：
- 选题条件：按知识点、按导入批次、按未练习
- 题目数量：10/20/30/50
- 点击"开始练习" → 跳转练习页

## 5. 创建 views/practice/PracticeSession.vue
练习中：
- 顶部：进度 N/20、计时器、退出按钮
- 中部：题干 + 选项（radio button 单选 / checkbox 多选）
- 底部：提交答案按钮
- 提交后即时显示：对/错 + 正确答案 + 解析
- 点击"下一题"继续

## 6. 创建 views/practice/PracticeReport.vue
练习报告：
- 正确率大字展示
- 各知识点得分率柱状图（ECharts）
- 答错题目列表（点击可跳转错题详情）
- "再做一次"按钮

## 7. 创建 views/question/ExamPaper.vue（组卷）
- 从题库勾选题目
- 支持拖拽排序
- 预览试卷
- 导出 PDF 或在线练习
```

#### Prompt 7.3 — 小程序端：练习模式

```markdown
# 微信小程序 — 练习页

## 创建 pages/practice/list.wxml
练习入口：
- 展示可选练习条件（按知识点、按导入批次）
- 点击开始 → 创建练习会话

## 创建 pages/practice/session.wxml
练习进行中：
1. 页面顶部显示进度（3/20）
2. 题干文本区
3. 选项按钮（A/B/C/D 四个按钮，点击高亮选中）
4. 提交答案按钮
5. 提交后：
   - 对：绿色提示"回答正确！"
   - 错：红色提示，显示正确答案 + 解析
   - 自动记录错题
6. 下一题按钮（swiper 滑动切换更佳）

## 创建 pages/practice/report.wxml
练习报告：
- 正确率圆环图（使用 canvas 绘制）
- 正确/错误统计
- 错题列表（点击可查看详情）
- 返回首页按钮
```

---

### Phase 8：AI 额度管理与计费

#### Prompt 8.1 — 后端：额度管理 + 调用日志

```markdown
# AI 额度管理模块开发

## Flyway 迁移脚本 V8__create_ai_usage_logs.sql
```sql
CREATE TABLE ai_usage_logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    api_type VARCHAR(30) NOT NULL COMMENT 'plan_ocr/note_organize/error_parse/essay_correct/question_parse',
    input_tokens INT DEFAULT 0,
    output_tokens INT DEFAULT 0,
    cost DECIMAL(10,6) DEFAULT 0 COMMENT '本次调用费用',
    status VARCHAR(20) COMMENT 'success/failed',
    error_msg VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_created (created_at)
);
```

## 创建 module/ai/ 下的文件

### entity/AiUsageLog.java

### dto/AiQuotaVO.java
{ monthlyLimit: int, monthlyUsed: int, remaining: int, apiType: String }

### AiQuotaService.java
核心方法：
1. checkQuota(userId, apiType) — 检查用户是否有额度
   - 根据 membership_type 获取月度限制
   - 查询当月已用次数（从 users 表的 monthly_ai_calls_used / monthly_essay_used）
   - 不足时抛出 BusinessException(AI_QUOTA_EXCEEDED)
2. deductQuota(userId, apiType) — 扣减额度
   - 更新 users 表对应字段（+1）
3. getQuotaInfo(userId) — 获取额度详情
   - 返回各 API 类型的限制/已用/剩余

### AiService.java（统一 AI 调用入口）
代理所有 AI 请求，逻辑：
1. 调用 AiQuotaService.checkQuota()
2. 构建 Prompt，调用 DeepSeek API
3. 解析返回结果
4. 记录 AiUsageLog（含 token 消耗和费用）
5. 调用 AiQuotaService.deductQuota()
6. 返回结果

重要：所有业务模块（plan/note/error/essay/question）的 AI 调用都必须走 AiService，不能直接调 DeepSeek API。

### DeepSeekClient.java（底层 HTTP 调用）
- 使用 RestTemplate 或 WebClient 调用 DeepSeek API
- 配置：API Key、Model、Temperature(0.1)、Max Tokens(4096)
- 重试机制：失败重试 3 次
- 超时：30s
- 从环境变量读取配置

### config/AiConfig.java
- @ConfigurationProperties(prefix = "deepseek")
- 读取 all 环境变量（DEEPSEEK_API_KEY, DEEPSEEK_BASE_URL 等）

### AiController.java
- GET /api/ai/quota — 获取当前用户额度信息
- GET /api/ai/usage-logs?page=1&size=20 — 调用记录（仅管理员或用户本人）

## 额度规则配置（写在 AiQuotaService 常量或数据库配置表）
| apiType          | 免费版月限 | VIP月限 |
|------------------|-----------|---------|
| plan_ocr         | 5         | 无限    |
| note_organize    | 5         | 无限    |
| error_parse      | 10        | 无限    |
| essay_correct    | 2         | 30      |
| question_parse   | 5         | 无限    |
```

#### Prompt 8.2 — 后端：RabbitMQ 异步处理 + OCR 集成

```markdown
# 异步 AI 任务处理

## 1. RabbitMQ 配置
在 config/RabbitConfig.java 中定义：
- 队列: ai.task.queue (持久化)
- 交换机: ai.task.exchange (Direct)
- 路由键: ai.task.routing

## 2. 消息体结构 AiTaskMessage.java
```java
{
  taskId: String (UUID),
  userId: Long,
  taskType: String (plan_ocr/note_ocr/error_ocr/question_ocr),
  imageUrl: String (OSS URL),
  bizId: Long (关联的业务记录ID，如 noteId)
}
```

## 3. 消息生产者 AITaskProducer.java
- sendImageTask(userId, imageUrl, taskType, bizId) — 发送图片处理任务到队列

## 4. 消息消费者 AITaskConsumer.java
@RabbitListener(queues = "ai.task.queue")
处理方法：
1. 接收消息
2. 调用百度云 OCR 提取文字
3. 根据 taskType 调用对应的 DeepSeek Prompt
4. 更新业务记录（如更新 note 的 raw_content）
5. 通过 WebSocket 通知前端任务完成

## 5. 百度云 OCR 集成
创建 ai/OcrService.java：
- generalText(imageUrl) — 通用文字识别
- 使用百度云 OCR SDK
- 从环境变量读取 BAIDU_OCR_APP_ID / API_KEY / SECRET_KEY

## 6. 缓存机制
在 ai/AiCacheService.java 中：
- 对图片计算 MD5（基于 imageUrl 下载前几字节或由前端提供）
- 以 image_md5:taskType 为 key 存入 Redis，有效期7天
- 相同图片重复提交时直接返回缓存结果，不调用 AI

## 7. WebSocket 通知
创建 config/WebSocketConfig.java：
- 端点: /ws/notifications
- 用户连接后订阅个人频道 /user/{userId}/notifications

AI 任务完成后发送消息：
```json
{
  "type": "ai_task_complete",
  "taskType": "plan_ocr",
  "bizId": 123,
  "result": "success/failed"
}
```
```

---

## 五、Prompt 编写规范

### 给 AI 编码助手的通用规范

在项目开始时，先在 Trae/Cursor 中设置以下规则（作为项目的 `.cursorrules` 或 `.clinerules`）：

```
# 项目通用规则

## 后端 (Spring Boot)
- 使用 MyBatis-Plus，entity 字段用 @TableField 注解
- Controller 统一返回 Result<T>
- 所有分页接口使用 PageResult 包装
- 业务异常抛 BusinessException，错误码从 ErrorCode 枚举取
- 所有需要登录的接口通过请求属性获取 userId（AuthInterceptor 注入）
- 日志使用 Lombok @Slf4j
- AI 调用必须经过 AiService 代理，不直接调 DeepSeek API

## Web 前端 (Vue 3 + Element Plus)
- 组件使用 Composition API + <script setup> 语法
- 页面路由使用懒加载: () => import('@/views/xxx.vue')
- API 请求统一放在 src/api/ 目录下
- Pinia store 使用 setup 语法
- 样式使用 Element Plus 内置样式 + scoped style，不引入额外 CSS 框架
- 图标使用 @element-plus/icons-vue

## 小程序端 (微信原生)
- 使用 WeUI 组件库
- API 请求通过 utils/request.js 统一封装
- 页面数据使用 Page data 管理，复杂状态用 behaviors
- 图片上传前先压缩 (wx.compressImage)
```

### 每个 Prompt 的结构模板

```
## 需求描述
（一句话说明要做什么）

## 技术约束
（列举必须遵循的规则，如框架版本、命名规范、已有代码的约定）

## 输入/输出规范
（接口的请求响应格式、数据结构）

## 功能点列表
（逐条列出需要实现的功能，用 - 列表）

## 注意事项
（边界情况、已知坑点、需要测试的场景）
```

---

## 六、环境变量与配置清单

> 在项目根目录创建 `.env.example` 文件

```bash
# === DeepSeek ===
DEEPSEEK_API_KEY=your-api-key
DEEPSEEK_BASE_URL=https://api.deepseek.com
DEEPSEEK_MODEL_NAME=deepseek-v4-flash
DEEPSEEK_MAX_TOKENS=4096
DEEPSEEK_TEMPERATURE=0.1
DEEPSEEK_TOP_P=0.9
DEEPSEEK_RETRY_COUNT=3
DEEPSEEK_TIMEOUT_MS=30000

# === 百度云 OCR ===
BAIDU_OCR_APP_ID=your-app-id
BAIDU_OCR_API_KEY=your-api-key
BAIDU_OCR_SECRET_KEY=your-secret-key

# === 阿里云 OSS ===
ALIYUN_OSS_ACCESS_KEY_ID=your-access-key
ALIYUN_OSS_ACCESS_KEY_SECRET=your-secret-key
ALIYUN_OSS_ENDPOINT=oss-cn-hangzhou.aliyuncs.com
ALIYUN_OSS_BUCKET_NAME=your-bucket

# === 数据库 ===
DB_USERNAME=root
DB_PASSWORD=password

# === Redis ===
REDIS_PASSWORD=

# === RabbitMQ ===
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# === Elasticsearch ===
ES_USERNAME=
ES_PASSWORD=

# === JWT ===
JWT_SECRET=gk-zhixue-assistant-secret-key-2026
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# === CORS ===
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:8080

# === 文件限制 ===
MAX_FILE_SIZE=10MB
MAX_REQUEST_SIZE=50MB
```

---

## 附录：Vibe Coding 开发建议

### 推荐的开发节奏

```
第1周: Phase 1 (骨架) + Phase 2 (用户) — 跑通登录流程
第2周: Phase 8 (AI额度) + Phase 3 (计划) — 核心功能闭环
第3周: Phase 4 (笔记) — 知识树和AI整理
第4周: Phase 5 (错题) — 复习引擎
第5周: Phase 7 (题库) — 导入和练习
第6周: Phase 6 (申论) + 联调测试
```

### 每轮 Vibe Coding 的标准流程

1. **写 Prompt** — 从本文档复制对应的 Prompt 单元，按需补充细节
2. **丢给 AI** — 粘贴到 Trae/Cursor/Windsurf 的对话窗口
3. **审查代码** — AI 生成后逐文件 review，确认：
   - 接口字段和文档一致
   - 异常情况有处理
   - 安全校验（user_id 归属检查）
   - 没有硬编码
4. **测试** — 启动对应服务，用 Postman/浏览器验证
5. **提交** — `git add . && git commit -m "feat: xxx module"`

### 常见问题处理

| 问题 | 建议 |
|------|------|
| AI 生成的代码偏离需求 | 在 Prompt 中明确 "只做 X，不要做 Y" |
| AI 重复生成已有代码 | 在 Prompt 中用 "参考已有的 XxxService 模式" 引导 |
| AI 忘记加错误处理 | 在 Prompt 末尾加 "注意处理所有异常情况" |
| 前端接口对不上后端 | 先定义好 API 文档再分别生成前后端 |
| AI 额度校验没做 | 在 Prompt 强调 "所有 AI 调用必须经过 AiService" |

---

> 本文档由原《产品设计文档（开发实施版）》重构而来，
> 技术选型调整为 **Vue 3 + Element Plus（Web端）/ 微信原生（小程序端）/ Spring Boot（后端）**，
> 面向 Vibe Coding 开发模式，每个模块拆分为可直接输入的 Prompt 单元。
