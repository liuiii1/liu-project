-- 测试：把管理员已支付订单的审核按钮去掉
-- 先查看当前状态
SELECT id, 
  CASE 
    WHEN menujson LIKE '%"已支付"%' THEN 'has 已支付 tab' 
    ELSE 'no 已支付 tab' 
  END AS check_pay,
  CASE 
    WHEN menujson LIKE '%"审核"%' THEN 'has 审核 button' 
    ELSE 'no 审核 button' 
  END AS check_audit
FROM menu;
