<template>
	<div>
		<div class="center_view">
			<div class="list_search_view">
				<el-form :model="searchQuery" class="search_form">
					<div class="search_view">
						<div class="search_label">商品名称：</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.productName" placeholder="商品名称" clearable></el-input>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">小车编号：</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.cartId" placeholder="小车编号" clearable></el-input>
						</div>
					</div>
					<div class="search_btn_view">
						<el-button class="search_btn" type="primary" @click="searchClick()" size="small">搜索</el-button>
					</div>
				</el-form>
				<div class="btn_view">
					<el-button class="del_btn" type="danger" @click="delClick()" v-if="btnAuth('caidata','删除')">
						批量删除
					</el-button>
				</div>
			</div>
			<br>
			<el-table
				:data="list"
				border
				striped
				style="width: 100%"
				@selection-change="handleSelectionChange"
				v-if="btnAuth('caidata','查看')">
				<el-table-column type="selection" width="55" v-if="btnAuth('caidata','删除')"></el-table-column>
				<el-table-column prop="id" label="ID" width="70"></el-table-column>
				<el-table-column prop="productName" label="商品名称" width="140"></el-table-column>
				<el-table-column prop="quantity" label="采摘数量" width="100">
					<template #default="scope">
						<span style="color:#7f9e39;font-weight:bold;">{{ scope.row.quantity }}</span>
					</template>
				</el-table-column>
				<el-table-column prop="cartId" label="小车编号" width="140"></el-table-column>
				<el-table-column prop="account" label="供应商账号" width="140"></el-table-column>
				<el-table-column prop="addtime" label="上报时间" width="180"></el-table-column>
				<el-table-column label="操作" width="120" fixed="right">
					<template #default="scope">
						<el-button class="view_btn" type="info" size="small" @click="infoClick(scope.row.id)">详情</el-button>
						<el-button class="del_btn" type="danger" size="small" @click="delClick(scope.row.id)" v-if="btnAuth('caidata','删除')">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<el-pagination
				background
				:layout="layouts.join(',')"
				:total="total"
				:page-size="listQuery.limit"
				v-model:current-page="listQuery.page"
				prev-text="➨"
				next-text="➨"
				:hide-on-single-page="false"
				:page-sizes="[10, 20, 30, 50, 100]"
				@size-change="sizeChange"
				@current-change="currentChange" />
		</div>

		<!-- 详情弹窗 -->
		<el-dialog v-model="infoVisible" title="采摘数据详情" width="600px">
			<div v-if="currentInfo">
				<el-descriptions :column="2" border>
					<el-descriptions-item label="商品名称">{{ currentInfo.productName }}</el-descriptions-item>
					<el-descriptions-item label="采摘数量">{{ currentInfo.quantity }}</el-descriptions-item>
					<el-descriptions-item label="小车编号">{{ currentInfo.cartId }}</el-descriptions-item>
					<el-descriptions-item label="供应商账号">{{ currentInfo.account }}</el-descriptions-item>
					<el-descriptions-item label="上报时间" :span="2">{{ currentInfo.addtime }}</el-descriptions-item>
					<el-descriptions-item label="原始报文" :span="2">
						<pre style="white-space:pre-wrap;font-size:12px;">{{ formatPayload(currentInfo.payload) }}</pre>
					</el-descriptions-item>
				</el-descriptions>
			</div>
			<template #footer>
				<el-button @click="infoVisible = false">关闭</el-button>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import { ElMessageBox } from 'element-plus'

const context = getCurrentInstance()?.appContext.config.globalProperties

const tableName = 'caidata'
const list = ref([])
const total = ref(0)
const selRows = ref([])
const listQuery = ref({ page: 1, limit: 20, sort: 'id', order: 'desc' })
const searchQuery = ref({})
const layouts = ref(['total', 'prev', 'pager', 'next', 'sizes', 'jumper'])

const infoVisible = ref(false)
const currentInfo = ref(null)

const btnAuth = (e, a) => context?.$toolUtil.isAuth(e, a)

const getList = () => {
	let params = JSON.parse(JSON.stringify(listQuery.value))
	params.sort = 'id'
	params.order = 'desc'
	if (searchQuery.value.productName && searchQuery.value.productName !== '') {
		params.productName = '%' + searchQuery.value.productName + '%'
	}
	if (searchQuery.value.cartId && searchQuery.value.cartId !== '') {
		params.cartId = '%' + searchQuery.value.cartId + '%'
	}
	context.$http({
		url: `${tableName}/page`,
		method: 'get',
		params
	}).then(res => {
		list.value = res.data.data.list
		total.value = Number(res.data.data.total)
	})
}

const searchClick = () => {
	listQuery.value.page = 1
	getList()
}

const sizeChange = (size) => { listQuery.value.limit = size; getList() }
const currentChange = (page) => { listQuery.value.page = page; getList() }

const handleSelectionChange = (e) => { selRows.value = e }

const infoClick = (id) => {
	context.$http({
		url: `${tableName}/info/${id}`,
		method: 'get'
	}).then(res => {
		currentInfo.value = res.data.data
		infoVisible.value = true
	})
}

const delClick = (id) => {
	let ids = ref([])
	if (id) {
		ids.value = [id]
	} else {
		if (selRows.value.length) {
			for (let x in selRows.value) ids.value.push(selRows.value[x].id)
		} else {
			return context?.$toolUtil.message('请选择要删除的记录', 'warning')
		}
	}
	ElMessageBox.confirm(`确定删除选中的 ${ids.value.length} 条采摘记录？`, '提示', {
		confirmButtonText: '是', cancelButtonText: '否', type: 'warning',
	}).then(() => {
		context.$http({
			url: `${tableName}/delete`,
			method: 'post',
			data: ids.value
		}).then(() => {
			context?.$toolUtil.message('删除成功', 'success', () => getList())
		})
	})
}

const formatPayload = (p) => {
	if (!p) return '-'
	try { return JSON.stringify(JSON.parse(p), null, 2) } catch { return p }
}

onMounted(() => { getList() })
</script>

<style lang="scss" scoped>
// 和项目其他列表页保持一致的样式
.list_search_view {
	.search_form {
		.search_view {
			display: inline-flex;
			align-items: center;
			margin-right: 16px;
			.search_label { margin-right: 8px; white-space: nowrap; }
			.search_box { :deep(.search_inp) { width: 180px; } }
		}
		.search_btn_view { display: inline-block; vertical-align: middle; }
	}
	.btn_view { margin-top: 8px; }
}
</style>
