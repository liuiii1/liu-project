const http = require('http');

function req(url, opts = {}) {
    return new Promise((resolve, reject) => {
        const u = new URL(url);
        const o = {
            hostname: u.hostname, port: u.port, path: u.pathname + u.search,
            method: opts.method || 'GET',
            headers: { ...(opts.headers || {}), 'Content-Type': 'application/json' }
        };
        const r = http.request(o, res => {
            let d = ''; res.on('data', c => d += c);
            res.on('end', () => { try { resolve(JSON.parse(d)); } catch { resolve({ raw: d }); } });
        });
        r.on('error', reject);
        if (opts.body) r.write(JSON.stringify(opts.body));
        r.end();
    });
}

async function main() {
    const BASE = 'http://localhost:8080/project';
    
    // 1. Login admin
    const adminLogin = await req(`${BASE}/users/login?username=admin&password=123456`, { method: 'POST' });
    const adminToken = adminLogin.token;
    console.log('Admin token:', adminToken);

    // 2. Restore all user money to 1000
    const userList = await req(`${BASE}/user/list?page=1&limit=50`, { headers: { Token: adminToken } });
    console.log('\n=== Restoring user balances ===');
    for (const u of userList.data.list) {
        const res = await req(`${BASE}/user/update`, {
            method: 'POST', headers: { Token: adminToken },
            body: { ...u, money: 1000.0 }
        });
        console.log(`  User ${u.id} (${u.userAccount}) money: ${u.money} -> 1000.0  ${res.code === 0 ? 'OK' : 'FAIL'}`);
    }

    // 3. Restore all product stock to 100
    const prodList = await req(`${BASE}/agriculturalproductinformation/list?page=1&limit=50`, { headers: { Token: adminToken } });
    console.log('\n=== Restoring product stock ===');
    for (const p of prodList.data.list) {
        const res = await req(`${BASE}/agriculturalproductinformation/update`, {
            method: 'POST', headers: { Token: adminToken },
            body: { ...p, alllimittimes: 100, number: 100 }
        });
        console.log(`  Product ${p.id} (${p.productname}) stock: ${p.alllimittimes} -> 100  ${res.code === 0 ? 'OK' : 'FAIL'}`);
    }

    console.log('\n=== DONE ===');
    
    // 4. Verify
    const u41 = await req(`${BASE}/user/info/41`, { headers: { Token: adminToken } });
    const p1 = await req(`${BASE}/agriculturalproductinformation/info/1`, { headers: { Token: adminToken } });
    const p2 = await req(`${BASE}/agriculturalproductinformation/info/2`, { headers: { Token: adminToken } });
    const p10 = await req(`${BASE}/agriculturalproductinformation/info/10`, { headers: { Token: adminToken } });
    console.log(`\nVERIFY:`);
    console.log(`  User 41 money = ${u41.data.money}`);
    console.log(`  P1 stock = ${p1.data.alllimittimes}`);
    console.log(`  P2 stock = ${p2.data.alllimittimes}`);
    console.log(`  P10 stock = ${p10.data.alllimittimes}`);
}

main().catch(console.error);
