<template>
	<div>
		<div class="center_view edit_form">
			<el-form class="userinfo_form" ref="userinfoFormRef" :model="form">
				<el-row>
					<el-col :span="12">
						<el-form-item label="账号" prop="account">
							<el-input class="list_inp" v-model="user.account" readonly placeholder="账号" clearable />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="名称" prop="name">
							<el-input class="list_inp" v-model="user.name"  placeholder="名称" clearable />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="联系电话" prop="contactPhone">
							<el-input class="list_inp" v-model="user.contactPhone"  placeholder="联系电话" clearable />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="地址" prop="address">
							<el-input class="list_inp" v-model="user.address"  placeholder="地址" clearable />
						</el-form-item>
					</el-col>
					<span class="formModel_btn_box">
						<el-button class='confirm_btn' type="primary" @click="onSubmit">保存</el-button>
					</span>
				</el-row>
			</el-form>
		</div>
	</div>
</template>

<script setup>
	import { isNumber,isIntNumer,isEmail,isMobile,isPhone,isURL,checkIdCard } from "@/utils/toolUtil";
	import {
		reactive,
		ref,
		getCurrentInstance
	} from 'vue'
	import { useStore } from 'vuex'
	const store = useStore()
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const tableName = ref('supplier')
	const user = ref({})
	const init = () => {
	}
	const onSubmit = () => {
		if((!user.value.account)){
			context?.$toolUtil.message(`账号不能为空`,'error')
			return false
		}
		if((!user.value.password)){
			context?.$toolUtil.message(`密码不能为空`,'error')
			return false
		}
		if((!user.value.name)){
			context?.$toolUtil.message(`名称不能为空`,'error')
			return false
		}
		if((user.value.contactPhone)&&(!context?.$toolUtil.isMobile(user.value.contactPhone))){
			context?.$toolUtil.message(`联系电话应输入手机格式`,'error')
			return false
		}
		store.dispatch('user/update',user.value).then(res=>{
			if(res?.data&&res.data.code==0)context?.$toolUtil.message('修改成功','success')
		})

	}
	const getInfo = () => {
		context?.$http({
			url: `${tableName.value}/session`,
			method: 'get'
		}).then(res => {
			user.value = res.data.data
			init()
		})
	}
	getInfo()
</script>

<style lang="scss" scoped>
	// 表单
	.userinfo_form {
		// form item
		:deep(.el-form-item) {
			// 内容盒子
			.el-form-item__content{
			}
			// 输入框
			.list_inp {
			}

		}
		// 按钮盒子
		.formModel_btn_box {
			// 确定按钮
			.confirm_btn {
			}
			// 确定按钮-悬浮
			.confirm_btn:hover {
			}
		}
	}
</style>
