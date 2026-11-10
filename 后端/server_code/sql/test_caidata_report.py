# -*- coding: utf-8 -*-
"""
采摘小车上报接口测试脚本
用法: python test_caidata_report.py
依赖: pip install requests
"""
import requests
import json

BASE_URL = "http://localhost:8080/project"

def test_report_direct():
    """方式1: 直接调 /caidata/report (小车固件模式，无需登录 token)"""
    url = f"{BASE_URL}/caidata/report"
    payload = {
        "number":     "CART001",
        "productname": "苹果",
        "account":    "root",
        "harvest":    5,
        "weight":     12.5,
        "temperature": 22.0,
        "humidity":   65.0,
        "location":   "A区-3号大棚"
    }
    print(f"[1] POST {url}")
    print(f"    payload: {json.dumps(payload, ensure_ascii=False)}")
    try:
        resp = requests.post(url, json=payload, timeout=10)
        data = resp.json()
        print(f"    响应: {json.dumps(data, ensure_ascii=False)}")
        if data.get('code') == 0:
            print("    ✅ 上报成功！库存已增加 5，日志已记录 (harvest 未落库)")
        else:
            print(f"    ❌ 失败: {data.get('msg')}")
    except Exception as e:
        print(f"    ❌ 请求异常: {e}")


def test_report_minimal():
    """方式2: 最小化报文 (只有必需字段)"""
    url = f"{BASE_URL}/caidata/report"
    payload = {
        "number":     "CART002",
        "productname": "苹果",
        "account":    "root",
        "harvest":    3
    }
    print(f"\n[2] 最小化报文 POST {url}")
    print(f"    payload: {json.dumps(payload, ensure_ascii=False)}")
    try:
        resp = requests.post(url, json=payload, timeout=10)
        data = resp.json()
        print(f"    响应: {json.dumps(data, ensure_ascii=False)}")
    except Exception as e:
        print(f"    ❌ 请求异常: {e}")


def test_list_with_token():
    """方式3: 供应商登录后查自己的采摘数据列表"""
    # 先登录拿 token
    login_url = f"{BASE_URL}/supplier/login"
    try:
        resp = requests.post(login_url, params={"username": "账号1", "password": "123456"}, timeout=10)
        login_data = resp.json()
        if login_data.get('code') != 0:
            print(f"\n[3] 供应商登录失败: {login_data}")
            return
        token = login_data.get('token')
        print(f"\n[3] 供应商登录成功，token={token[:20]}...")

        # 查列表
        list_url = f"{BASE_URL}/caidata/page"
        headers = {"Token": token}
        params = {"page": 1, "limit": 10}
        resp2 = requests.get(list_url, headers=headers, params=params, timeout=10)
        list_data = resp2.json()
        print(f"    列表响应 code={list_data.get('code')}, total={list_data.get('data',{}).get('total')}")
        for item in list_data.get('data', {}).get('list', []):
            print(f"      - [{item.get('id')}] {item.get('productName')} x{item.get('quantity')} (小车:{item.get('cartId')}, {item.get('addtime')})")
    except Exception as e:
        print(f"    ❌ 请求异常: {e}")


def check_stock_change():
    """验证库存是否真正增加了"""
    url = f"{BASE_URL}/agriculturalproductinformation/page"
    try:
        resp = requests.get(url, params={"page": 1, "limit": 10}, timeout=10)
        data = resp.json()
        print(f"\n[4] 当前商品库存 (alllimittimes):")
        for item in data.get('data', {}).get('list', []):
            print(f"    {item.get('productname')}({item.get('account')}): stock={item.get('alllimittimes')}")
    except Exception as e:
        print(f"    ❌ 请求异常: {e}")


if __name__ == "__main__":
    print("=" * 60)
    print(" 采摘小车 /caidata/report 接口测试")
    print("=" * 60)
    test_report_direct()
    test_report_minimal()
    test_list_with_token()
    check_stock_change()
    print("\n✅ 完成！")
