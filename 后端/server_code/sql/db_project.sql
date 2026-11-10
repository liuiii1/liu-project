-- MySQL dump 10.13  Distrib 5.7.44, for Linux (x86_64)
--
-- Host: localhost    Database: project
-- ------------------------------------------------------
-- Server version	5.7.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `project`
--

/*!40000 DROP DATABASE IF EXISTS `project`*/;

CREATE
DATABASE /*!32312 IF NOT EXISTS*/ `db_project` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE
`db_project`;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `address`   varchar(200) NOT NULL COMMENT '地址',
    `name`      varchar(200)          DEFAULT NULL COMMENT '收货人',
    `phone`     varchar(200)          DEFAULT NULL COMMENT '电话',
    `isdefault` varchar(200)          DEFAULT '否' COMMENT '是否默认地址',
    `userid`    bigint(20) NOT NULL COMMENT '用户id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='地址';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK
TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address`
VALUES (1, '2024-12-09 01:02:13', '地址1', '李一', '19819881111', '是', 11),
       (2, '2024-12-09 01:02:13', '地址2', '王二', '19819882222', '是', 12),
       (3, '2024-12-09 01:02:13', '地址3', '张三', '19819883333', '是', 13),
       (4, '2024-12-09 01:02:13', '地址4', '刘四', '19819884444', '是', 14),
       (5, '2024-12-09 01:02:13', '地址5', '陈五', '19819885555', '是', 15),
       (6, '2024-12-09 01:02:13', '地址6', '杨六', '19819886666', '是', 16),
       (7, '2024-12-09 01:02:13', '地址7', '赵七', '19819887777', '是', 17),
       (8, '2024-12-09 01:02:13', '地址8', '黄八', '19819888888', '是', 18),
       (9, '2024-12-09 01:02:13', '地址9', '周九', '19819889999', '是', 19),
       (10, '2024-12-09 01:02:13', '地址10', '吴十', '19819880000', '是', 20);
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `agriculturalproductinformation`
--

DROP TABLE IF EXISTS `agriculturalproductinformation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `agriculturalproductinformation`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`           timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `productname`       varchar(200)       DEFAULT NULL COMMENT '产品名称',
    `productcategory`   varchar(200)       DEFAULT NULL COMMENT '产品分类',
    `image`             longtext COMMENT '图片',
    `specification`     varchar(200)       DEFAULT NULL COMMENT '规格',
    `placeofproduction` varchar(200)       DEFAULT NULL COMMENT '出产地',
    `productdetails`    longtext COMMENT '产品详情',
    `account`           varchar(200)       DEFAULT NULL COMMENT '账号',
    `name`              varchar(200)       DEFAULT NULL COMMENT '名称',
    `storeup_number`    int(11) DEFAULT '0' COMMENT '收藏数',
    `discuss_number`    int(11) DEFAULT '0' COMMENT '评论数',
    `price`             double             DEFAULT '0' COMMENT '价格',
    `onelimittimes`     int(11) DEFAULT '-1' COMMENT '单限',
    `alllimittimes`     int(11) DEFAULT '-1' COMMENT '库存',
    PRIMARY KEY (`id`),
    KEY                 `agriculturalproductinformation_price` (`price`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='农产品信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agriculturalproductinformation`
--

LOCK
TABLES `agriculturalproductinformation` WRITE;
/*!40000 ALTER TABLE `agriculturalproductinformation` DISABLE KEYS */;
INSERT INTO `agriculturalproductinformation`
VALUES (1, '2024-12-09 01:02:13', '产品名称1', '产品分类1',
        'file/agriculturalproductinformationImage1.jpg,file/agriculturalproductinformationImage2.jpg,file/agriculturalproductinformationImage3.jpg',
        '规格1', '出产地1', '产品详情1', '账号1', '名称1', 1, 1, 99.9, 1, 99),
       (2, '2024-12-09 01:02:13', '产品名称2', '产品分类2',
        'file/agriculturalproductinformationImage2.jpg,file/agriculturalproductinformationImage3.jpg,file/agriculturalproductinformationImage4.jpg',
        '规格2', '出产地2', '产品详情2', '账号2', '名称2', 2, 2, 99.9, 2, 99),
       (3, '2024-12-09 01:02:13', '产品名称3', '产品分类3',
        'file/agriculturalproductinformationImage3.jpg,file/agriculturalproductinformationImage4.jpg,file/agriculturalproductinformationImage5.jpg',
        '规格3', '出产地3', '产品详情3', '账号3', '名称3', 3, 3, 99.9, 3, 99),
       (4, '2024-12-09 01:02:13', '产品名称4', '产品分类4',
        'file/agriculturalproductinformationImage4.jpg,file/agriculturalproductinformationImage5.jpg,file/agriculturalproductinformationImage6.jpg',
        '规格4', '出产地4', '产品详情4', '账号4', '名称4', 4, 4, 99.9, 4, 99),
       (5, '2024-12-09 01:02:13', '产品名称5', '产品分类5',
        'file/agriculturalproductinformationImage5.jpg,file/agriculturalproductinformationImage6.jpg,file/agriculturalproductinformationImage7.jpg',
        '规格5', '出产地5', '产品详情5', '账号5', '名称5', 5, 5, 99.9, 5, 99),
       (6, '2024-12-09 01:02:13', '产品名称6', '产品分类6',
        'file/agriculturalproductinformationImage6.jpg,file/agriculturalproductinformationImage7.jpg,file/agriculturalproductinformationImage8.jpg',
        '规格6', '出产地6', '产品详情6', '账号6', '名称6', 6, 6, 99.9, 6, 99),
       (7, '2024-12-09 01:02:13', '产品名称7', '产品分类7',
        'file/agriculturalproductinformationImage7.jpg,file/agriculturalproductinformationImage8.jpg,file/agriculturalproductinformationImage9.jpg',
        '规格7', '出产地7', '产品详情7', '账号7', '名称7', 7, 7, 99.9, 7, 99),
       (8, '2024-12-09 01:02:13', '产品名称8', '产品分类8',
        'file/agriculturalproductinformationImage8.jpg,file/agriculturalproductinformationImage9.jpg,file/agriculturalproductinformationImage10.jpg',
        '规格8', '出产地8', '产品详情8', '账号8', '名称8', 8, 8, 99.9, 8, 99),
       (9, '2024-12-09 01:02:13', '产品名称9', '产品分类9',
        'file/agriculturalproductinformationImage9.jpg,file/agriculturalproductinformationImage10.jpg,file/agriculturalproductinformationImage11.jpg',
        '规格9', '出产地9', '产品详情9', '账号9', '名称9', 9, 9, 99.9, 9, 99),
       (10, '2024-12-09 01:02:13', '产品名称10', '产品分类10',
        'file/agriculturalproductinformationImage10.jpg,file/agriculturalproductinformationImage11.jpg,file/agriculturalproductinformationImage12.jpg',
        '规格10', '出产地10', '产品详情10', '账号10', '名称10', 10, 10, 99.9, 10, 99);
/*!40000 ALTER TABLE `agriculturalproductinformation` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cart`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`       timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `tablename`     varchar(200)       DEFAULT 'nongchanpinxinxi' COMMENT '商品表名',
    `goodid`        bigint(20) NOT NULL COMMENT '商品id',
    `goodname`      varchar(200)       DEFAULT NULL COMMENT '商品名称',
    `picture`       longtext  NOT NULL COMMENT '图片',
    `buynumber`     int(11) DEFAULT NULL COMMENT '购买数量',
    `price`         double             DEFAULT NULL COMMENT '单价',
    `discountprice` double             DEFAULT NULL COMMENT '折扣价',
    `userid`        bigint(20) NOT NULL COMMENT '用户id',
    `account`       varchar(200)       DEFAULT NULL COMMENT '商户名称',
    PRIMARY KEY (`id`),
    KEY             `price` (`price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='购物车';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK
TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `config`
--

DROP TABLE IF EXISTS `config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `name`    varchar(200) NOT NULL COMMENT '名称',
    `value`   longtext COMMENT '值',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='轮播图';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config`
--

LOCK
TABLES `config` WRITE;
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
INSERT INTO `config`
VALUES (1, '2024-12-09 01:02:13', 'swiper1', 'file/swiperPicture1.jpg'),
       (2, '2024-12-09 01:02:13', 'swiper2', 'file/swiperPicture2.jpg'),
       (3, '2024-12-09 01:02:13', 'swiper3', 'file/swiperPicture3.jpg');
/*!40000 ALTER TABLE `config` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `discussagriculturalproductinformation`
--

DROP TABLE IF EXISTS `discussagriculturalproductinformation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `discussagriculturalproductinformation`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`   timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `refid`     bigint(20) NOT NULL COMMENT '关联表id',
    `userid`    bigint(20) NOT NULL COMMENT '用户id',
    `orderid`   bigint(20)          DEFAULT NULL COMMENT '订单id',
    `avatarurl` longtext COMMENT '头像',
    `nickname`  varchar(200)       DEFAULT NULL COMMENT '用户名',
    `content`   longtext  NOT NULL COMMENT '评论内容',
    `reply`     longtext COMMENT '回复内容',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='agriculturalproductinformation评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discussagriculturalproductinformation`
--

LOCK
TABLES `discussagriculturalproductinformation` WRITE;
/*!40000 ALTER TABLE `discussagriculturalproductinformation` DISABLE KEYS */;
/*!40000 ALTER TABLE `discussagriculturalproductinformation` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `menu`
(
    `id`       bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `menujson` longtext  NOT NULL COMMENT '菜单',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='菜单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu`
--

LOCK
TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
INSERT INTO `menu`
VALUES (1, '2024-12-09 01:02:13',
        '[{\"backMenu\":[{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"appFrontIcon\":\"cuIcon-similar\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"classname\":\"user\",\"menu\":\"用户\",\"menuJump\":\"列表\",\"tableName\":\"user\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"appFrontIcon\":\"cuIcon-album\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"classname\":\"users\",\"menu\":\"管理员\",\"menuJump\":\"列表\",\"tableName\":\"users\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"appFrontIcon\":\"cuIcon-paint\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"classname\":\"supplier\",\"menu\":\"供应商\",\"menuJump\":\"列表\",\"tableName\":\"supplier\"}],\"fontClass\":\"icon-user6\",\"menu\":\"用户管理\",\"unicode\":\"&#xef9c;\"},{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"appFrontIcon\":\"cuIcon-medal\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"classname\":\"productcategory\",\"menu\":\"产品分类\",\"menuJump\":\"列表\",\"tableName\":\"productcategory\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"查看评论\"],\"appFrontIcon\":\"cuIcon-addressbook\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"查看评论\"],\"classname\":\"agriculturalproductinformation\",\"menu\":\"农产品信息\",\"menuJump\":\"列表\",\"tableName\":\"agriculturalproductinformation\"}],\"fontClass\":\"icon-common33\",\"menu\":\"农产品信息管理\",\"unicode\":\"&#xee6a;\"},{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"导出\"],\"menu\":\"商品订单\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"导出\"],\"menu\":\"未支付订单\",\"menuJump\":\"未支付\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"发货\",\"物流\",\"核销\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"导出\",\"发货\"],\"menu\":\"已支付订单\",\"menuJump\":\"已支付\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"导出\"],\"menu\":\"已完成订单\",\"menuJump\":\"已完成\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"导出\"],\"menu\":\"已取消订单\",\"menuJump\":\"已取消\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"导出\"],\"menu\":\"已退款订单\",\"menuJump\":\"已退款\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"确认收货\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"导出\",\"确认收货\"],\"menu\":\"已发货订单\",\"menuJump\":\"已发货\",\"tableName\":\"orders\"}],\"fontClass\":\"icon-common34\",\"menu\":\"订单管理\",\"unicode\":\"&#xee85;\"},{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"appFrontIcon\":\"cuIcon-form\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"classname\":\"config\",\"menu\":\"轮播图\",\"menuJump\":\"列表\",\"tableName\":\"config\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"appFrontIcon\":\"cuIcon-shop\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"classname\":\"news\",\"menu\":\"公告资讯\",\"menuJump\":\"列表\",\"tableName\":\"news\"}],\"fontClass\":\"icon-common19\",\"menu\":\"系统管理\",\"unicode\":\"&#xee00;\"},{\"child\":[{\"allButtons\":[\"菜单管理\"],\"appFrontIcon\":\"cuIcon-present\",\"buttons\":[\"菜单管理\"],\"classname\":\"menu\",\"menu\":\"菜单\",\"menuJump\":\"列表\",\"tableName\":\"menu\"}],\"fontClass\":\"icon-common34\",\"menu\":\"菜单管理\",\"unicode\":\"&#xee85;\"}],\"frontMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-camera\",\"buttons\":[\"查看\"],\"classname\":\"news\",\"fontClass\":\"icon-common32\",\"menu\":\"公告资讯\",\"menuJump\":\"列表\",\"tableName\":\"news\",\"unicode\":\"&#xee66;\"}],\"fontClass\":\"icon-common32\",\"menu\":\"公告资讯\",\"unicode\":\"&#xee66;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-newshot\",\"buttons\":[\"查看\",\"查看评论\"],\"classname\":\"agriculturalproductinformation\",\"fontClass\":\"icon-sun-fill\",\"menu\":\"农产品信息\",\"menuJump\":\"列表\",\"tableName\":\"agriculturalproductinformation\",\"unicode\":\"&#xefab;\"}],\"fontClass\":\"icon-sun-fill\",\"menu\":\"农产品信息\",\"unicode\":\"&#xefab;\"}],\"hasBackLogin\":\"是\",\"hasBackRegister\":\"否\",\"hasFrontLogin\":\"否\",\"hasFrontRegister\":\"否\",\"roleName\":\"管理员\",\"tableName\":\"users\"},{\"backMenu\":[{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"appFrontIcon\":\"cuIcon-keyboard\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"classname\":\"storeup\",\"menu\":\"我的收藏\",\"menuJump\":\"1\",\"tableName\":\"storeup\"}],\"fontClass\":\"icon-common42\",\"menu\":\"我的收藏管理\",\"unicode\":\"&#xef24;\"},{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"appFrontIcon\":\"cuIcon-rank\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"classname\":\"address\",\"menu\":\"地址\",\"menuJump\":\"列表\",\"tableName\":\"address\"}],\"fontClass\":\"icon-common36\",\"menu\":\"地址管理\",\"unicode\":\"&#xee9f;\"},{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\"],\"menu\":\"商品订单\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\"],\"menu\":\"未支付订单\",\"menuJump\":\"未支付\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"发货\",\"物流\",\"核销\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\"],\"menu\":\"已支付订单\",\"menuJump\":\"已支付\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\"],\"menu\":\"已完成订单\",\"menuJump\":\"已完成\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\"],\"menu\":\"已取消订单\",\"menuJump\":\"已取消\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\"],\"menu\":\"已退款订单\",\"menuJump\":\"已退款\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"确认收货\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"确认收货\"],\"menu\":\"已发货订单\",\"menuJump\":\"已发货\",\"tableName\":\"orders\"}],\"fontClass\":\"icon-common34\",\"menu\":\"订单管理\",\"unicode\":\"&#xee85;\"}],\"frontMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-camera\",\"buttons\":[\"查看\"],\"classname\":\"news\",\"fontClass\":\"icon-common32\",\"menu\":\"公告资讯\",\"menuJump\":\"列表\",\"tableName\":\"news\",\"unicode\":\"&#xee66;\"}],\"fontClass\":\"icon-common32\",\"menu\":\"公告资讯\",\"unicode\":\"&#xee66;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-newshot\",\"buttons\":[\"查看\",\"查看评论\"],\"classname\":\"agriculturalproductinformation\",\"fontClass\":\"icon-sun-fill\",\"menu\":\"农产品信息\",\"menuJump\":\"列表\",\"tableName\":\"agriculturalproductinformation\",\"unicode\":\"&#xefab;\"}],\"fontClass\":\"icon-sun-fill\",\"menu\":\"农产品信息\",\"unicode\":\"&#xefab;\"}],\"hasBackLogin\":\"否\",\"hasBackRegister\":\"否\",\"hasFrontLogin\":\"是\",\"hasFrontRegister\":\"是\",\"roleName\":\"用户\",\"tableName\":\"user\"},{\"backMenu\":[{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"查看评论\"],\"appFrontIcon\":\"cuIcon-addressbook\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"查看评论\"],\"classname\":\"agriculturalproductinformation\",\"menu\":\"农产品信息\",\"menuJump\":\"列表\",\"tableName\":\"agriculturalproductinformation\"}],\"fontClass\":\"icon-common33\",\"menu\":\"农产品信息管理\",\"unicode\":\"&#xee6a;\"},{\"child\":[{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"menu\":\"商品订单\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"menu\":\"未支付订单\",\"menuJump\":\"未支付\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"发货\",\"物流\",\"核销\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"发货\",\"物流\"],\"menu\":\"已支付订单\",\"menuJump\":\"已支付\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"menu\":\"已完成订单\",\"menuJump\":\"已完成\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"审核\",\"导出\"],\"menu\":\"已取消订单\",\"menuJump\":\"已取消\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"menu\":\"已退款订单\",\"menuJump\":\"已退款\",\"tableName\":\"orders\"},{\"allButtons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"确认收货\",\"物流\"],\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"审核\",\"导出\",\"物流\"],\"menu\":\"已发货订单\",\"menuJump\":\"已发货\",\"tableName\":\"orders\"}],\"fontClass\":\"icon-common34\",\"menu\":\"订单管理\",\"unicode\":\"&#xee85;\"}],\"frontMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-camera\",\"buttons\":[\"查看\"],\"classname\":\"news\",\"fontClass\":\"icon-common32\",\"menu\":\"公告资讯\",\"menuJump\":\"列表\",\"tableName\":\"news\",\"unicode\":\"&#xee66;\"}],\"fontClass\":\"icon-common32\",\"menu\":\"公告资讯\",\"unicode\":\"&#xee66;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-newshot\",\"buttons\":[\"查看\",\"查看评论\"],\"classname\":\"agriculturalproductinformation\",\"fontClass\":\"icon-sun-fill\",\"menu\":\"农产品信息\",\"menuJump\":\"列表\",\"tableName\":\"agriculturalproductinformation\",\"unicode\":\"&#xefab;\"}],\"fontClass\":\"icon-sun-fill\",\"menu\":\"农产品信息\",\"unicode\":\"&#xefab;\"}],\"hasBackLogin\":\"是\",\"hasBackRegister\":\"是\",\"hasFrontLogin\":\"否\",\"hasFrontRegister\":\"否\",\"roleName\":\"供应商\",\"tableName\":\"supplier\"}]');
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `news`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `title`        varchar(200) NOT NULL COMMENT '标题',
    `introduction` longtext COMMENT '简介',
    `picture`      longtext     NOT NULL COMMENT '图片',
    `content`      longtext     NOT NULL COMMENT '内容',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='公告资讯';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK
TABLES `news` WRITE;
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
INSERT INTO `news`
VALUES (1, '2024-12-09 01:02:13', '余生，愿你活出自己的精彩',
        '曾几何时，为了让自己显得合群，选择去做自己不喜欢的事;为了讨好喜欢的人，一次次地委屈自己;甚至，为了陌生人的一个眼神，就陷入了深深的自我怀疑。',
        'file/newsPicture1.jpg',
        '可是，这样小心翼翼的你，一定很累吧。你以为照顾了所有人的想法，就能得到他们的喜欢;你以为逼着自己合群，就能真的合群。直到后来才发现，在日复一日对自己的为难中，你逐渐弄丢了真实的自己.\n殊不知，人生在世，最不值得的事情，就是在别人的眼光中，迷失自己。其实，正如世界上没有十全十美的人，你也同样无法做到让每个人满意。\n世上没有不快乐的人，只有不肯让自己快乐的心。有一句话说:\n如果一段关系让你变得卑微，你可以选择抽身离去;如果一个人总是让你悲伤哭泣，你要学会放手。\n始终相信，每个人最初的样子，都是最美好的。真正喜欢你的人，会喜欢你原来的样子:真正爱你的从来不需要你的委曲求全。\n后半生，请把身上的枷锁，全部解除。去做喜欢的事，去听自己内心的声音。你会发现，这世上没有什么事情比活出自己更加幸福。\n要相信，真实的自己亦是一道风景线。你若盛开，清风自来。愿你此生尽兴，不负韶华，'),
       (2, '2024-12-09 01:02:13', '岁月无常，好好珍惜',
        '常听到这样的话，如果一切可以重新开始，我一定改掉任性，学会坚持，如果有下辈子，我一定好好珍惜，不会再弄丢原本属于自己的幸福。',
        'file/newsPicture2.jpg',
        '然而，世上有很多事情没有再来一次的机会，一旦错失就意味着无法弥补，正如人与人之间，有的只是一世的缘分，一旦转身，就意味着再无可能。\n这一生只活一次，有些人也只爱一回。\n所以，在拥有时就好好把握，对于来世，可以适当憧憬，但不要一昧去幻想，活着，唯有珍惜身边做好眼前事，才能让明天少一些遗憾。人，\n既然有幸来到这个世上，那就要努力把这辈子活好。岁月其实远没有想象中的那么漫长，若是能爱就好好爱，别让说好相伴一生的人，中途散走;若是当下能相知相守，就别去等待虚无缥缈的未来。\n就像一位网友留言说的:我从来不去期待下辈子，我要的只有今生，世事无常，来世是怎样的光景，谁都无法预知。对我来说，在有限的时光里，善待爱自己及自己爱的人，用心珍惜相聚的每一时刻，努力过好当下就足够了。\n时光匆匆，韶华似水，每个人都只有这短暂的一生。\n有些事，如果喜欢就坚定努力的去做，有些人，如果爱就真诚用心地去爱，别幻想着重来，别总是寄希望于下辈子。\n下辈子，彼此也许再也不会遇见，就算遇见了，谁还会记得谁，今生相互承诺的两个人，来生也许相见不相识。\n想起之前看过的一句话:人生没有假如，我们没有来世，下辈子，你我也许只是大千世界里互不相识的两个陌生人。\n的确，生命只有一次，今生有幸相遇的人，下辈子不一定能遇见，就算能遇见，谁能把谁陪伴，谁又是谁的谁?\n余生不长，趁着岁月未老，好好珍惜这辈子的缘分吧，别等到失去了才悔不该当初。'),
       (3, '2024-12-09 01:02:13', '与其背着烦恼活，不如带着美好过',
        '一直觉得，感情里，无疾而终并不可怕，可怕的是结束后，你还抱着回忆不肯放;岁月中，失去并不可怕，可怕的是失去后，你还揪着曾经不肯忘。\n有些事，明知道不应该继续，却始终没有转身的勇气;有些人，明知道已经成为过去，却还是舍不得放手;有时候，明知道纠缠于往事旧人只会徒增伤悲，却仍然执着的去回忆，去留恋。',
        'file/newsPicture3.jpg',
        '人生短短几十年，为什么要蹉跎浪费在无可挽回的失去上，为什么不酒脱一点，看开一点，活得轻松快乐一点。\n这世间，没有谁的人生是十全十美的，总要面对一些离别，总要经历一些失去，如果总对身边的一切执念太深，只会越活越累。\n时间不停向前，人也不可避免的要向前看。面对一些不如意、不顺心的事，该放的就放，该忘的就忘，别总把自己困在失败的悲伤中。\n生而为人，放不是无能，而是一种洒脱，忘更不是懦弱，而是一种智慧。\n在来去匆匆，聚散不定的现实里，放下不属于自己的感情，才能有新的开始，忘记心中的烦恼，才能收获愉快的心情。\n所以，有些离开的人，该放就放下吧，一直念念不忘，伤害的是自己，有些烦心事，该忘就忘了吧一直耿耿于怀，痛苦的是自己。\n-辈子本就不长，何必让自己活得那么累，过去的情就让它过去，别再留恋，想不通的事就算了，别再纠结。\n人生的旅途，总有些事让人烦，总有些情让人伤。只有学会忘记过往的不如意，忘记一些不必要的烦恼，以轻盈的身心重新出发，才不会被旧人旧事所累。\n记得，每个人心灵的空间都是有限的，当装多了纠结与伤感，就装不下自在与欢快了。要想日子过得简单轻松一点，就要懂得放下执念，忘掉痛苦，清除伤。'),
       (4, '2024-12-09 01:02:13', '理想很丰满，现实很骨感',
        '游戏里的你帅气无比，乘风御剑，通关杀敌无所不能，你满足于那其中的成就感，你满足于通关的畅快淋漓。',
        'file/newsPicture4.jpg',
        '年轻人，可那毕竟不是现实，理想很丰满，现实很骨感这不是玩笑，你不走出来，怎么会知\n你真的想等到某天喜欢上一个姑娘时，才去纠结咖啡钱电影钱你要如何与爸妈张口，才去苦恼为她买礼物你要怎样省吃俭用节衣缩食?\n你没有挥金如土的.魄力，没有殷实的家境，没有既定的光芒锦绣的前程，二十出头的年纪，你不努力，你想拿什么报答那个在你身边用最好的青春陪伴你的姑娘?你又拿什么报答含辛茹苦养育你这么多年的父母?\n生活不是游戏，不会给你那么多反复再来的机会，青春年少风华正茂，年轻人，你不努力，你想指望什么?\n年轻人，走出来奋斗吧!累也好苦也罢。如果你现在对自己各种放纵，你指望以后你用什么条件来放松?\n年轻人，你要努力赚钱，为了自己，也为了你爸妈。为了他们在以后逛商场的时候，可以给自己买东西像给你买东西一样毫不犹豫。为了他们可以在同老友谈起你时可以一脸安详，而不是想着，这个月，儿子交了房租还有钱吃饭么。我们这个年龄的人，已经像吸血鬼一样巴着父母活了二十多年了，还真的要继续吸着他们的血活下去么?\n年轻人，停止期望凡事简单容易。如果总是挑简单的事情做，那你永远成不了大器，生活总会在始料不及时考验你，所以你必须挑战困难，保持坚强，世上任何值得拥有的东西都需要付诸努力，勤奋与自律，倘若自甘平庸，那你就会变得庸碌无为。\n年轻人，与我们一起，挑战一下自己，拥抱一下梦想。你想要的生活，跳起来，一定够得到!'),
       (5, '2024-12-09 01:02:13', '理想是一种精神上的追求',
        '如果前方是一片黑暗，理想是否会黯淡?黑暗终究不能限制理想的步伐，只是需要时间。', 'file/newsPicture5.jpg',
        '是否会撕裂黑暗的结界?给理想一点时间，是否会完成理想的目标?太多时候，黑给黑暗一缕曙光，暗限制了理想的脚步，时间成了撕裂黑暗的曙光。社会的压抑，未知的迷茫，甚至是事实的打击，都成了所谓的黑暗。\n理想应该是一种精神上的追求，一种我们内心深处的渴望。也许理想的种类太多，不一样的人也有不一样的理想。但唯一的共同点就是我们在追求，追求自己精神上亦或是心中的目标。但总是不尽人意，一来存在太多不确定因素，二来时间还不够。理想并不是一而就的空话。\n想过放弃，因为理想与现实的差距。但又不想放弃，因为理想是精神上的追求心中的渴望。纠结了太多，却是否想过在纠结的时候，已经选择了错过。被刻意选择的错过是什么?是路过。我们在不经意间路过了多少理想，只是因为纠结。如果坚定自己的理想，用时间去行动，是否会少几次刻意的错过:-次，与一位朋友谈到如何乐观，然后追求理想。朋友想了一会儿，给我的回答是正能量与时间。他说，想要乐观就要不断给自己正能量。事物有阴阳两面，如果阳代表乐观，那在我们看待一件事时要看阳面千万别看阴面。这样自己不会消极，某种程度上来说就是一种正能量。想要追求理想，务必要花费时间。正如没有风平浪静的大海，想要追求理想，要时间。给理想一点时间，让自己去追求，不顾切的追求。\n点点滴滴的小事构成了我们的生活，点点滴滴的努力给理想铺平了路。每一次小成功都为理想迈进了一步。每一次失败，都告诉我们一些教训，反方向来说为下一次成功提供了经验。不要觉得失败是可怕的，长远来看，正是失败才有了成功。\n时间在流逝。如果安于现状，就会乐不思。理想需要时间不代表我们可以挥霍，我们能做的是珍惜时间，去为理想拼搏。\n间接意义上讲，理想的时间是我们的努力。\n如果正在路上，一直任性向前走，忘记时间不回头。'),
       (6, '2024-12-09 01:02:13', '人生如梦，梦想是帆',
        '人生如梦，梦想是帆，每个人都有一个只属于自己的梦，但我们同属一个国家，所以每个人的梦又与国家的兴衰荣辱紧密相连。先哲顾炎武曾说:天下兴亡，匹夫有责。只有国家好，大家才能好。',
        'file/newsPicture6.jpg',
        '我依然清楚的记得:\n当甲午战争战败，日寇无礼踏破中国的门户;当八国联军侵入北京，无情掠夺中国的财产:当七七事变发生，中国的老人、妇孺被残忍杀害的时候，我在想那时中国的梦是怎样的!\n我虽不曾亲眼看到，但那却是铁一般的事实。因为从老人们那深邃的眼神中可以感到无尽的愤懑;从他们干瘪的脸颊可以看到深情的泪水，从他们嘹亮的军歌中可以想到那奋勇杀敌时的豪迈;从他们激昂话语中听到那誓要捍卫家园振兴中华的誓言。作为新一代青年的我们难道不应该树立远大的理想，付之以踏实的行动，去继承先辈们的使命。去实现中华民族的伟大崛起和复兴吗?\n有梦才能使中国繁荣!\n在改革开放以来中国取得了一系列的可以载入中国史册的成就。香港、澳门的回归，经济特区的建立，使中国成为发展国家中的经济大国，科技先进国和军事强国。当中国成功举办奥运的时候，当神九飞天的时候，当蛟龙入海的时候，当航母下水的时候，当莫言荣获诺贝尔文学奖的时候。我相信每个人都感觉到了无比的自豪。但是现在的中国与其他发达国家还有很大差距。作为新一代的我们，难道不应该志存高远吗?\n我想有的人会说，我们的力量是有限的。的确个人的力量很渺小，但是中国梦就是因一个个微不足道的个人的梦一直汇集、汇集，然后凝聚成的一个巨大的梦。冯至在《十四行诗》中写道，我们准备着，深深领受，那些意想不到的奇迹，在漫长的岁月里，忽然有彗星的出现，狂风乍起。\n梦想是美丽的，它是美的期望;梦想是阳光的，它使人由浮躁走向踏实;梦想是充满力量的，它可以激发人身体里无限的潜能。我们期盼的是国泰民安、经济发展、政治清明、文化繁荣、社会和谐、生态良\n好、公平正义。这才是中国人伟大的梦'),
       (7, '2024-12-09 01:02:13', '若月亮没来-王宇宙Leto，乔浚丞',
        '《若月亮没来(若是月亮还没来)》是王宇宙Leto，乔浚丞演唱的歌曲，于2024年1月22日上线发行。', 'file/newsPicture7.jpg',
        '风吹过山 船靠了岸  风光呀 一点点看  我走向北 你去往南\n故事呀 一篇篇翻  好烦 又加班到很晚  你搭上空荡的 地铁已是末班\n好烦 很爱却要分开  恋爱谈不明白  好烦 接近理想好难 却又还很不甘\n如何拥抱平淡  如果 最难得到圆满  不如选择坦然 若是月亮还没来\n路灯也可照窗台  照着白色的山茶花微微开  若是晨风还没来\n晚风也可吹入怀  吹着那一地树影温柔摇摆  你总以为你不够好\n不够苗条和美貌  可是完美谁能达到  做你自己就很好\n烦恼烦恼拥有太少  没有房车没有钞票  可爱你的人永远会 \n把你当做心尖珍宝  我也懂大多数的时候  你只想逃离这世界\n我也懂太多的情绪在  一个人失眠的深夜 你可以是悲伤或者埋怨\n但请不要放弃明天  这一直灰暗的世界  我想看见你的笑脸\n若是月亮还没来  路灯也可照窗台  照着白色的山茶花微微开\n若是晨风还没来  晚风也可吹入怀  吹着那一地树影温柔摇摆\n若是月亮还没来  路灯也可照窗台  照着白色的山茶花微微开\n若是晨风还没来  晚风也可吹入怀  吹着那一地树影温柔摇摆\n李哲今年1月份创作了《若月亮没来》这首歌。随着歌曲的热度不断上升，约他写歌的人越来越多，他的工作变得更加繁忙。\n近年来，李哲凭借对音乐的热爱和不懈的努力，创作了几百首音乐作品，其中不乏广为人知的佳作，例如王栎鑫的《一个人》、何赛飞与叶炫清在央视演唱的《木兰诗》等歌曲，都是由李哲作曲。\n如今，李哲的音乐才华和努力得到了业界的广泛认可，他的音乐作品多次登上各大音乐平台的热歌榜，成为备受瞩目的新生代音乐人。现在，走在街头、坐在车里，经常会听到自己的作品。看到自己的音乐作品受到如此广泛的喜爱和认可，我感到非常有成就感。李哲笑着说，他会努力创作更多优秀音乐作品，为家乡争光。'),
       (8, '2024-12-09 01:02:13', '-米阳光传递一份温暖',
        '黑暗中跌倒，坚强里苦笑。只是因为青春还在，所以梦想一直在路上。当年华未曾老去，青春没有散场，留给幽暗的岁月，还有几分值得珍藏?孤独无助时、伤心难过时总爱恣意挥霍愤怒。伤了、痛了原本以为就都结束了;可是心里还不甘愿放弃，就只为一个理由，有梦想的人永不孤单。',
        'file/newsPicture8.jpg',
        '放进去了的青春，走不出来的悲伤却总是给年少时的我们留下太多无法割舍的情怀。记录岁月的足迹，聆听时光的故事。然轻吟浅唱出，青春嘉年华。\n故事里总会有，那些太多感慨于时间的无情，太多对往事的追忆，太多的太多，而今刻在了心里也停泻在了流年里。一段无法抹去的回忆，也只不过是一场烟火燃放的瞬间，却也一辈子在脑海里兜兜转转。很是怀念那些相遇的日子，邂逅了幸福也留在了时间轨迹里。时而想起，那些曾肩并肩奋斗的日子，那些写满感动与快乐的岁月。\n如今，我们似乎渐行渐远，彼此奋斗着青春的轮回。渐渐的，我们都长大了，时间改变了我们时刻分享彼此快乐与不快乐的习惯，青春的渐逝，岁月的更迭让我们变得都不再联系，身边的朋友也越来越少;那些欢笑，那些幸福的流年也随着我们的陌生而变得更加奢侈。也许多年以后，我们都有了自己的朋友，有了自己关心的人。那时，我们再细数那些癫狂在你生命里一辈子的记忆故事，蓦然发现一切都停留在了回忆里。'),
       (9, '2024-12-09 01:02:13', '自己的路自己走，自己的心自己懂',
        '岛上书店》中有句话:每个人的生命里，都有最艰难的那一年，将人生变得美好而辽阔。\n你有没有经历过那么一年，在走过的岁月中跋涉得异常艰难困苦，却仍无法灭前行的勇气。哪怕所有人都极力劝阻，也要不甘心地试一试;哪怕前方已没有道路，却还在坚定前行;哪怕再看不到任何希望，也要竭尽全力地为自己争取一个机会。',
        'file/newsPicture9.jpg',
        '你开始不动声色的扮演着一个大人，经历着生活日复一日的打。\n每天醒在天还没亮的五六点，挤过早高峰的地下铁，面对成山的工作，加班到深夜倒下就睡更成为了日常，还要应付各色的人际关系，逐渐开始接受努力也不一定有回报的道理。\n这段日子也许很长，但你没有放弃对生活的信心，始终相信人生总会变得一帆风顺，更坚信每件事的最后都会有一个好的结局，如果不是，说明还没到最后。\n于是你树立新的目标，让这一年的奔波更有方向。\n虽然永远无法预料明天是晴是雨，也无法预知你在乎的人是否还在身边，你一直以来的坚持究竟能换来什么。\n但你能决定的是，今天有没有备好雨伞，有没有好好爱人以及是否为自己追求的理想拼尽全力。\n永远不要只看见前方路途遥远而忘了从前的自己坚持了多久才走到了这里，\n今天拼尽全力虽然艰辛万分，在未来都将成为落在你身上的礼物。'),
       (10, '2024-12-09 01:02:13', '愿你有明天可奔赴，有过往可回头',
        '此去经年，用一张离家的车票，换一段有故事可说的人生。\n大概就是那时候怀揣着梦想，背上了行囊，一腔热血决定远行的你。独自拎着一只行李箱，背着双肩包在深夜抵达一个陌生的城市，站在人潮涌动的车站出口，望着宽敞公路上车水马龙的喧嚣，城市里亮如白昼的繁华。',
        'file/newsPicture10.jpg',
        '这世界上的人们都活的匆匆忙忙，每个人看起来都很焦虑，每个人都有自己的苦楚，却往往找不到人可以诉说。也许你的倾诉，对别人来说是一种打扰，而且别人没经历过你的经历，所以无法体会你的心情。我们总是在心情不好的时候拿着手机，翻翻通讯录，然后再默默放下手机，压住心中那股想要找人说说话的想法。\n等到低落的情绪慢慢有些好转，才开始明白，人这一生，无论是什么样的路，都是自己的选择，冲动也好，深思熟虑也罢，自己选的路，不管多难，都要自己走下去。心中的感受，不管是复杂还是简单，都只能自己来懂，别人帮不了你，也安慰不了你，顶多给你一些无济于事的劝告，最后还得靠自己想开。你选的幸福，你自己享受;你选的眼泪，你自己来流，生活就是如此，自己的路，自己走;自己的心，自己懂。');
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orders`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `orderid`       varchar(200) NOT NULL COMMENT '订单编号',
    `tablename`     varchar(200)          DEFAULT 'nongchanpinxinxi' COMMENT '商品表名',
    `goodid`        bigint(20) NOT NULL COMMENT '商品id',
    `goodname`      varchar(200)          DEFAULT NULL COMMENT '商品名称',
    `picture`       longtext     NOT NULL COMMENT '图片',
    `buynumber`     int(11) DEFAULT NULL COMMENT '购买数量',
    `price`         double                DEFAULT NULL COMMENT '单价',
    `discountprice` double                DEFAULT NULL COMMENT '折扣价',
    `total`         double                DEFAULT NULL COMMENT '总价',
    `discounttotal` double                DEFAULT NULL COMMENT '折扣总价格',
    `type`          varchar(200)          DEFAULT NULL COMMENT '支付类型',
    `status`        varchar(200)          DEFAULT NULL COMMENT '订单状态',
    `address`       varchar(200)          DEFAULT NULL COMMENT '地址',
    `tel`           varchar(200)          DEFAULT NULL COMMENT '电话',
    `consignee`     varchar(200)          DEFAULT NULL COMMENT '收货人',
    `remark`        varchar(200)          DEFAULT NULL COMMENT '备注',
    `logistics`     longtext COMMENT '物流',
    `role`          varchar(200)          DEFAULT NULL COMMENT '用户角色',
    `userid`        bigint(20) NOT NULL COMMENT '用户id',
    `sfsh`          varchar(200)          DEFAULT NULL COMMENT '是否审核',
    `shhf`          longtext COMMENT '回复内容',
    `account`       varchar(200)          DEFAULT NULL COMMENT '商户名称',
    PRIMARY KEY (`id`),
    UNIQUE KEY `orderid` (`orderid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品订单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK
TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `productcategory`
--

DROP TABLE IF EXISTS `productcategory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `productcategory`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`         timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `productcategory` varchar(200)       DEFAULT NULL COMMENT '产品分类',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='产品分类';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productcategory`
--

LOCK
TABLES `productcategory` WRITE;
/*!40000 ALTER TABLE `productcategory` DISABLE KEYS */;
INSERT INTO `productcategory`
VALUES (1, '2024-12-09 01:02:13', '产品分类1'),
       (2, '2024-12-09 01:02:13', '产品分类2'),
       (3, '2024-12-09 01:02:13', '产品分类3'),
       (4, '2024-12-09 01:02:13', '产品分类4'),
       (5, '2024-12-09 01:02:13', '产品分类5'),
       (6, '2024-12-09 01:02:13', '产品分类6'),
       (7, '2024-12-09 01:02:13', '产品分类7'),
       (8, '2024-12-09 01:02:13', '产品分类8'),
       (9, '2024-12-09 01:02:13', '产品分类9'),
       (10, '2024-12-09 01:02:13', '产品分类10');
/*!40000 ALTER TABLE `productcategory` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `storeup`
--

DROP TABLE IF EXISTS `storeup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storeup`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `refid`     bigint(20) DEFAULT NULL COMMENT 'refid',
    `tablename` varchar(200)          DEFAULT NULL COMMENT '表名',
    `name`      varchar(200) NOT NULL COMMENT '名称',
    `picture`   longtext     NOT NULL COMMENT '图片',
    `type`      varchar(200)          DEFAULT '1' COMMENT '类型(1:收藏,21:赞,22:踩,31:竞拍参与,41:关注)',
    `inteltype` varchar(200)          DEFAULT NULL COMMENT '推荐类型',
    `remark`    varchar(200)          DEFAULT NULL COMMENT '备注',
    `userid`    bigint(20) NOT NULL COMMENT '用户id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='我的收藏';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storeup`
--

LOCK
TABLES `storeup` WRITE;
/*!40000 ALTER TABLE `storeup` DISABLE KEYS */;
/*!40000 ALTER TABLE `storeup` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `supplier`
--

DROP TABLE IF EXISTS `supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplier`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `account`       varchar(200) NOT NULL COMMENT '账号',
    `password`      varchar(200) NOT NULL COMMENT '密码',
    `name`          varchar(200) NOT NULL COMMENT '名称',
    `contact_phone` varchar(200)          DEFAULT NULL COMMENT '联系电话',
    `address`       varchar(200)          DEFAULT NULL COMMENT '地址',
    PRIMARY KEY (`id`),
    UNIQUE KEY `account` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8 COMMENT='供应商';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supplier`
--

LOCK
TABLES `supplier` WRITE;
/*!40000 ALTER TABLE `supplier` DISABLE KEYS */;
INSERT INTO `supplier`
VALUES (51, '2024-12-09 01:02:13', '账号1', '123456', '名称1', '19819881111', '地址1'),
       (52, '2024-12-09 01:02:13', '账号2', '123456', '名称2', '19819881112', '地址2'),
       (53, '2024-12-09 01:02:13', '账号3', '123456', '名称3', '19819881113', '地址3'),
       (54, '2024-12-09 01:02:13', '账号4', '123456', '名称4', '19819881114', '地址4'),
       (55, '2024-12-09 01:02:13', '账号5', '123456', '名称5', '19819881115', '地址5'),
       (56, '2024-12-09 01:02:13', '账号6', '123456', '名称6', '19819881116', '地址6'),
       (57, '2024-12-09 01:02:13', '账号7', '123456', '名称7', '19819881117', '地址7'),
       (58, '2024-12-09 01:02:13', '账号8', '123456', '名称8', '19819881118', '地址8'),
       (59, '2024-12-09 01:02:13', '账号9', '123456', '名称9', '19819881119', '地址9'),
       (60, '2024-12-09 01:02:13', '账号10', '123456', '名称10', '198198811110', '地址10');
/*!40000 ALTER TABLE `supplier` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `token`
--

DROP TABLE IF EXISTS `token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `token`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userid`        bigint(20) NOT NULL COMMENT '用户id',
    `username`      varchar(100) NOT NULL COMMENT '用户名',
    `tablename`     varchar(100)          DEFAULT NULL COMMENT '表名',
    `role`          varchar(100)          DEFAULT NULL COMMENT '角色',
    `token`         varchar(200) NOT NULL COMMENT '密码',
    `addtime`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `expiratedtime` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='token表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token`
--

LOCK
TABLES `token` WRITE;
/*!40000 ALTER TABLE `token` DISABLE KEYS */;
/*!40000 ALTER TABLE `token` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `user_account`  varchar(200) NOT NULL COMMENT '用户账号',
    `user_password` varchar(200) NOT NULL COMMENT '用户密码',
    `user_name`     varchar(200) NOT NULL COMMENT '用户姓名',
    `headportrait`  longtext COMMENT '头像',
    `gender`        varchar(200)          DEFAULT NULL COMMENT '性别',
    `phone_number`  varchar(200)          DEFAULT NULL COMMENT '电话号码',
    `money`         double                DEFAULT '0' COMMENT '余额',
    PRIMARY KEY (`id`),
    UNIQUE KEY `user_account` (`user_account`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8 COMMENT='用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK
TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
-- 密码统一使用加盐 MD5：MD5("agri_platform_2026_salt" + 明文 + "agri_platform_2026_salt")
-- 这里所有账号的明文密码都是 "123456"
INSERT INTO `user`
VALUES (41, '2024-12-09 01:02:13', '用户账号1', '8b13837388e5d22d10dea402cae08d3f', '用户姓名1', 'file/userHeadportrait1.jpg', '男',
        '19819881111', 200),
       (42, '2024-12-09 01:02:13', '用户账号2', '8b13837388e5d22d10dea402cae08d3f', '用户姓名2', 'file/userHeadportrait2.jpg', '男',
        '19819881112', 200),
       (43, '2024-12-09 01:02:13', '用户账号3', '8b13837388e5d22d10dea402cae08d3f', '用户姓名3', 'file/userHeadportrait3.jpg', '男',
        '19819881113', 200),
       (44, '2024-12-09 01:02:13', '用户账号4', '8b13837388e5d22d10dea402cae08d3f', '用户姓名4', 'file/userHeadportrait4.jpg', '男',
        '19819881114', 200),
       (45, '2024-12-09 01:02:13', '用户账号5', '8b13837388e5d22d10dea402cae08d3f', '用户姓名5', 'file/userHeadportrait5.jpg', '男',
        '19819881115', 200),
       (46, '2024-12-09 01:02:13', '用户账号6', '8b13837388e5d22d10dea402cae08d3f', '用户姓名6', 'file/userHeadportrait6.jpg', '男',
        '19819881116', 200),
       (47, '2024-12-09 01:02:13', '用户账号7', '8b13837388e5d22d10dea402cae08d3f', '用户姓名7', 'file/userHeadportrait7.jpg', '男',
        '19819881117', 200),
       (48, '2024-12-09 01:02:13', '用户账号8', '8b13837388e5d22d10dea402cae08d3f', '用户姓名8', 'file/userHeadportrait8.jpg', '男',
        '19819881118', 200),
       (49, '2024-12-09 01:02:13', '用户账号9', '8b13837388e5d22d10dea402cae08d3f', '用户姓名9', 'file/userHeadportrait9.jpg', '男',
        '19819881119', 200),
       (50, '2024-12-09 01:02:13', '用户账号10', '8b13837388e5d22d10dea402cae08d3f', '用户姓名10', 'file/userHeadportrait10.jpg', '男',
        '198198811110', 200);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users`
(
    `id`       bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `addtime`  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `username` varchar(200) NOT NULL COMMENT '用户名',
    `password` varchar(200) NOT NULL COMMENT '密码',
    `role`     varchar(200)          DEFAULT NULL COMMENT '角色',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='管理员';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK
TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users`
VALUES (1, '2024-12-09 01:02:13', 'admin', 'admin', '管理员');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK
TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-09  9:04:58
