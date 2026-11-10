-- ============================================================
-- 密码加密迁移脚本
-- 在运行 Java 代码之前，先执行此脚本将存量明文密码加密为 MD5+盐
-- 加密规则：MD5( 'agri_platform_2026_salt' + 明文密码 + 'agri_platform_2026_salt' )
-- ============================================================

USE db_project;

-- 1. 普通用户表：user_password 字段
UPDATE user 
SET user_password = MD5(CONCAT('agri_platform_2026_salt', user_password, 'agri_platform_2026_salt'))
WHERE user_password IS NOT NULL AND LENGTH(user_password) < 32;

-- 2. 管理员表：password 字段
UPDATE users 
SET password = MD5(CONCAT('agri_platform_2026_salt', password, 'agri_platform_2026_salt'))
WHERE password IS NOT NULL AND LENGTH(password) < 32;

-- 3. 供应商表：password 字段
UPDATE supplier 
SET password = MD5(CONCAT('agri_platform_2026_salt', password, 'agri_platform_2026_salt'))
WHERE password IS NOT NULL AND LENGTH(password) < 32;

-- ============================================================
-- 验证迁移结果（可选）：
-- 迁移后密码应该是 32 位 MD5 hash
-- SELECT user_account, LENGTH(user_password) as pwd_len FROM user;
-- SELECT username, LENGTH(password) as pwd_len FROM users;
-- SELECT account, LENGTH(password) as pwd_len FROM supplier;
-- ============================================================

-- ============================================================
-- ⚠️ 重要提示：
-- 1. 迁移前请备份数据库！
-- 2. 迁移脚本里 LENGTH(password) < 32 这个条件是为了：
--    - 只加密还没加密过的明文密码（明文一般很短）
--    - 不会把已经加密过的（32位MD5）再加密一遍
-- 3. 如果数据库里已有部分用户密码是 32 位 MD5（早期可能用过纯MD5），
--    那这些用户登录会失败，需要让他们用新密码重置接口重新设置密码
-- ============================================================
