-- 采摘小车操作日志表
-- harvest 字段只做接口入参，不落库
-- 库存通过调用 increaseStock 公共方法修改
CREATE TABLE IF NOT EXISTS caidata (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  addtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  productname varchar(200) DEFAULT NULL COMMENT '商品名称',
  account varchar(200) DEFAULT NULL COMMENT '供应商账号',
  number varchar(200) DEFAULT NULL COMMENT '小车编号',
  action varchar(50) DEFAULT 'harvest' COMMENT '操作类型：harvest采摘',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采摘小车操作日志';
