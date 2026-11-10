-- 给供应商 root 造测试订单（12 条，覆盖 6 天 x 5 种状态 x 3 商品 x 2 分类）
INSERT INTO orders (orderid, tablename, goodid, goodname, picture, buynumber, price, discountprice, total, discounttotal, type, status, address, tel, consignee, role, userid, sfsh, account, addtime) VALUES
('ROOT001','nongchanpinxinxi',2,'土豆','',2,36.6,36.6,73.2,73.2,'1','已完成','北京市朝阳区','13800000001','张三','用户',41,'','root',NOW()),
('ROOT002','nongchanpinxinxi',10,'猕猴桃','',1,48.8,48.8,48.8,48.8,'1','已支付','上海市浦东区','13800000002','李四','用户',50,'','root',NOW()),
('ROOT003','nongchanpinxinxi',11,'红薯','',3,29.9,29.9,89.7,89.7,'1','已发货','广州市天河区','13800000003','王五','用户',41,'','root',DATE_SUB(NOW(), INTERVAL 1 DAY)),
('ROOT004','nongchanpinxinxi',2,'土豆','',1,36.6,36.6,36.6,36.6,'1','已完成','深圳市南山区','13800000004','赵六','用户',50,'','root',DATE_SUB(NOW(), INTERVAL 1 DAY)),
('ROOT005','nongchanpinxinxi',10,'猕猴桃','',2,48.8,48.8,97.6,97.6,'1','已完成','杭州市西湖区','13800000005','孙七','用户',41,'','root',DATE_SUB(NOW(), INTERVAL 3 DAY)),
('ROOT006','nongchanpinxinxi',11,'红薯','',1,29.9,29.9,29.9,29.9,'1','已取消','成都市武侯区','13800000006','周八','用户',50,'','root',DATE_SUB(NOW(), INTERVAL 3 DAY)),
('ROOT007','nongchanpinxinxi',2,'土豆','',5,36.6,36.6,183,183,'1','已退款','武汉市江汉区','13800000007','吴九','用户',41,'是','root',DATE_SUB(NOW(), INTERVAL 5 DAY)),
('ROOT008','nongchanpinxinxi',10,'猕猴桃','',2,48.8,48.8,97.6,97.6,'1','已完成','南京市鼓楼区','13800000008','郑十','用户',50,'','root',DATE_SUB(NOW(), INTERVAL 5 DAY)),
('ROOT009','nongchanpinxinxi',10,'猕猴桃','',3,48.8,48.8,146.4,146.4,'1','已完成','西安市雁塔区','13800000009','钱一','用户',41,'','root',DATE_SUB(NOW(), INTERVAL 6 DAY)),
('ROOT010','nongchanpinxinxi',11,'红薯','',4,29.9,29.9,119.6,119.6,'1','已完成','重庆市渝中区','13800000010','冯二','用户',50,'','root',DATE_SUB(NOW(), INTERVAL 2 DAY)),
('ROOT011','nongchanpinxinxi',2,'土豆','',3,36.6,36.6,109.8,109.8,'1','已完成','天津市南开区','13800000011','陈三','用户',41,'','root',DATE_SUB(NOW(), INTERVAL 4 DAY)),
('ROOT012','nongchanpinxinxi',11,'红薯','',2,29.9,29.9,59.8,59.8,'1','已支付','青岛市市南区','13800000012','褚四','用户',50,'待审核','root',DATE_SUB(NOW(), INTERVAL 1 DAY));
