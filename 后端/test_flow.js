const http = require('http');

const BASE = 'http://localhost:8080/project';

function request(url, opts = {}) {
    return new Promise((resolve, reject) => {
        const u = new URL(url);
        const options = {
            hostname: u.hostname,
            port: u.port,
            path: u.pathname + u.search,
            method: opts.method || 'GET',
            headers: opts.headers || {}
        };
        const req = http.request(options, (res) => {
            let data = '';
            res.on('data', chunk => data += chunk);
            res.on('end', () => {
                try { resolve(JSON.parse(data)); }
                catch { resolve({ raw: data }); }
            });
        });
        req.on('error', reject);
        if (opts.body) req.write(JSON.stringify(opts.body));
        req.end();
    });
}

async function login(tableName, username, password) {
    const res = await request(`${BASE}/${tableName}/login?username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`, { method: 'POST' });
    return res.token;
}

async function info(tableName, id, token) {
    const res = await request(`${BASE}/${tableName}/info/${id}`, { headers: { Token: token } });
    return res.data;
}

async function list(tableName, params, token) {
    const qs = new URLSearchParams(params).toString();
    const res = await request(`${BASE}/${tableName}/list?${qs}`, { headers: { Token: token } });
    return res.data;
}

async function page(tableName, params, token) {
    const qs = new URLSearchParams(params).toString();
    const res = await request(`${BASE}/${tableName}/page?${qs}`, { headers: { Token: token } });
    return res.data;
}

async function createOrder(body, token) {
    const res = await request(`${BASE}/orders/createOrder`, {
        method: 'POST',
        headers: { Token: token, 'Content-Type': 'application/json' },
        body
    });
    return res;
}

async function updateOrder(body, token) {
    const res = await request(`${BASE}/orders/update`, {
        method: 'POST',
        headers: { Token: token, 'Content-Type': 'application/json' },
        body
    });
    return res;
}

function step(title) {
    console.log('\n' + '='.repeat(60));
    console.log(`  ${title}`);
    console.log('='.repeat(60));
}

async function main() {
    step('STEP 1: User Login');
    const userToken = await login('user', '用户账号1', '123456');
    if (!userToken) { console.log('FAIL: User login failed'); return; }
    console.log('User Token:', userToken);

    step('STEP 2: State BEFORE Order');
    const uBefore = await info('user', 41, userToken);
    const p1Before = await info('agriculturalproductinformation', 1, userToken);
    const p2Before = await info('agriculturalproductinformation', 2, userToken);
    const p10Before = await info('agriculturalproductinformation', 10, userToken);
    console.log(`User(41) money: ${uBefore.money}`);
    console.log(`P1(peanut, account1) stock: alllimittimes=${p1Before.alllimittimes}`);
    console.log(`P2(potato, root) stock: alllimittimes=${p2Before.alllimittimes}`);
    console.log(`P10(kiwi, root) stock: alllimittimes=${p10Before.alllimittimes}`);

    step('STEP 3: Create Order (Transaction)');
    console.log('Items: peanut x1(42.0) + potato x2(73.2) + kiwi x3(146.4) = TOTAL 261.6');
    const orderRes = await createOrder({
        address: 'Chaoyang District, Beijing',
        tel: '13800000001',
        consignee: 'Zhang San',
        remark: 'Test order',
        type: '1',
        goods: [
            { tablename: 'agriculturalproductinformation', goodid: 1, buynumber: 1 },
            { tablename: 'agriculturalproductinformation', goodid: 2, buynumber: 2 },
            { tablename: 'agriculturalproductinformation', goodid: 10, buynumber: 3 }
        ]
    }, userToken);
    console.log('Create result:', JSON.stringify(orderRes, null, 2));

    step('STEP 4: State AFTER Order - Verification');
    const uAfter = await info('user', 41, userToken);
    const p1After = await info('agriculturalproductinformation', 1, userToken);
    const p2After = await info('agriculturalproductinformation', 2, userToken);
    const p10After = await info('agriculturalproductinformation', 10, userToken);
    console.log(`User money: ${uBefore.money} -> ${uAfter.money} (deduct: ${(uBefore.money - uAfter.money).toFixed(2)}, expect 261.6)`);
    console.log(`P1 stock: ${p1Before.alllimittimes} -> ${p1After.alllimittimes} (expect -1)`);
    console.log(`P2 stock: ${p2Before.alllimittimes} -> ${p2After.alllimittimes} (expect -2)`);
    console.log(`P10 stock: ${p10Before.alllimittimes} -> ${p10After.alllimittimes} (expect -3)`);

    // List orders for this user
    const userOrders = await page('orders', { page: 1, limit: 50 }, userToken);
    console.log(`\nUser's orders (should be 3 entries):`);
    userOrders.list.forEach(o => {
        console.log(`  id=${o.id} orderid=${o.orderid} good=${o.goodname} qty=${o.buynumber} total=${o.total} account=${o.account} status=${o.status}`);
    });

    step('STEP 5: Admin View - Should see ALL orders');
    const adminToken = await login('users', 'admin', '123456');
    const adminOrders = await page('orders', { page: 1, limit: 50 }, adminToken);
    console.log(`Admin sees ${adminOrders.total} orders (should be 3)`);

    step('STEP 6: Supplier ROOT View - Should see ONLY root accounts');
    const supplierToken = await login('supplier', 'root', '123456');
    const supOrders = await page('orders', { page: 1, limit: 50 }, supplierToken);
    console.log(`Supplier root sees ${supOrders.total} orders`);
    const allRoot = supOrders.list.every(o => o.account === 'root');
    const correctAccounts = supOrders.list.map(o => o.account).filter(a => a === 'root').length;
    console.log(`  account values: ${[...new Set(supOrders.list.map(o => o.account))].join(', ')}`);
    console.log(`  CORRECT: ${allRoot ? 'YES' : 'NO - ISOLATION BROKEN!'}`);

    step('STEP 7: Supplier ACCOUNT1 View - Should see ONLY account1 orders');
    const sup1Token = await login('supplier', '账号1', '123456');
    if (sup1Token) {
        const sup1Orders = await page('orders', { page: 1, limit: 50 }, sup1Token);
        console.log(`Supplier account1 sees ${sup1Orders.total} orders`);
        const allAccount1 = sup1Orders.list.every(o => o.account === '账号1');
        console.log(`  CORRECT: ${allAccount1 ? 'YES' : 'NO - ISOLATION BROKEN!'}`);
    } else {
        console.log('Supplier account1 login failed (wrong password?)');
    }

    step('STEP 8: Admin ships orders');
    // Ship all 3 orders
    for (const o of adminOrders.list) {
        o.status = '已发货';
        const shipRes = await updateOrder(o, adminToken);
        console.log(`  Ship order id=${o.id}: ${shipRes.code === 0 ? 'OK' : 'FAIL'}`);
    }

    step('STEP 9: User confirms receipt');
    const userOrdersAfter = await page('orders', { page: 1, limit: 50 }, userToken);
    for (const o of userOrdersAfter.list) {
        // Users can also update their own order status
        const confirmRes = await updateOrder({ ...o, status: '已收货' }, userToken);
        console.log(`  Confirm order id=${o.id}: ${confirmRes.code === 0 ? 'OK' : 'FAIL - ' + confirmRes.msg}`);
    }

    step('STEP 10: User requests return (test product id=1, peanut)');
    // Save state before return
    const uBeforeReturn = await info('user', 41, userToken);
    const p1BeforeReturn = await info('agriculturalproductinformation', 1, userToken);
    
    const ordersFinal = await page('orders', { page: 1, limit: 50 }, userToken);
    const peanutOrder = ordersFinal.list.find(o => o.goodid === 1);
    console.log(`  Target order: id=${peanutOrder.id} good=${peanutOrder.goodname} qty=${peanutOrder.buynumber} total=${peanutOrder.total}`);
    
    // User sets sfsh='待审核' to request return
    const requestReturn = await updateOrder({ ...peanutOrder, sfsh: '待审核', shhf: 'Want to return' }, userToken);
    console.log(`  User request return: ${requestReturn.code === 0 ? 'OK' : 'FAIL - ' + requestReturn.msg}`);

    step('STEP 11: Admin APPROVES return (sfsh=是) -> auto refund stock+balance');
    // Admin approves -> triggers auto-increaseStock + increaseMoney + status=已退款
    const adminOrdersFinal = await page('orders', { page: 1, limit: 50 }, adminToken);
    const toApprove = adminOrdersFinal.list.find(o => o.id === peanutOrder.id);
    console.log(`  Before approval: sfsh=${toApprove.sfsh} status=${toApprove.status}`);
    
    const approveRes = await updateOrder({ ...toApprove, sfsh: '是' }, adminToken);
    console.log(`  Admin approve: ${approveRes.code === 0 ? 'OK' : 'FAIL - ' + approveRes.msg}`);

    // Verify auto-refund
    const uAfterReturn = await info('user', 41, userToken);
    const p1AfterReturn = await info('agriculturalproductinformation', 1, userToken);
    const orderAfterReturn = await info('orders', peanutOrder.id, userToken);
    console.log(`\n  VERIFY AUTO REFUND:`);
    console.log(`  User money: ${uBeforeReturn.money} -> ${uAfterReturn.money} (expect refund: ${peanutOrder.total})`);
    console.log(`  Peanut stock: ${p1BeforeReturn.alllimittimes} -> ${p1AfterReturn.alllimittimes} (expect +${peanutOrder.buynumber})`);
    console.log(`  Order sfsh=${orderAfterReturn.sfsh} status=${orderAfterReturn.status} (expect 是 / 已退款)`);

    const moneyRefundOK = Math.abs(uAfterReturn.money - uBeforeReturn.money - peanutOrder.total) < 0.01;
    const stockRefundOK = p1AfterReturn.alllimittimes - p1BeforeReturn.alllimittimes === peanutOrder.buynumber;
    const statusOK = orderAfterReturn.status === '已退款';
    console.log(`\n  MONEY REFUND: ${moneyRefundOK ? 'YES' : 'NO!'}`);
    console.log(`  STOCK REFUND: ${stockRefundOK ? 'YES' : 'NO!'}`);
    console.log(`  STATUS UPDATED: ${statusOK ? 'YES' : 'NO!'}`);

    step('ALL TESTS COMPLETE');
}

main().catch(e => { console.error('FATAL:', e); process.exit(1); });
