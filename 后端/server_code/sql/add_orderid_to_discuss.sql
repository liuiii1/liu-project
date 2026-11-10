-- 修复: discussagriculturalproductinformation 表缺少 orderid 列
-- 实体类和 Controller 的评价功能都引用了此列，但数据库没有
USE db_project;

ALTER TABLE discussagriculturalproductinformation
    ADD COLUMN orderid bigint(20) DEFAULT NULL COMMENT '订单id（orders表主键）' AFTER userid;
