<template>
	<div>
		<div class="center_view edit_form">
			<el-form class="userinfo_form" ref="userinfoFormRef" :model="form">
				<el-row>
					<el-col :span="12">
						<el-form-item label="用户账号" prop="userAccount">
							<el-input class="list_inp" v-model="user.userAccount" readonly placeholder="用户账号" clearable />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="用户密码" prop="userPassword">
							<el-input class="list_inp" v-model="user.userPassword" readonly placeholder="用户密码" clearable />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="用户姓名" prop="userName">
							<el-input class="list_inp" v-model="user.userName"  placeholder="用户姓名" clearable />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="头像" prop="headportrait">
							<uploads
								action="file/upload" 
								tip="请上传头像" 
								:limit="3" 
								style="width: 100%;text-align: left;"
								:fileUrls="user.headportrait?user.headportrait:''" 
								@change="userheadportraitUploadSuccess">
							</uploads>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="性别" prop="gender">
							<el-select 
								class="list_sel" 
								v-model="user.gender" 
								placeholder="请选择性别"
								>
								<el-option v-for="item in usergenderLists" :label="item" :value="item"></el-option>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="电话号码" prop="phoneNumber">
							<el-input class="list_inp" v-model="user.phoneNumber"  placeholder="电话号码" clearable />
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
	const tableName = ref('user')
	const user = ref({})
	const usergenderLists = ref([])
	const init = () => {
		usergenderLists.value = "男,女".split(',')
	}
	const userheadportraitUploadSuccess=(fileUrls)=> {
	    user.value.headportrait = fileUrls;
	}
	const onSubmit = () => {
		if((!user.value.userAccount)){
			context?.$toolUtil.message(`用户账号不能为空`,'error')
			return false
		}
		if((!user.value.userPassword)){
			context?.$toolUtil.message(`用户密码不能为空`,'error')
			return false
		}
		if((!user.value.userName)){
			context?.$toolUtil.message(`用户姓名不能为空`,'error')
			return false
		}
		if(user.value.headportrait!=null){
			user.value.headportrait = user.value.headportrait.replace(new RegExp(context?.$config.url,"g"),"");
		}
		if((user.value.phoneNumber)&&(!context?.$toolUtil.isMobile(user.value.phoneNumber))){
			context?.$toolUtil.message(`电话号码应输入手机格式`,'error')
			return false
		}
		if((user.value.money)&&(!context?.$toolUtil.isNumber(user.value.money))){
			context?.$toolUtil.message(`余额应输入数字`,'error')
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
			//下拉框样式
			.list_sel {
				//去掉默认样式
				.select-trigger{
					height: 100%;
					.el-input{
						height: 100%;
					}
				}
			}
			//图片上传样式
			.el-upload-list  {
				//提示语
				.el-upload__tip {
				}
				//外部盒子
				.el-upload--picture-card {
					//图标
					.el-icon{
					}
				}
				.el-upload-list__item {
				}
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
