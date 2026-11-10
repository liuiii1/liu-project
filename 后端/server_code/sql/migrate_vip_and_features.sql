-- ============================================================
-- 迁移脚本：补全 VIP 功能 + 数据库字段
-- 执行前请先备份数据库！
-- ============================================================

-- 1. user 表加 vip 字段（用户是否为 VIP）
ALTER TABLE `user` ADD COLUMN `vip` varchar(200) DEFAULT '否' COMMENT 'VIP标识（是/否）' AFTER `money`;

-- 2. agriculturalproductinformation 表加 vipprice 字段（VIP 专享价）
ALTER TABLE `agriculturalproductinformation` ADD COLUMN `vipprice` double DEFAULT 0 COMMENT 'VIP价格' AFTER `price`;

-- 3. 可选：给几个商品设置一个 VIP 价（测试用）
UPDATE `agriculturalproductinformation` SET `vipprice` = ROUND(`price` * 0.85, 2) WHERE `vipprice` = 0;
-- 效果：VIP 用户享 85 折
