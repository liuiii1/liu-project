<h1 align="center">🌾 农产品直供平台 · Agricultural Direct Supply</h1>

<p align="center">
  <strong>基于 Spring Cloud Alibaba 微服务架构的农产品 B2C 电商平台</strong><br>
  微信小程序 · Vue3 管理后台 · Spring Cloud 后端
</p>

<p align="center">
  <a href="#技术栈">技术栈</a> ·
  <a href="#项目结构">项目结构</a> ·
  <a href="#核心功能">核心功能</a> ·
  <a href="#架构设计">架构设计</a> ·
  <a href="#快速开始">快速开始</a>
</p>

--- 

## ✨ 项目亮点

- 🏗️ **完整微服务架构**：基于 Spring Cloud Alibaba + Nacos + Gateway + OpenFeign + RabbitMQ 的五服务拆分
- ⚡ **事务式下单**：下单过程采用"先扣后回补"的事务思路，并发场景下对库存和余额进行一致性保护
- 🕒 **RabbitMQ 延迟队列**：未支付订单使用 TTL + 死信队列实现 15 分钟自动取消，配合定时任务兜底
- 🔐 **统一鉴权**：基于 Token 的轻量鉴权方案，支持普通用户、管理员、供应商三种角色隔离
- 📊 **可视化数据看板**：管理后台内置 ECharts 图表，支持订单趋势、热销商品、分类占比等多维数据展示
- 🛒 **采摘小车联动**：特色业务场景——采摘小车实时上报库存，与商城库存联动

---

## 🧱 技术栈

| 分类      | 技术                                                                                              |
| ------- | ----------------------------------------------------------------------------------------------- |
| 小程序     | 微信原生 WXML / WXSS / JS                                                                           |
| 管理后台    | Vue 3 + Vue Router + Vuex + Element Plus + ECharts + Axios                                      |
| 后端（微服务） | Spring Cloud Hoxton.SR1 · Spring Cloud Alibaba 2.2.0 · Nacos · Spring Cloud Gateway · OpenFeign |
| 消息队列    | RabbitMQ 3.x（订单超时、订单成功通知）                                                                       |
| 数据持久化   | MySQL · MyBatis-Plus                                                                            |
| 鉴权      | Token + 拦截器 · 密码加盐 MD5                                                                          |

---

## 📂 项目结构

```
NEW/
├── wechat_code/          # 微信原生小程序（用户端 / 供应商端）
│   ├── pages/            # 40+ 业务页面
│   ├── api/              # 请求封装与接口定义
│   ├── component/        # 自定义组件
│   ├── utils/            # 工具函数
│   └── app.js/json/wxss  # 全局入口
│
├── manage_code/          # Vue3 + Element Plus 管理后台
│   ├── src/
│   │   ├── views/        # 16 个业务模块视图
│   │   ├── router/       # 动态路由
│   │   ├── store/        # Vuex 状态
│   │   ├── components/   # 通用组件
│   │   └── utils/        # 请求封装
│   └── vue.config.js     # 构建配置
│
└── 后端/micro-cloud/       # Spring Cloud 微服务后端
    ├── gateway/          # API 网关 :8080
    ├── user-service/     # 用户 / 地址 / 供应商 / 配置服务 :9081
    ├── product-service/  # 商品 / 分类 / 收藏 / 公告服务 :9082
    ├── cart-service/     # 购物车服务 :9083
    ├── order-service/    # 订单 / 采摘数据服务 :9084
    ├── upload/           # 文件上传服务
    ├── common/           # 公共依赖（实体、工具、鉴权）
    └── pom.xml           # Maven 聚合父工程
```

---

## 🎯 核心功能

### 👤 微信小程序（用户端）

- 首页轮播、公告资讯、商品分类浏览
- 商品详情 / 收藏 / 评论
- 购物车管理、收货地址管理
- 下单确认、余额支付、订单列表、收货、退款申请
- 余额充值、VIP 会员购买与 VIP 专享价
- 个人中心、修改密码

### 🏭 微信小程序（供应商端）

- 供应商注册 / 登录
- 商品发布与上下架
- 订单查看与发货
- 采摘小车库存上报

### 🖥️ 管理后台

- **数据看板**：总订单、今日营收、注册用户、商品总数、待审核退款
- **ECharts 图表**：订单趋势、状态分布、热销 TOP10、分类销量占比
- **订单管理**：列表搜索、查看详情、发货、收货、退款审核、取消
- **商品管理**：农产品、分类、供应商、VIP 价格、库存维护
- **内容管理**：公告资讯、轮播图、评论、收藏
- **系统管理**：用户、管理员、菜单权限、系统配置
- **特色业务**：采摘数据（caidata）查询

### ⚙️ 后端关键实现

- **`POST /orders/createOrder`**：下单事务，逆序补偿库存与余额，见 [OrdersController](后端/micro-cloud/order-service/src/main/java/com/cl/controller/OrdersController.java)
- **RabbitMQ 延迟队列**：15 分钟未支付订单自动进入死信队列，由 [OrderTimeoutConsumer](后端/micro-cloud/order-service/src/main/java/com/cl/consumer/OrderTimeoutConsumer.java) 取消并回补库存
- **事件驱动**：订单创建后通过 [OrderCreatedEventListener](后端/micro-cloud/order-service/src/main/java/com/cl/listener/OrderCreatedEventListener.java) 发送 MQ 消息做日志 / 通知
- **Feign 远程调用**：各微服务间通过 OpenFeign 互相访问内部 RPC 接口
- **采摘小车上报**：`/caidata/report` 接口接收小车 JSON 报文，按商品 + 供应商增加库存并写入操作日志

---

## 🏗️ 架构设计

```
  小程序 / 管理后台
         │
         ▼
  Spring Cloud Gateway :8080
  ├─ StripPrefix=1 路由
  ├─ /rpc/** 外部禁止访问
         │
    ┌────┼─────────────┬────────────┐
    ▼    ▼             ▼            ▼
 :9081  :9082        :9083        :9084
 user   product     cart         order
 service service  service     service
    │    │             │            │
    └────┴──────┬──────┴────────────┘
                 ▼
             MySQL db_project
                 ▲
        ┌────────┴────────┐
        ▼                 ▼
      Nacos            RabbitMQ
   (服务注册发现)     (延迟队列 / 事件)
```

---

## 🗄️ 数据库模型

核心表共 14 张 + 1 张业务日志表：

| 表名                                      | 说明                  |
| --------------------------------------- | ------------------- |
| `user`                                  | 普通用户（含余额、VIP 标识）    |
| `users`                                 | 管理员                 |
| `supplier`                              | 供应商                 |
| `address`                               | 收货地址                |
| `agriculturalproductinformation`        | 农产品信息（含价格、VIP 价、库存） |
| `productcategory`                       | 商品分类                |
| `cart`                                  | 购物车                 |
| `orders`                                | 订单主表                |
| `discussagriculturalproductinformation` | 商品评论                |
| `storeup`                               | 收藏                  |
| `news`                                  | 公告资讯                |
| `config`                                | 系统配置                |
| `menu`                                  | 菜单与权限               |
| `caidata`                               | 采摘小车操作日志            |

数据库脚本位于 `后端/server_code/sql/`：

> 💡 建库脚本放在了 `server_code/sql/` 目录下，是仓库里通用的初始化 SQL，微服务模式同样使用。

- `db_project.sql` — 建库建表与基础数据
- `migrate_vip_and_features.sql` — VIP 字段迁移

---

## 🚀 快速开始

### 环境要求

| 组件       | 版本   |
| -------- | ---- |
| JDK      | 1.8  |
| Maven    | 3.6+ |
| Node.js  | 14+  |
| MySQL    | 5.7+ |
| Nacos    | 2.x  |
| RabbitMQ | 3.x  |

### 启动步骤

```bash
# 0. 导入数据库（首次部署必做）
mysql -u root -p < 后端/server_code/sql/db_project.sql
mysql -u root -p < 后端/server_code/sql/migrate_vip_and_features.sql
mysql -u root -p < 后端/server_code/sql/migrate_password.sql

# 1. 启动 Nacos
sh startup.sh -m standalone    # 或 Windows: startup.cmd -m standalone

# 2. 启动 RabbitMQ（需启用插件 rabbitmq_delayed_message_exchange）
rabbitmq-server -detached

# 3. 修改各服务 bootstrap.yml 中的 Nacos / RabbitMQ / MySQL 地址

# 4. 按顺序启动（推荐 IDE 逐个 Run，或 mvn spring-boot:run）
#    ① gateway            :8080
#    ② user-service       :9081
#    ③ product-service    :9082
#    ④ cart-service       :9083
#    ⑤ order-service      :9084

# 5. 启动管理后台
cd manage_code
npm install
npm run dev
# 管理后台访问: http://localhost:8081

# 6. 启动微信小程序
# 用微信开发者工具打开 wechat_code 目录
# 修改 api/request.js 中的 baseURL 为网关地址 http://localhost:8080/project
```

### 默认账号

| 角色   | 账号    | 密码                   |
| ---- | ----- | -------------------- |
| 管理员  | admin | 123456（或根据迁移脚本加密后的值） |
| 普通用户 | —     | 自行注册                 |
| 供应商  | —     | 自行注册后登录              |

---

## 📝 部署说明

### 管理后台打包

```bash
cd manage_code
npm run build:prod
# 产物: dist/，丢到 Nginx / 静态服务器即可
```

### 微服务打包

```bash
cd 后端/micro-cloud
mvn clean package -DskipTests
# 各服务生成独立 jar，可分别部署
```
