<template>
	<div>
		<div class="register_view">
			<div class="outTitle_view">
				<el-image class="outTitle_img" src="http://clfile.zggen.cn/20240921/f930544101f94cbba9f175d2472cba0d.png" fit="cover"></el-image>
				<div class="outTilte">{{projectName}}注册</div>
			</div>
			<el-form :model="registerForm" class="register_form">
				<div class="list_item">
					<div class="list_label">账号：</div>
					<el-input class="list_inp"
						 v-model="registerForm.account" 
						 placeholder="请输入账号"
						 type="text"
						/>
				</div>
				<div class="list_item">
					<div class="list_label">密码：</div>
					<el-input class="list_inp"
						 v-model="registerForm.password" 
						 placeholder="请输入密码"
						 type="password"
						 />
				</div>
				<div class="list_item">
					<div class="list_label">确认密码：</div>
					<el-input class="list_inp" v-model="registerForm.password2" type="password" placeholder="请输入确认密码" />
				</div>
				<div class="list_item">
					<div class="list_label">名称：</div>
					<el-input class="list_inp"
						 v-model="registerForm.name" 
						 placeholder="请输入名称"
						 type="text"
						/>
				</div>
				<div class="list_item">
					<div class="list_label">联系电话：</div>
					<el-input class="list_inp"
						 v-model="registerForm.contactPhone" 
						 placeholder="请输入联系电话"
						 type="text"
						/>
				</div>
				<div class="list_item">
					<div class="list_label">地址：</div>
					<el-input class="list_inp"
						 v-model="registerForm.address" 
						 placeholder="请输入地址"
						 type="text"
						/>
				</div>
				<div class="list_btn">
					<el-button class="register" type="success" @click="handleRegister">注册</el-button>
					<div class="r-login" @click="close">已有账号，直接登录</div>
				</div>
			</el-form>
		</div>
	</div>
</template>
<script setup>
	import {
		ref,
		getCurrentInstance,
		nextTick,
		onMounted,
	} from 'vue';
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const projectName = context?.$project.projectName
	//获取注册类型
	import { useRoute } from 'vue-router';
	const route = useRoute()
	const tableName = ref('supplier')
	
	//公共方法
	const getUUID=()=> {
		return new Date().getTime();
	}
	const registerForm = ref({
	})
	const init=()=>{
	}
	// 多级联动参数
	//注册按钮
	const handleRegister = () => {
		let url = tableName.value +"/register";
		if((!registerForm.value.account)){
			context?.$toolUtil.message(`账号不能为空`,'error')
			return false
		}
		if((!registerForm.value.password)){
			context?.$toolUtil.message(`密码不能为空`,'error')
			return false
		}
		if(registerForm.value.password!=registerForm.value.password2){
			context?.$toolUtil.message('两次密码输入不一致','error')
			return false
		}
		if((!registerForm.value.name)){
			context?.$toolUtil.message(`名称不能为空`,'error')
			return false
		}
		if(registerForm.value.contactPhone&&(!context?.$toolUtil.isMobile(registerForm.value.contactPhone))){
			context?.$toolUtil.message(`联系电话应输入手机格式`,'error')
			return false
		}
		context?.$http({
			url:url,
			method:'post',
			data:registerForm.value
		}).then(res=>{
			context?.$toolUtil.message('注册成功','success', obj=>{
				context?.$router.push({
					path: "/login"
				});
			})
		})
	}
	//返回登录
	const close = () => {
		context?.$router.push({
			path: "/login"
		});
	}
	init()
	onMounted(()=>{

	})
</script>
<style lang="scss" scoped>
	
	.register_view {
        background-image: url("http://clfile.zggen.cn/20240921/fb6feddfbb544df593bbf3240e3437db.webp");
		// 标题盒子
		.outTitle_view {
			// 标题图片
			.outTitle_img {
			}
			.outTilte {
			}
		}
		// 表单盒子
		.register_form {
		}
		// item盒子
		.list_item {
			// label
			.list_label {
			}
			// 输入框
			:deep(.list_inp) {
			}
		}
		//按钮盒子
		.list_btn {
			//注册按钮
			.register {
			}
			//注册按钮悬浮样式
			.register:hover {
			}
			//已有账号
			.r-login {
			}
		}
	}
</style>
<style>
.register_view {
    min-height: 100vh;
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-position: center center;
    background-size: 100% 100% !important;
    background-repeat: no-repeat;
    background-attachment: fixed;
    background-origin: initial;
    background-clip: initial;
    background-color: initial;
}



.register_view .register_form{
    margin: 40px 0 0;
    position: relative;
    padding: 80px;
    width: 710px;
    background: #F6F6F6;
    border-radius: 60px 60px 0 0;
}


.register_view .register_form .list_item {
    border-radius: 5px;
    margin: 0 0 30px;
    display: flex;
    padding: 10px 14px;
    align-items: center;
    background: #fff;
}
.register_view .register_form .list_item .list_label{ display:block; margin-right:10px; white-space:nowrap; font-size:16px;  }
.register_view .register_form .list_item .list_label i {  font-size:24px; color:#999;  }
.register_view .register_form .list_item .el-input .el-input__inner{ width:100%; border:none; height:40px; line-height:40px; border-radius:0; font-size:16px; color:#999;   }
.register_view .register_form .list_item .el-select { width:100%; }
.register_view .register_form .list_type{ margin-bottom:20px;  }
.register_view .register_form .list_code{  }
.register_view .register_form .list_code .el-input .el-input__inner{ width:100%; border:none; border-bottom:1px solid #ddd; height:40px; line-height:40px; border-radius:0; font-size:16px; color:#999;   }


.register_view .register_form .list_item .list_file_list{ width: 100%;  margin:10px 20px 10px 0px; flex:1; }

.register_view .register_form .list_item .list_file_list .el-upload{background-color: rgb(255, 255, 255);width: 148px;line-height: 68px;text-align: center;border: 1px solid rgb(221, 221, 221);border-radius: 0px;cursor: pointer;height: 148px;}

.register_view .register_form .list_item .list_file_list .el-upload .el-icon{font-size: 26px;color: rgb(187, 187, 187);line-height: 148px;}

.register_view .register_form .list_item .list_file_list .img-uploader .el-upload__tip{ font-size: 16px;  color: rgb(102, 102, 102);margin: 7px 0px 0px; display: flex; align-items: center;  justify-content: flex-start; }


.register_view .list_item .upload-demo {
    width: 350px;
}
.register_view .register_form .list_item .el-upload-dragger {
    background-color: #fff;
    border: 1px solid #ddd;
    border-radius: 0;
    box-sizing: border-box;
    width: 100%;
    height: auto;
    text-align: center;
    cursor: pointer;
    overflow: hidden;
    padding: 40px 20px;
}

.register_view .register_form .list_item .el-upload-dragger .el-icon--upload{ font-size: 48px; color: var(--theme); line-height: 40px;  margin: 0px; }

.register_view .register_form .list_item .upload-demo .el-upload__tip{ font-size: 16px; color: rgb(102, 102, 102); margin: 7px 0px 0px; }

.register_view .register_form .list_item .el-upload-dragger .el-upload__text{  font-size: 16px;  }
.register_view .register_form .list_item .el-upload-dragger .el-upload__text em{ color: var(--theme); }


.register_view .register_form .list_item .list_radio{ display: flex; width: 80%; align-items: center; flex-wrap: wrap; background: none; height: 36px; flex: 1; }

.register_view .register_form .list_item .list_radio .el-radio{ width: auto; margin: 0px 20px 0px 0px; display: flex; justify-content: center; align-items: center; }

.register_view .register_form .list_item .list_radio .el-radio .el-radio__label { font-size: 16px; color: rgb(153, 153, 153);   }

.register_view .register_form .list_item .list_radio .el-radio.is-checked .el-radio__label { color:var(--theme);   }

.register_view .register_form .list_item .list_radio .el-radio .el-radio__input .el-radio__inner{ border-color: rgb(153, 153, 153); background: rgb(255, 255, 255); }

.register_view .register_form .list_item .list_radio .el-radio .el-radio__input.is-checked .el-radio__inner{ border-color: var(--theme); background: var(--theme);  }


.register_view .register_form .list_item .list_checkbox { display: flex; width: 80%;  flex-wrap: wrap; align-items: center; background: none; height: 36px; flex:1; }

.register_view .register_form .list_item .list_checkbox .el-checkbox{ width: auto; margin: 0px 20px 0px 0px; display: flex;  justify-content: center;  align-items: center; }

.register_view .register_form .list_item .list_checkbox .el-checkbox .el-checkbox__label { color: rgb(153, 153, 153);  font-size: 16px; }

.register_view .register_form .list_item .list_checkbox .el-checkbox.is-checked .el-checkbox__label { color: var(--theme); }

.register_view .register_form .list_item .list_checkbox .el-checkbox .el-checkbox__input .el-checkbox__inner{ border-color: rgb(153, 153, 153);  background: rgb(255, 255, 255); }

.register_view .register_form .list_item .list_checkbox .el-checkbox.is-checked .el-checkbox__input .el-checkbox__inner{ border-color: var(--theme); background: var(--theme); }


.register_view .register_form .list_code{display: flex;align-items: center;width: 100%;padding: 10px 14px;background: #fff;border-radius: 5px;}
.register_view .register_form .list_code .list_code_label{ font-size:16px; }

.register_view .register_form .list_code .list_code_item{ width: calc(100% - 130px); display: flex; align-items: center; flex: 1;  }

.register_view .register_form .list_code .list_code_item .list_code_inp{  font-size:16px; color:#999;  }

.register_view .register_form .list_code .list_code_item .list_code_btn{width: 150px;height: 36px;line-height: 36px;padding: 0px;border: 0px solid rgb(73, 189, 227);background: var(--theme);border-radius: 0px;color: rgb(255, 255, 255);font-size: 16px;border-radius: 5px;}


.register_view .register_form .list_btn{text-align:center;display: flex;align-items: center;margin-top: 80px;}
.register_view .register_form .list_btn .register{
    background: var(--theme);
    border: none;
    height: 50px;
    font-size: 20px;
    margin: 0;
    width: 320px;
    border-radius: 5px;
} 
.register_view .register_form .list_btn .r-login{font-size: 16px;color: #999;background: #DADEDA;border-radius: 5px;height: 50px;flex: 1;margin-left: 20px;line-height: 50px;color: var(--theme);} 
.register_view .register_form .list_btn .r-login:hover{ cursor:pointer; color: var(--theme);  } 

.register_view img.el-image__inner {}

.register_view .el-image.outTitle_img {
    width: 78px;
    height: 78px;
    margin-bottom: 30px;
}

.register_view .outTitle_view {
    text-align: center;
    color: #fff;
    font-size: 26px;
    line-height: 26px;
    margin-top: 6vh;
}
.register_view .el-select,.register_view .el-input {
  border: none;
}

</style>