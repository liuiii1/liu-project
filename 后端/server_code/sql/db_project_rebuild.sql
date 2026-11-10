-- ============================================================================
-- db_project 数据库重建脚本（最终版，与 micro-cloud 微服务实体类完全对齐）
-- 适用 MySQL 5.7+ / 8.x，字符集 utf8mb4
-- 生成日期：2026-09-19
--
-- 【默认账号】（密码统一 123456，已按系统规则加盐 MD5 存储）
--   管理员：admin    / 123456   （users 表）
--   供应商：root     / 123456   （已启用采摘小车，设备号 CART001）
--   供应商：fresh    / 123456
--   普通用户：user1     / 123456 （余额 9999）
--   VIP 用户：vipuser  / 123456 （余额 9999）
--
-- 加密规则：MD5('agri_platform_2026_salt' + 明文密码 + 'agri_platform_2026_salt')
-- 123456 的密文 = 8b13837388e5d22d10dea402cae08d3f
--
-- 与原始 db_project.sql 的差异（后期迭代新增，本脚本已包含）：
--   1. user 表新增 vip 字段
--   2. agriculturalproductinformation 表新增 vipprice、number 字段
--      （number 被 decreaseStock/increaseStock 原子库存 SQL 使用，必须存在）
--   3. supplier 表新增 use_cart、device_token 字段（采摘小车绑定）
--   4. discussagriculturalproductinformation 表包含 orderid 字段
--   5. caidata 表按当前 CaidataEntity 重建：
--      account / cart_id / product_name / quantity / payload（旧版 number/action/remark 废弃）
-- ============================================================================

CREATE DATABASE IF NOT EXISTS `db_project` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `db_project`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================================
-- 1. 用户表（小程序普通用户）
--    注意：注册时 id 由应用层用时间戳赋值（全局 id-type=INPUT）
-- ============================================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `user_account`  varchar(200) NOT NULL COMMENT '用户账号',
    `user_password` varchar(200) NOT NULL COMMENT '用户密码（加盐MD5）',
    `user_name`     varchar(200) NOT NULL COMMENT '用户姓名',
    `headportrait`  longtext COMMENT '头像',
    `gender`        varchar(200)          DEFAULT NULL COMMENT '性别',
    `phone_number`  varchar(200)          DEFAULT NULL COMMENT '电话号码',
    `money`         double                DEFAULT '0' COMMENT '余额',
    `vip`           varchar(200)          DEFAULT '否' COMMENT 'VIP标识（是/否）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_account` (`user_account`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='用户';

INSERT INTO `user` (`id`, `user_account`, `user_password`, `user_name`, `headportrait`, `gender`, `phone_number`, `money`, `vip`)
VALUES (1, 'user1', '8b13837388e5d22d10dea402cae08d3f', '张三', 'file/userHeadportrait1.jpg', '男', '13800000001', 9999.00, '否'),
       (2, 'vipuser', '8b13837388e5d22d10dea402cae08d3f', '李四', 'file/userHeadportrait2.jpg', '女', '13800000002', 9999.00, '是');

-- ============================================================================
-- 2. 管理员表（管理后台）
-- ============================================================================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`
(
    `id`       bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `username` varchar(200) NOT NULL COMMENT '用户名',
    `password` varchar(200) NOT NULL COMMENT '密码（加盐MD5）',
    `role`     varchar(200)          DEFAULT NULL COMMENT '角色',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='管理员';

INSERT INTO `users` (`id`, `username`, `password`, `role`)
VALUES (1, 'admin', '8b13837388e5d22d10dea402cae08d3f', '管理员');

-- ============================================================================
-- 3. 供应商表
-- ============================================================================
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier`
(
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `account`       varchar(200) NOT NULL COMMENT '账号',
    `password`      varchar(200) NOT NULL COMMENT '密码（加盐MD5）',
    `name`          varchar(200) NOT NULL COMMENT '名称',
    `contact_phone` varchar(200)          DEFAULT NULL COMMENT '联系电话',
    `address`       varchar(200)          DEFAULT NULL COMMENT '地址',
    `use_cart`      int(11)               DEFAULT '0' COMMENT '采摘小车启用标志（1=启用，0=未启用）',
    `device_token`  varchar(200)          DEFAULT NULL COMMENT '绑定的采摘小车编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_supplier_account` (`account`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='供应商';

INSERT INTO `supplier` (`id`, `account`, `password`, `name`, `contact_phone`, `address`, `use_cart`, `device_token`)
VALUES (1, 'root', '8b13837388e5d22d10dea402cae08d3f', '绿源农场', '13900001111', '山东省寿光市农产品产业园', 1, 'CART001'),
       (2, 'fresh', '8b13837388e5d22d10dea402cae08d3f', '鲜果时光', '13900002222', '陕西省西安市周至县', 0, NULL);

-- ============================================================================
-- 4. token 表（登录态，2天过期由应用层控制，重建后为空）
-- ============================================================================
DROP TABLE IF EXISTS `token`;
CREATE TABLE `token`
(
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userid`        bigint(20)   NOT NULL COMMENT '用户id',
    `username`      varchar(100) NOT NULL COMMENT '用户名',
    `tablename`     varchar(100)          DEFAULT NULL COMMENT '表名',
    `role`          varchar(100)          DEFAULT NULL COMMENT '角色',
    `token`         varchar(200) NOT NULL COMMENT 'token',
    `addtime`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `expiratedtime` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='token表';

-- ============================================================================
-- 5. 收货地址表
-- ============================================================================
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`
(
    `id`        bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `address`   varchar(200) NOT NULL COMMENT '地址',
    `name`      varchar(200)          DEFAULT NULL COMMENT '收货人',
    `phone`     varchar(200)          DEFAULT NULL COMMENT '电话',
    `isdefault` varchar(200)          DEFAULT '否' COMMENT '是否默认地址',
    `userid`    bigint(20)   NOT NULL COMMENT '用户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='地址';

INSERT INTO `address` (`id`, `address`, `name`, `phone`, `isdefault`, `userid`)
VALUES (1, '北京市朝阳区建国路88号SOHO现代城A座1001室', '张三', '13800000001', '是', 1);

-- ============================================================================
-- 6. 产品分类表
-- ============================================================================
DROP TABLE IF EXISTS `productcategory`;
CREATE TABLE `productcategory`
(
    `id`                bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`           timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `productcategory`   varchar(200)          DEFAULT NULL COMMENT '产品分类',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='产品分类';

INSERT INTO `productcategory` (`id`, `productcategory`)
VALUES (1, '根茎类'),
       (2, '茄果类'),
       (3, '鲜果类'),
       (4, '粮油加工');

-- ============================================================================
-- 7. 农产品信息表
--    onelimittimes = 单次限购（-1不限）；alllimittimes = 库存（-1不限量）
--    number = 库存数量联动字段（decreaseStock/increaseStock 会同时更新）
-- ============================================================================
DROP TABLE IF EXISTS `agriculturalproductinformation`;
CREATE TABLE `agriculturalproductinformation`
(
    `id`                  bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`             timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `productname`         varchar(200)          DEFAULT NULL COMMENT '产品名称',
    `productcategory`     varchar(200)          DEFAULT NULL COMMENT '产品分类',
    `image`               longtext COMMENT '图片',
    `specification`       varchar(200)          DEFAULT NULL COMMENT '规格',
    `placeofproduction`   varchar(200)          DEFAULT NULL COMMENT '出产地',
    `productdetails`      longtext COMMENT '产品详情',
    `account`             varchar(200)          DEFAULT NULL COMMENT '供应商账号',
    `name`                varchar(200)          DEFAULT NULL COMMENT '供应商名称',
    `storeup_number`      int(11)               DEFAULT '0' COMMENT '收藏数',
    `discuss_number`      int(11)               DEFAULT '0' COMMENT '评论数',
    `price`               double                DEFAULT '0' COMMENT '价格',
    `vipprice`            double                DEFAULT '0' COMMENT 'VIP价格',
    `onelimittimes`       int(11)               DEFAULT '-1' COMMENT '单限（-1不限购）',
    `alllimittimes`       int(11)               DEFAULT '-1' COMMENT '库存（-1不限量）',
    `number`              int(11)               DEFAULT '0' COMMENT '库存数量（与alllimittimes联动）',
    PRIMARY KEY (`id`),
    KEY `idx_price` (`price`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='农产品信息';

INSERT INTO `agriculturalproductinformation`
(`id`, `productname`, `productcategory`, `image`, `specification`, `placeofproduction`, `productdetails`, `account`, `name`,
 `storeup_number`, `discuss_number`, `price`, `vipprice`, `onelimittimes`, `alllimittimes`, `number`)
VALUES
(1, '土豆', '根茎类', 'file/土豆.jpg', '500g/份', '山东潍坊',
 '山东潍坊新鲜土豆，现挖现发，皮薄肉厚，淀粉含量高，炖、炒、煎、炸皆宜。', 'root', '绿源农场', 0, 0, 3.50, 2.98, 20, 500, 0),
(2, '红薯', '根茎类', 'file/hongshu.png', '1kg/份', '山东烟台',
 '烟台沙地蜜薯，软糯香甜，烤熟后流蜜，富含膳食纤维，老少皆宜。', 'root', '绿源农场', 0, 0, 6.80, 5.90, -1, 300, 0),
(3, '大蒜', '根茎类', 'file/大蒜.png', '250g/份', '山东金乡',
 '金乡紫皮大蒜，蒜头饱满，辛辣味浓，厨房必备调味佳品。', 'root', '绿源农场', 0, 0, 4.20, 3.80, -1, 800, 0),
(4, '彩椒', '茄果类', 'file/彩椒.png', '500g/份', '山东寿光',
 '寿光温室彩椒，色泽鲜艳、果肉厚实，生食清甜，炒菜增色添香。', 'root', '绿源农场', 0, 0, 7.60, 6.50, -1, 200, 0),
(5, '茄子', '茄果类', 'file/茄子.png', '500g/份', '山东寿光',
 '寿光长茄，表皮紫亮、肉质细嫩，少油做法也软糯可口。', 'root', '绿源农场', 0, 0, 4.80, 4.20, -1, 260, 0),
(6, '豆角', '茄果类', 'file/豆角.png', '500g/份', '山东聊城',
 '当日采摘嫩豆角，豆粒饱满、清脆无筋，焖面、干煸俱佳。', 'root', '绿源农场', 0, 0, 6.20, 5.30, -1, 320, 0),
(7, '猕猴桃', '鲜果类', 'file/猕猴桃.png', '12枚/箱（约2斤）', '陕西周至',
 '周至绿心猕猴桃，酸甜多汁，维C含量高，放软后食用口感最佳。', 'root', '绿源农场', 0, 0, 39.90, 33.90, 10, 200, 0),
(8, '草莓', '鲜果类', 'file/caomei.png', '500g/盒', '辽宁丹东',
 '丹东九九草莓，果大香甜、入口化渣，顺丰冷链产地直发。', 'root', '绿源农场', 0, 0, 29.90, 25.40, 5, 100, 0),
(9, '圣女果', '鲜果类', 'file/圣女果.png', '500g/盒', '山东寿光',
 '玲珑圣女果，一口爆汁，酸甜开胃，洗净即食，代餐好搭档。', 'root', '绿源农场', 0, 0, 9.90, 8.40, -1, 400, 0),
(10, '红苹果', '鲜果类', 'file/红苹果.png', '5斤/箱', '山东烟台',
 '烟台红富士苹果，脆甜多汁、果香浓郁，耐储存，整箱更实惠。', 'root', '绿源农场', 0, 0, 24.90, 21.10, -1, 180, 0),
(11, '草莓果酱', '粮油加工', 'file/草莓果酱.png', '220g/瓶', '山东本地',
 '新鲜草莓慢火熬制，果肉含量高，抹面包、拌酸奶都合适。', 'root', '绿源农场', 0, 0, 28.00, 23.80, -1, -1, 0),
(12, '花生', '粮油加工', 'file/花生.png', '500g/袋', '山东临沂',
 '临沂白沙花生，颗粒饱满，可煮可炸可榨油，香脆可口。', 'root', '绿源农场', 0, 0, 12.00, 10.20, -1, 350, 0);

-- ============================================================================
-- 8. 购物车表
-- ============================================================================
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`
(
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `tablename`     varchar(200)          DEFAULT 'agriculturalproductinformation' COMMENT '商品表名',
    `goodid`        bigint(20)   NOT NULL COMMENT '商品id',
    `goodname`      varchar(200)          DEFAULT NULL COMMENT '商品名称',
    `picture`       longtext COMMENT '图片',
    `buynumber`     int(11)               DEFAULT NULL COMMENT '购买数量',
    `price`         double                DEFAULT NULL COMMENT '单价',
    `discountprice` double                DEFAULT NULL COMMENT '折扣价',
    `userid`        bigint(20)   NOT NULL COMMENT '用户id',
    `account`       varchar(200)          DEFAULT NULL COMMENT '商户名称',
    PRIMARY KEY (`id`),
    KEY `idx_cart_price` (`price`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='购物车';

-- ============================================================================
-- 9. 商品订单表（orderid 业务编号唯一；一单多商品时用 基础号-1/-2 后缀）
-- ============================================================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`
(
    `id`             bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`        timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `orderid`        varchar(200) NOT NULL COMMENT '订单编号',
    `tablename`      varchar(200)          DEFAULT 'agriculturalproductinformation' COMMENT '商品表名',
    `goodid`         bigint(20)   NOT NULL COMMENT '商品id',
    `goodname`       varchar(200)          DEFAULT NULL COMMENT '商品名称',
    `picture`        longtext COMMENT '图片',
    `buynumber`      int(11)               DEFAULT NULL COMMENT '购买数量',
    `price`          double                DEFAULT NULL COMMENT '单价',
    `discountprice`  double                DEFAULT NULL COMMENT '折扣价',
    `total`          double                DEFAULT NULL COMMENT '总价',
    `discounttotal`  double                DEFAULT NULL COMMENT '折扣总价格',
    `type`           varchar(200)          DEFAULT NULL COMMENT '支付类型（1在线支付 2货到付款 3积分）',
    `status`         varchar(200)          DEFAULT NULL COMMENT '订单状态',
    `address`        varchar(200)          DEFAULT NULL COMMENT '地址',
    `tel`            varchar(200)          DEFAULT NULL COMMENT '电话',
    `consignee`      varchar(200)          DEFAULT NULL COMMENT '收货人',
    `remark`         varchar(200)          DEFAULT NULL COMMENT '备注',
    `logistics`      longtext COMMENT '物流',
    `role`           varchar(200)          DEFAULT NULL COMMENT '用户角色',
    `userid`         bigint(20)   NOT NULL COMMENT '用户id',
    `sfsh`           varchar(200)          DEFAULT NULL COMMENT '是否审核',
    `shhf`           longtext COMMENT '回复内容',
    `account`        varchar(200)          DEFAULT NULL COMMENT '商户名称',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_orders_orderid` (`orderid`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='商品订单';

-- ============================================================================
-- 10. 农产品评论表
-- ============================================================================
DROP TABLE IF EXISTS `discussagriculturalproductinformation`;
CREATE TABLE `discussagriculturalproductinformation`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`   timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `refid`     bigint(20) NOT NULL COMMENT '关联商品id',
    `userid`    bigint(20) NOT NULL COMMENT '用户id',
    `orderid`   bigint(20)          DEFAULT NULL COMMENT '订单id（orders表主键）',
    `avatarurl` longtext COMMENT '头像',
    `nickname`  varchar(200)       DEFAULT NULL COMMENT '用户名',
    `content`   longtext   NOT NULL COMMENT '评论内容',
    `reply`     longtext COMMENT '回复内容',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='农产品评论表';

-- ============================================================================
-- 11. 收藏表（type：1收藏 21赞 22踩 41关注）
-- ============================================================================
DROP TABLE IF EXISTS `storeup`;
CREATE TABLE `storeup`
(
    `id`        bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `refid`     bigint(20)            DEFAULT NULL COMMENT '关联id',
    `tablename` varchar(200)          DEFAULT NULL COMMENT '表名',
    `name`      varchar(200) NOT NULL COMMENT '名称',
    `picture`   longtext     NOT NULL COMMENT '图片',
    `type`      varchar(200)          DEFAULT '1' COMMENT '类型(1:收藏,21:赞,22:踩,31:竞拍参与,41:关注)',
    `inteltype` varchar(200)          DEFAULT NULL COMMENT '推荐类型',
    `remark`    varchar(200)          DEFAULT NULL COMMENT '备注',
    `userid`    bigint(20)   NOT NULL COMMENT '用户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='我的收藏';

-- ============================================================================
-- 12. 采摘小车操作日志表（与 CaidataEntity 对齐）
--     接口入参 harvest 不落库；数量写入 quantity；原始报文写入 payload
-- ============================================================================
DROP TABLE IF EXISTS `caidata`;
CREATE TABLE `caidata`
(
    `id`           bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `account`      varchar(200)          DEFAULT NULL COMMENT '供应商账号',
    `cart_id`      varchar(200)          DEFAULT NULL COMMENT '小车编号',
    `product_name` varchar(200)          DEFAULT NULL COMMENT '商品名称',
    `quantity`     int(11)               DEFAULT NULL COMMENT '采摘/上报数量',
    `payload`      longtext COMMENT '原始JSON报文',
    PRIMARY KEY (`id`),
    KEY `idx_caidata_account` (`account`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='采摘小车操作日志';

-- ============================================================================
-- 13. 轮播图/站点配置表
-- ============================================================================
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config`
(
    `id`      bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `name`    varchar(200) NOT NULL COMMENT '名称',
    `value`   longtext COMMENT '值',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='轮播图';

INSERT INTO `config` (`id`, `name`, `value`)
VALUES (1, 'swiper1', 'file/swiperPicture1.jpg'),
       (2, 'swiper2', 'file/swiperPicture2.jpg'),
       (3, 'swiper3', 'file/swiperPicture3.jpg'),
       (4, 'swiper4', 'file/swiperPicture4.jpg');

-- ============================================================================
-- 14. 公告资讯表
-- ============================================================================
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`
(
    `id`           bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `title`        varchar(200) NOT NULL COMMENT '标题',
    `introduction` longtext COMMENT '简介',
    `picture`      longtext COMMENT '图片',
    `content`      longtext     NOT NULL COMMENT '内容',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='公告资讯';

INSERT INTO `news` (`id`, `title`, `introduction`, `picture`, `content`)
VALUES (1, '智慧农业商城上线公告',
        '本平台已完成微服务架构升级，为您提供更稳定的农产品购物体验。',
        'file/newsPicture1.jpg',
        '尊敬的用户：\n智慧农产品商城已正式上线，平台整合了产地直供、冷链配送、采摘小车溯源等功能，所有商品均来自经过认证的供应商基地，新鲜看得见。\n感谢您的支持！'),
       (2, '新人购物指南',
        '注册即送新人福利，下单流程一看就懂。',
        'file/newsPicture2.jpg',
        '1. 小程序端注册登录后可浏览全部农产品；\n2. 加入购物车或直接购买，支持余额支付与货到付款；\n3. 未支付订单15分钟后自动取消并释放库存；\n4. 收货后可对商品进行评价。'),
       (3, '关于VIP会员',
        '开通VIP即可享受全场商品VIP专享价。',
        'file/newsPicture3.jpg',
        'VIP会员购买带有VIP价格的商品时自动享受优惠价，优惠在下单时自动生效，无需领券。余额可在个人中心充值。');

-- ============================================================================
-- 15. 菜单表（管理后台动态菜单，JSON 由原项目备份原样恢复）
-- ============================================================================
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`
(
    `id`       bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `menujson` longtext   NOT NULL COMMENT '菜单JSON',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='菜单';

INSERT INTO `menu`
VALUES (1, '2024-12-09 01:02:13',
        '[{\"backMenu\":[{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"appFrontIcon\":\"cuIcon-similar\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"classname\":\"user\",\"menu\":\"用户\",\"menuJump\":\"列表\",\"tableName\":\"user\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"appFrontIcon\":\"cuIcon-album\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"classname\":\"users\",\"menu\":\"管理员\",\"menuJump\":\"列表\",\"tableName\":\"users\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"appFrontIcon\":\"cuIcon-paint\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"classname\":\"supplier\",\"menu\":\"供应商\",\"menuJump\":\"列表\",\"tableName\":\"supplier\"}],\"fontClass\":\"icon-user6\",\"menu\":\"用户管理\",\"unicode\":\"&#xef9c;\"},{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"appFrontIcon\":\"cuIcon-medal\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"classname\":\"productcategory\",\"menu\":\"产品分类\",\"menuJump\":\"列表\",\"tableName\":\"productcategory\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"查看评论\"],\"appFrontIcon\":\"cuIcon-addressbook\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"查看评论\"],\"classname\":\"agriculturalproductinformation\",\"menu\":\"农产品信息\",\"menuJump\":\"列表\",\"tableName\":\"agriculturalproductinformation\"}],\"fontClass\":\"icon-common33\",\"menu\":\"农产品信息管理\",\"unicode\":\"&#xee6a;\"},{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"导出\"],\"menu\":\"商品订单\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"导出\"],\"menu\":\"未支付订单\",\"menuJump\":\"未支付\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"发货\",\"物流\",\"核销\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"导出\",\"发货\"],\"menu\":\"已支付订单\",\"menuJump\":\"已支付\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"导出\"],\"menu\":\"已完成订单\",\"menuJump\":\"已完成\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"导出\"],\"menu\":\"已取消订单\",\"menuJump\":\"已取消\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"导出\"],\"menu\":\"已退款订单\",\"menuJump\":\"已退款\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"确认收货\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"导出\",\"确认收货\"],\"menu\":\"已发货订单\",\"menuJump\":\"已发货\",\"tableName\":\"orders\"}],\"fontClass\":\"icon-common34\",\"menu\":\"订单管理\",\"unicode\":\"&#xee85;\"},{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"appFrontIcon\":\"cuIcon-form\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"classname\":\"config\",\"menu\":\"轮播图\",\"menuJump\":\"列表\",\"tableName\":\"config\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"appFrontIcon\":\"cuIcon-shop\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"classname\":\"news\",\"menu\":\"公告资讯\",\"menuJump\":\"列表\",\"tableName\":\"news\"}],\"fontClass\":\"icon-common19\",\"menu\":\"系统管理\",\"unicode\":\"&#xee00;\"},{\"child\":[{\"allButtons\":[\"菜单管理\"],\"appFrontIcon\":\"cuIcon-present\",\"buttons\":[\"菜单管理\"],\"classname\":\"menu\",\"menu\":\"菜单\",\"menuJump\":\"列表\",\"tableName\":\"menu\"}],\"fontClass\":\"icon-common34\",\"menu\":\"菜单管理\",\"unicode\":\"&#xee85;\"}],\"frontMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-camera\",\"buttons\":[\"查看\"],\"classname\":\"news\",\"fontClass\":\"icon-common32\",\"menu\":\"公告资讯\",\"menuJump\":\"列表\",\"tableName\":\"news\",\"unicode\":\"&#xee66;\"}],\"fontClass\":\"icon-common32\",\"menu\":\"公告资讯\",\"unicode\":\"&#xee66;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-newshot\",\"buttons\":[\"查看\",\"查看评论\"],\"classname\":\"agriculturalproductinformation\",\"fontClass\":\"icon-sun-fill\",\"menu\":\"农产品信息\",\"menuJump\":\"列表\",\"tableName\":\"agriculturalproductinformation\",\"unicode\":\"&#xefab;\"}],\"fontClass\":\"icon-sun-fill\",\"menu\":\"农产品信息\",\"unicode\":\"&#xefab;\"}],\"hasBackLogin\":\"是\",\"hasBackRegister\":\"否\",\"hasFrontLogin\":\"否\",\"hasFrontRegister\":\"否\",\"roleName\":\"管理员\",\"tableName\":\"users\"},{\"backMenu\":[{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"appFrontIcon\":\"cuIcon-keyboard\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"classname\":\"storeup\",\"menu\":\"我的收藏\",\"menuJump\":\"1\",\"tableName\":\"storeup\"}],\"fontClass\":\"icon-common42\",\"menu\":\"我的收藏管理\",\"unicode\":\"&#xef24;\"},{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"appFrontIcon\":\"cuIcon-rank\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"classname\":\"address\",\"menu\":\"地址\",\"menuJump\":\"列表\",\"tableName\":\"address\"}],\"fontClass\":\"icon-common36\",\"menu\":\"地址管理\",\"unicode\":\"&#xee9f;\"},{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\"],\"menu\":\"商品订单\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\"],\"menu\":\"未支付订单\",\"menuJump\":\"未支付\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"发货\",\"物流\",\"核销\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\"],\"menu\":\"已支付订单\",\"menuJump\":\"已支付\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\"],\"menu\":\"已完成订单\",\"menuJump\":\"已完成\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\"],\"menu\":\"已取消订单\",\"menuJump\":\"已取消\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\"],\"menu\":\"已退款订单\",\"menuJump\":\"已退款\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"确认收货\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"确认收货\"],\"menu\":\"已发货订单\",\"menuJump\":\"已发货\",\"tableName\":\"orders\"}],\"fontClass\":\"icon-common34\",\"menu\":\"订单管理\",\"unicode\":\"&#xee85;\"}],\"frontMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-camera\",\"buttons\":[\"查看\"],\"classname\":\"news\",\"fontClass\":\"icon-common32\",\"menu\":\"公告资讯\",\"menuJump\":\"列表\",\"tableName\":\"news\",\"unicode\":\"&#xee66;\"}],\"fontClass\":\"icon-common32\",\"menu\":\"公告资讯\",\"unicode\":\"&#xee66;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-newshot\",\"buttons\":[\"查看\",\"查看评论\"],\"classname\":\"agriculturalproductinformation\",\"fontClass\":\"icon-sun-fill\",\"menu\":\"农产品信息\",\"menuJump\":\"列表\",\"tableName\":\"agriculturalproductinformation\",\"unicode\":\"&#xefab;\"}],\"fontClass\":\"icon-sun-fill\",\"menu\":\"农产品信息\",\"unicode\":\"&#xefab;\"}],\"hasBackLogin\":\"否\",\"hasBackRegister\":\"否\",\"hasFrontLogin\":\"是\",\"hasFrontRegister\":\"是\",\"roleName\":\"用户\",\"tableName\":\"user\"},{\"backMenu\":[{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"查看评论\"],\"appFrontIcon\":\"cuIcon-addressbook\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"查看评论\"],\"classname\":\"agriculturalproductinformation\",\"menu\":\"农产品信息\",\"menuJump\":\"列表\",\"tableName\":\"agriculturalproductinformation\"}],\"fontClass\":\"icon-common33\",\"menu\":\"农产品信息管理\",\"unicode\":\"&#xee6a;\"},{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"menu\":\"商品订单\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"menu\":\"未支付订单\",\"menuJump\":\"未支付\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"发货\",\"物流\",\"核销\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"发货\",\"物流\"],\"menu\":\"已支付订单\",\"menuJump\":\"已支付\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"menu\":\"已完成订单\",\"menuJump\":\"已完成\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"menu\":\"已取消订单\",\"menuJump\":\"已取消\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"menu\":\"已退款订单\",\"menuJump\":\"已退款\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"确认收货\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"menu\":\"已发货订单\",\"menuJump\":\"已发货\",\"tableName\":\"orders\"}],\"fontClass\":\"icon-common34\",\"menu\":\"订单管理\",\"unicode\":\"&#xee85;\"}],\"frontMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-camera\",\"buttons\":[\"查看\"],\"classname\":\"news\",\"fontClass\":\"icon-common32\",\"menu\":\"公告资讯\",\"menuJump\":\"列表\",\"tableName\":\"news\",\"unicode\":\"&#xee66;\"}],\"fontClass\":\"icon-common32\",\"menu\":\"公告资讯\",\"unicode\":\"&#xee66;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-newshot\",\"buttons\":[\"查看\",\"查看评论\"],\"classname\":\"agriculturalproductinformation\",\"fontClass\":\"icon-sun-fill\",\"menu\":\"农产品信息\",\"menuJump\":\"列表\",\"tableName\":\"agriculturalproductinformation\",\"unicode\":\"&#xefab;\"}],\"fontClass\":\"icon-sun-fill\",\"menu\":\"农产品信息\",\"unicode\":\"&#xefab;\"}],\"hasBackLogin\":\"是\",\"hasBackRegister\":\"是\",\"hasFrontLogin\":\"否\",\"hasFrontRegister\":\"否\",\"roleName\":\"供应商\",\"tableName\":\"supplier\"}]');


-- ============================================================================
-- 16. 给管理员、供应商菜单追加“采摘小车管理”分组（MySQL 5.7+ JSON 函数）
-- ============================================================================
UPDATE menu SET menujson = JSON_ARRAY_APPEND(
    menujson,
    '$[0].backMenu',
    CAST('{"child":[{"allButtons":["查看","删除"],"buttons":["查看","删除"],"classname":"caidata","menu":"采摘数据列表","menuJump":"列表","tableName":"caidata"}],"fontClass":"icon-common33","menu":"采摘小车管理","unicode":"&#xee6a;"}' AS JSON)
) WHERE id = 1;

UPDATE menu SET menujson = JSON_ARRAY_APPEND(
    menujson,
    '$[2].backMenu',
    CAST('{"child":[{"allButtons":["查看","删除"],"buttons":["查看","删除"],"classname":"caidata","menu":"采摘数据列表","menuJump":"列表","tableName":"caidata"}],"fontClass":"icon-common33","menu":"采摘小车管理","unicode":"&#xee6a;"}' AS JSON)
) WHERE id = 1;

-- 重建完成
SET FOREIGN_KEY_CHECKS = 1;
