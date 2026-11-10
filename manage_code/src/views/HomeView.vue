<template>
	<div class="dashboard">
		<!-- ========== 管理员 Dashboard 顶部卡片 ========== -->
		<template v-if="!isSupplier">
			<div class="stat-cards">
				<!-- 1. 总订单数 -->
				<div class="stat-card" style="background: linear-gradient(135deg, #b7d573 0%, #9fc160 100%);">
					<div class="stat-icon">📦</div>
					<div class="stat-info">
						<div class="stat-value">{{ stats.totalOrders || 0 }}</div>
						<div class="stat-label">总订单数</div>
					</div>
				</div>
				<!-- 2. 今日营收 -->
				<div class="stat-card" style="background: linear-gradient(135deg, #b2a4ec 0%, #9d8fdc 100%);">
					<div class="stat-icon">💰</div>
					<div class="stat-info">
						<div class="stat-value">¥{{ context.$formatMoney(stats.todayRevenue) }}</div>
						<div class="stat-label">今日营收</div>
					</div>
				</div>
				<!-- 3. 注册用户 -->
				<div class="stat-card" style="background: linear-gradient(135deg, #7ec8e3 0%, #68b3cf 100%);">
					<div class="stat-icon">👥</div>
					<div class="stat-info">
						<div class="stat-value">{{ stats.totalUsers || 0 }}</div>
						<div class="stat-label">注册用户</div>
					</div>
				</div>
				<!-- 4. 商品总数 -->
				<div class="stat-card" style="background: linear-gradient(135deg, #ffb366 0%, #e6994d 100%);">
					<div class="stat-icon">🌱</div>
					<div class="stat-info">
						<div class="stat-value">{{ stats.totalProducts || 0 }}</div>
						<div class="stat-label">商品总数</div>
					</div>
				</div>
				<!-- 警告卡：待审核退款（管理员） -->
				<div class="stat-card warning" v-if="stats.pendingReviews > 0">
					<div class="stat-icon">⚠️</div>
					<div class="stat-info">
						<div class="stat-value">{{ stats.pendingReviews }}</div>
						<div class="stat-label">待审核退款</div>
					</div>
				</div>
			</div>
		</template>

		<!-- ========== 供应商 Dashboard 顶部卡片 ========== -->
		<template v-else>
			<div class="stat-cards">
				<!-- 1. 我的订单总数 -->
				<div class="stat-card" style="background: linear-gradient(135deg, #b7d573 0%, #9fc160 100%);">
					<div class="stat-icon">📦</div>
					<div class="stat-info">
						<div class="stat-value">{{ stats.totalOrders || 0 }}</div>
						<div class="stat-label">我的订单总数</div>
					</div>
				</div>
				<!-- 2. 今日营收 -->
				<div class="stat-card" style="background: linear-gradient(135deg, #b2a4ec 0%, #9d8fdc 100%);">
					<div class="stat-icon">💰</div>
					<div class="stat-info">
						<div class="stat-value">¥{{ context.$formatMoney(stats.todayRevenue) }}</div>
						<div class="stat-label">今日营收</div>
					</div>
				</div>
				<!-- 3. 在售商品数量 -->
				<div class="stat-card" style="background: linear-gradient(135deg, #ffb366 0%, #e6994d 100%);">
					<div class="stat-icon">🌱</div>
					<div class="stat-info">
						<div class="stat-value">{{ stats.totalProducts || 0 }}</div>
						<div class="stat-label">在售商品数量</div>
					</div>
				</div>
				<!-- 4. 待处理订单（供应商最关心：待发货 + 退款审核） -->
				<div class="stat-card" style="background: linear-gradient(135deg, #ff8a8a 0%, #e67070 100%);">
					<div class="stat-icon">🔔</div>
					<div class="stat-info">
						<div class="stat-value">{{ stats.pendingOrders || 0 }}</div>
						<div class="stat-label">待处理订单</div>
					</div>
				</div>
			</div>
		</template>

		<!-- 图表区域 -->
		<div class="charts-grid">
			<!-- 订单趋势折线图 -->
			<div class="chart-card">
				<div class="chart-header">
					<span class="chart-title">{{ chartTitles.trend }}</span>
				</div>
				<div ref="trendChart" class="chart-box"></div>
			</div>

			<!-- 订单状态饼图 -->
			<div class="chart-card">
				<div class="chart-header">
					<span class="chart-title">{{ chartTitles.status }}</span>
				</div>
				<div ref="statusChart" class="chart-box"></div>
			</div>

			<!-- 热销商品 TOP 10 -->
			<div class="chart-card">
				<div class="chart-header">
					<span class="chart-title">{{ chartTitles.top }}</span>
				</div>
				<div ref="topChart" class="chart-box"></div>
			</div>

			<!-- 分类销量占比 -->
			<div class="chart-card">
				<div class="chart-header">
					<span class="chart-title">{{ chartTitles.category }}</span>
				</div>
				<div ref="categoryChart" class="chart-box"></div>
			</div>
		</div>
	</div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, getCurrentInstance } from 'vue'
import * as echarts from 'echarts'

const context = getCurrentInstance()?.appContext.config.globalProperties

const stats = ref({})
const trendData = ref([])
const statusData = ref([])
const topData = ref([])
const categoryData = ref([])

const trendChart = ref(null)
const statusChart = ref(null)
const topChart = ref(null)
const categoryChart = ref(null)

// 根据角色切换：管理员 vs 供应商
const isSupplier = computed(() => context?.$toolUtil?.storageGet('role') === '供应商')
const chartTitles = computed(() => {
	if (isSupplier.value) {
		return { trend: '近 7 天我的订单趋势', status: '订单状态分布', top: '我的热销商品 TOP', category: '我的商品分类销量占比' }
	}
	return { trend: '近 7 天订单趋势', status: '订单状态分布', top: '热销商品 TOP', category: '分类销量占比' }
})

// 统一请求方法
const fetchData = async (url) => {
	try {
		const res = await context.$http({ url, method: 'get' })
		return res.data?.data || null
	} catch (e) {
		return null
	}
}

// 加载所有统计数据
const loadAll = async () => {
	const [s, t, st, top, cat] = await Promise.all([
		fetchData('/orders/statistics'),
		fetchData('/orders/trend?days=7'),
		fetchData('/orders/status-distribution'),
		fetchData('/orders/top-products?limit=10'),
		fetchData('/orders/category-sales'),
	])
	if (s) stats.value = s
	if (t) trendData.value = t
	if (st) statusData.value = st
	if (top) topData.value = top
	if (cat) categoryData.value = cat
	await nextTick()
	renderCharts()
}

// 颜色主题：项目主色 + 辅助色
const THEME = ['#b7d573', '#b2a4ec', '#7ec8e3', '#ffb366', '#ff8a8a', '#8fbf8f', '#c9a0dc', '#68b3cf']

const renderCharts = () => {
	// 1. 订单趋势 — 双 Y 轴折线
	if (trendChart.value && trendData.value.length) {
		const dates = trendData.value.map(i => String(i.d))
		const counts = trendData.value.map(i => Number(i.cnt))
		const revenues = trendData.value.map(i => Number(i.revenue))
		echarts.init(trendChart.value).setOption({
			tooltip: { trigger: 'axis' },
			legend: { data: ['订单数', '营收'], top: 0 },
			grid: { left: 40, right: 50, bottom: 30, top: 40 },
			xAxis: { type: 'category', data: dates },
			yAxis: [
				{ type: 'value', name: '订单数', splitLine: { lineStyle: { type: 'dashed' } } },
				{ type: 'value', name: '营收(¥)', splitLine: { show: false } }
			],
			series: [
				{ name: '订单数', type: 'line', smooth: true, data: counts,
				  itemStyle: { color: '#b7d573' }, areaStyle: { color: 'rgba(183,213,115,0.2)' } },
				{ name: '营收', type: 'bar', yAxisIndex: 1, data: revenues,
				  itemStyle: { color: '#b2a4ec', borderRadius: [4, 4, 0, 0] } }
			]
		})
	}

	// 2. 订单状态分布 — 环形饼图
	if (statusChart.value && statusData.value.length) {
		const total = statusData.value.reduce((s, i) => s + Number(i.cnt), 0)
		echarts.init(statusChart.value).setOption({
			tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
			legend: { bottom: 0, type: 'scroll' },
			series: [{
				type: 'pie', radius: ['50%', '75%'], center: ['50%', '45%'],
				label: { show: false },
				data: statusData.value.map((i, idx) => ({
					name: i.status, value: Number(i.cnt),
					itemStyle: { color: THEME[idx % THEME.length] }
				})),
				emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } }
			}],
			graphic: {
				type: 'text', left: 'center', top: '38%',
				style: { text: String(total), fontSize: 24, fontWeight: 'bold', fill: '#333' }
			}
		})
	}

	// 3. 热销商品 TOP 10 — 横向柱状图
	if (topChart.value && topData.value.length) {
		const names = topData.value.map(i => i.name).reverse()
		const qtys = topData.value.map(i => Number(i.qty)).reverse()
		echarts.init(topChart.value).setOption({
			tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
			grid: { left: 10, right: 30, bottom: 10, top: 10, containLabel: true },
			xAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed' } } },
			yAxis: { type: 'category', data: names, axisLabel: { fontSize: 12 } },
			series: [{
				type: 'bar', data: qtys,
				itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
					{ offset: 0, color: '#b7d573' },
					{ offset: 1, color: '#9fc160' }
				]), borderRadius: [0, 4, 4, 0] },
				label: { show: true, position: 'right' }
			}]
		})
	}

	// 4. 分类销量占比 — 饼图
	if (categoryChart.value && categoryData.value.length) {
		echarts.init(categoryChart.value).setOption({
			tooltip: { trigger: 'item', formatter: '{b}: {c}件 ({d}%)' },
			legend: { bottom: 0, type: 'scroll' },
			series: [{
				type: 'pie', radius: '65%', center: ['50%', '45%'],
				label: { formatter: '{b}\n{d}%' },
				data: categoryData.value.map((i, idx) => ({
					name: i.category, value: Number(i.qty),
					itemStyle: { color: THEME[idx % THEME.length] }
				}))
			}]
		})
	}

	// 窗口 resize
	window.addEventListener('resize', () => {
		;[trendChart, statusChart, topChart, categoryChart].forEach(c => {
			if (c.value) echarts.getInstanceByDom(c.value)?.resize()
		})
	})
}

onMounted(() => {
	loadAll()
})
</script>

<style lang="scss" scoped>
.dashboard {
	padding: 20px;
	background: #f5f7fa;
	min-height: calc(100vh - 100px);
}

// ============ 指标卡片 ============
.stat-cards {
	display: flex;
	gap: 20px;
	margin-bottom: 24px;
	flex-wrap: wrap;
}
.stat-card {
	flex: 1;
	min-width: 200px;
	padding: 24px;
	border-radius: 12px;
	color: #fff;
	display: flex;
	align-items: center;
	gap: 16px;
	box-shadow: 0 4px 12px rgba(0,0,0,0.08);
	transition: transform .2s;
	&:hover { transform: translateY(-2px); }
	&.warning {
		background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
		animation: pulse 2s infinite;
	}
}
@keyframes pulse {
	0%, 100% { box-shadow: 0 4px 12px rgba(255,107,107,0.3); }
	50% { box-shadow: 0 4px 20px rgba(255,107,107,0.6); }
}
.stat-icon {
	font-size: 36px;
	opacity: 0.9;
}
.stat-info { flex: 1; }
.stat-value {
	font-size: 28px;
	font-weight: 700;
	line-height: 1.2;
}
.stat-label {
	font-size: 13px;
	opacity: 0.85;
	margin-top: 4px;
}

// ============ 图表区 ============
.charts-grid {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 20px;
}
.chart-card {
	background: #fff;
	border-radius: 12px;
	padding: 16px 20px;
	box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.chart-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 12px;
}
.chart-title {
	font-size: 15px;
	font-weight: 600;
	color: #333;
}
.chart-box {
	height: 320px;
	width: 100%;
}

@media (max-width: 900px) {
	.charts-grid { grid-template-columns: 1fr; }
}
</style>
