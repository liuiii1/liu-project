# ============================================
# 完整购物流程 API 测试脚本
# ============================================

$BASE = "http://localhost:8080/project"

function Curl {
    param([string]$Url, [string]$Method="GET", [string]$Token="", [string]$Body="", [string]$ContentType="application/json")
    $headers = @{}
    if ($Token) { $headers["Token"] = $Token }
    if ($Method -eq "POST" -and $Body) {
        return & curl.exe -s -X POST "$Url" -H "Token: $Token" -H "Content-Type: $ContentType" -d $Body
    }
    return & curl.exe -s "$Url" -H "Token: $Token"
}

Write-Host "========================================"
Write-Host "  STEP 1: 用户登录"
Write-Host "========================================"
$r = Curl "$BASE/user/login?username=用户账号1&password=123456" -Method POST
Write-Host "登录响应: $r"
$userToken = ($r | ConvertFrom-Json -ErrorAction SilentlyContinue).token
if (-not $userToken) {
    Write-Host "登录失败！" -ForegroundColor Red
    exit 1
}
Write-Host "用户Token: $userToken" -ForegroundColor Cyan

Write-Host "`n========================================"
Write-Host "  STEP 2: 下单前数据快照"
Write-Host "========================================"
$uBefore = Curl "$BASE/user/info/41" -Token $userToken | ConvertFrom-Json
Write-Host "用户(41) 余额: $($uBefore.data.money)"
$p1Before = Curl "$BASE/agriculturalproductinformation/info/1" -Token $userToken | ConvertFrom-Json
Write-Host "商品1(花生,账号1) 库存: alllimittimes=$($p1Before.data.alllimittimes) price=$($p1Before.data.price)"
$p2Before = Curl "$BASE/agriculturalproductinformation/info/2" -Token $userToken | ConvertFrom-Json
Write-Host "商品2(土豆,root) 库存: alllimittimes=$($p2Before.data.alllimittimes) price=$($p2Before.data.price)"
$p10Before = Curl "$BASE/agriculturalproductinformation/info/10" -Token $userToken | ConvertFrom-Json
Write-Host "商品10(猕猴桃,root) 库存: alllimittimes=$($p10Before.data.alllimittimes) price=$($p10Before.data.price)"

Write-Host "`n========================================"
Write-Host "  STEP 3: 用户下单 (事务下单 createOrder)"
Write-Host "  花生 x1 (¥42.0) + 土豆 x2 (¥36.6x2=¥73.2) + 猕猴桃 x3 (¥48.8x3=¥146.4)"
Write-Host "  总价 = ¥261.6"
Write-Host "========================================"
$orderBody = @{
    address = "北京市朝阳区建国路88号"
    tel = "13800000001"
    consignee = "张三"
    remark = "测试订单"
    type = "1"
    goods = @(
        @{ tablename = "agriculturalproductinformation"; goodid = 1; buynumber = 1; cartId = $null },
        @{ tablename = "agriculturalproductinformation"; goodid = 2; buynumber = 2; cartId = $null },
        @{ tablename = "agriculturalproductinformation"; goodid = 10; buynumber = 3; cartId = $null }
    )
} | ConvertTo-Json -Depth 5

$createRes = Curl "$BASE/orders/createOrder" -Method POST -Token $userToken -Body $orderBody
Write-Host "下单响应: $createRes"
$createJson = $createRes | ConvertFrom-Json -ErrorAction SilentlyContinue

Write-Host "`n========================================"
Write-Host "  STEP 4: 下单后验证"
Write-Host "========================================"

# 查订单数
$ordersAfter = Curl "$BASE/orders/list?page=1&limit=50" -Token $userToken | ConvertFrom-Json
Write-Host "订单总数: $($ordersAfter.data.total)"
foreach ($o in $ordersAfter.data.list) {
    Write-Host "  订单: id=$($o.id) orderid=$($o.orderid) good=$($o.goodname) qty=$($o.buynumber) total=$($o.total) account=$($o.account) status=$($o.status)"
}

# 查用户余额（应该是 1000 - 261.6 = 738.4）
$uAfter = Curl "$BASE/user/info/41" -Token $userToken | ConvertFrom-Json
Write-Host "`n用户(41) 余额: 下单前=$($uBefore.data.money) 下单后=$($uAfter.data.money) 扣减=$([math]::Round([double]$uBefore.data.money - [double]$uAfter.data.money, 2))"

# 查库存（商品1应该-1, 商品2-2, 商品10-3）
$p1After = Curl "$BASE/agriculturalproductinformation/info/1" -Token $userToken | ConvertFrom-Json
Write-Host "商品1(花生) 库存: 前=$($p1Before.data.alllimittimes) 后=$($p1After.data.alllimittimes)"
$p2After = Curl "$BASE/agriculturalproductinformation/info/2" -Token $userToken | ConvertFrom-Json
Write-Host "商品2(土豆) 库存: 前=$($p2Before.data.alllimittimes) 后=$($p2After.data.alllimittimes)"
$p10After = Curl "$BASE/agriculturalproductinformation/info/10" -Token $userToken | ConvertFrom-Json
Write-Host "商品10(猕猴桃) 库存: 前=$($p10Before.data.alllimittimes) 后=$($p10After.data.alllimittimes)"

# ============ 管理员登录验证全平台订单 ============
Write-Host "`n========================================"
Write-Host "  STEP 5: 管理员视角验证"
Write-Host "========================================"
$adminLogin = Curl "$BASE/users/login?username=admin&password=123456" -Method POST
$adminToken = ($adminLogin | ConvertFrom-Json -ErrorAction SilentlyContinue).token
$adminOrders = Curl "$BASE/orders/page?page=1&limit=50" -Token $adminToken | ConvertFrom-Json
Write-Host "管理员看到的订单总数: $($adminOrders.data.total)"

# ============ supplier root 登录（应该只看到自己的商品订单） ============
Write-Host "`n========================================"
Write-Host "  STEP 6: supplier root 视角验证（应只看到自己的订单）"
Write-Host "========================================"
$supplierLogin = Curl "$BASE/supplier/login?username=root&password=123456" -Method POST
$supplierToken = ($supplierLogin | ConvertFrom-Json -ErrorAction SilentlyContinue).token
$supOrders = Curl "$BASE/orders/page?page=1&limit=50" -Token $supplierToken | ConvertFrom-Json
Write-Host "supplier root 看到的订单总数: $($supOrders.data.total)"
foreach ($o in $supOrders.data.list) {
    Write-Host "  -> account=$($o.account) good=$($o.goodname) (只应看到account=root)"
}

# ============ supplier 账号1 登录验证 ============
Write-Host "`n========================================"
Write-Host "  STEP 7: supplier 账号1 视角验证（应只看到自己的订单）"
Write-Host "========================================"
# 先查 supplier 账号1 的密码
$sup1Login = Curl "$BASE/supplier/login?username=账号1&password=123456" -Method POST
Write-Host "supplier 账号1登录: $sup1Login"
$sup1Token = ($sup1Login | ConvertFrom-Json -ErrorAction SilentlyContinue).token
if ($sup1Token) {
    $sup1Orders = Curl "$BASE/orders/page?page=1&limit=50" -Token $sup1Token | ConvertFrom-Json
    Write-Host "supplier 账号1 看到的订单总数: $($sup1Orders.data.total)"
    foreach ($o in $sup1Orders.data.list) {
        Write-Host "  -> account=$($o.account) good=$($o.goodname) (只应看到account=账号1)"
    }
} else {
    Write-Host "账号1登录失败，可能密码不同" -ForegroundColor Yellow
}

Write-Host "`n========================================"
Write-Host "  测试脚本执行完毕"
Write-Host "========================================"
