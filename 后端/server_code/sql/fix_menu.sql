SET SQL_SAFE_UPDATES=0;
-- 去掉管理员已支付订单的审核按钮
UPDATE menu SET menujson = REPLACE(menujson, 
  '"buttons":["新增","查看","修改","删除","审核","导出","发货","物流"]', 
  '"buttons":["新增","查看","修改","删除","导出","发货","物流"]') 
WHERE id=1;

SELECT ROW_COUNT() AS affected_rows;
-- 验证
SELECT id, IF(menujson LIKE '%"buttons":["新增","查看","修改","删除","导出","发货","物流"]%', '已去掉审核', '未找到匹配') AS result FROM menu WHERE id=1;
