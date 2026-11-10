<template>
	<div class="top_view">
		<div class="top_left_view">
			<div class="fold_view" @click="toggleClick" :class="{'is_collapse':collapse}">
				<el-icon class="icons">
					<Fold v-if="!collapse" />
					<Expand v-else />
				</el-icon>
			</div>
		</div>

		<div class="projectTitle">农产品销售平台</div>
		<div class="top_right_view">
			<el-dropdown class="avatar-container" trigger="hover">
				<div class="avatar-wrapper">
					<div class="nickname">欢迎 {{$toolUtil.storageGet('adminName')}}</div>
					<img class="user-avatar" :src="store.getters['user/avatar']">
					<el-icon class="el-icon-arrow-down">
						<arrow-down />
					</el-icon>
				</div>
				<template #dropdown>
					<el-dropdown-menu class="user-dropDown" slot="dropdown">
						<el-dropdown-item @click="centerClick" v-if="roleName!='管理员'">
							个人中心
						</el-dropdown-item>
						<el-dropdown-item @click="updatepasswordClick">
							修改密码
						</el-dropdown-item>
						<el-dropdown-item>
							<span style="display:block;" @click="onLogout">退出登录</span>
						</el-dropdown-item>
					</el-dropdown-menu>
				</template>
			</el-dropdown>
		</div>
		<div class="breadcrumb-view">
			<el-breadcrumb separator="—">
				<el-breadcrumb-item v-for="(item,index) in breadcrumbList" :key="item.path">
					<span @click="router.push(item.path)">{{item.name}}</span>
				</el-breadcrumb-item>
			</el-breadcrumb>
		</div>
	</div>
</template>

<script setup>
	import axios from 'axios'
    import moment from "moment"
	import {
		ElMessageBox
	} from 'element-plus'
	import {
		toRefs,
		defineEmits,
		getCurrentInstance,
		ref,
		onBeforeUnmount,
		computed,
	} from 'vue';

	import {
		useRouter,
		useRoute
	} from 'vue-router';
	const router = useRouter()
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const emit = defineEmits(['collapseChange'])
	const role = context?.$toolUtil.storageGet('sessionTable')
	const roleName = context?.$toolUtil.storageGet('role')
	const route = useRoute()
	let breadcrumbList = computed(()=>{
		let arr = route.matched.map(item=>({
			name:item.name,
			path:item.path
		}))
		return arr[arr.length-1].path=='/'?[arr[0]]:arr
	})

	const props = defineProps({
		collapse: Boolean
	})
	const {collapse} = toRefs(props)

	//获取用户信息
	import { useStore } from 'vuex'
	const store = useStore()
	const user = computed(()=>store.getters['user/session'])
	const avatar = ref(store.state.user.avatar)
	if(!store.state.user.session.id){
		store.dispatch('user/getSession')
	}
	const toggleClick = () => {
		emit('collapseChange')
	}
	// 退出登录
	const onLogout = () => {
		let toolUtil = context?.$toolUtil
		store.dispatch('delAllCachedViews')
		store.dispatch('delAllVisitedViews')
		toolUtil.storageClear()
		router.replace({
			name: "login"
		});
	}
	// 个人中心
	const centerClick = () => {
		router.push(`/${role}Center`)
	}
	// 修改密码
	const updatepasswordClick = () => {
		router.push(`/updatepassword`)
	}
</script>

<style lang="scss" scoped>
	// 总盒子
	.top_view {
		// 左边盒子
		.top_left_view {
			// 折叠按钮盒子
			.fold_view {
				// 图标
				.icons {
				}
			}
		}
		// 标题
		.projectTitle{
		}
		// 右部盒子
		.top_right_view{
			// 头像盒子
			.avatar-container {
				.avatar-wrapper {
					// 昵称
					.nickname {
					}
					// 头像
					.user-avatar {
					}
					// 图标
					.el-icon-arrow-down {
					}
				}
			}
		}
	}
	// 下拉盒子
	.el-dropdown-menu{
		// 下拉盒子itme
		:deep(.el-dropdown-menu__item){
		}
		// item悬浮
		:deep(.el-dropdown-menu__item:hover){
		}
	}
</style>
<style>
.top_view img.user-avatar {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    margin-right: 10px;
}

.top_view {
    display: flex;
    align-items: center;
    background: #DADEDA;
    height: 114px;
    width: 100%;
    border: none;
    flex-wrap: wrap;
    flex-direction: row-reverse;
}

.top_view .top_left_view {
    display: none;
}

.top_view .projectTitle {display: none;}

.top_view .actionBar {
    display: flex;
}

.top_view .notice-btn {
    order: 3;
    border: none;
    background: none;
    color: #000;
}

.top_view .top_right_view {
    order: 2;
    margin: 0 20px;
}

.top_view .avatar-wrapper {
    display: flex;
    align-items: center;
}

.top_view .avatar-wrapper .nickname {
    order: 2;
    color: #000;
}

.top_view .avatar-wrapper i {
    order: 3;
    color: #000;
}
.top_view .el-dropdown-menu__item:focus,.top_view  .el-dropdown-menu__item:not(.is-disabled):hover {
    color: #6F9867;
    background: rgb(111 152 103 / 10%);
}

.top_view .breadcrumb-view {
    order: 9;
    width: 100%;
    height: 54px;
    background: #EAEAEA;
    padding: 0 26px;
}

.top_view .el-breadcrumb__inner {
    /* line-height: 54px; */
    color: #000!important;
}

.top_view .el-breadcrumb__separator {
    color: #000;
}

.top_view .el-breadcrumb__item {
    height: 54px;
    line-height: 54px;
    font-size: 14px;
}
</style>