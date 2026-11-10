-- 采摘小车菜单项：给管理员(索引0)和商家(索引1)各追加
-- 使用 MySQL JSON_ARRAY_APPEND 安全追加

-- 1. 给管理员追加 "采摘小车管理" 分组
UPDATE menu SET menujson = JSON_ARRAY_APPEND(
    menujson,
    '$[0].backMenu',
    CAST('{"child":[{"allButtons":["查看","删除"],"buttons":["查看","删除"],"classname":"caidata","menu":"采摘数据列表","menuJump":"列表","tableName":"caidata"}],"fontClass":"icon-common33","menu":"采摘小车管理","unicode":"&#xee6a;"}' AS JSON)
) WHERE id = 1;

-- 2. 给商家追加 "采摘小车管理" 分组
UPDATE menu SET menujson = JSON_ARRAY_APPEND(
    menujson,
    '$[1].backMenu',
    CAST('{"child":[{"allButtons":["查看","删除"],"buttons":["查看"],"classname":"caidata","menu":"采摘数据列表","menuJump":"列表","tableName":"caidata"}],"fontClass":"icon-common33","menu":"采摘小车管理","unicode":"&#xee6a;"}' AS JSON)
) WHERE id = 1;
