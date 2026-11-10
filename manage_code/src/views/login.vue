<template>
	<div>
		<div class="login_view">
			<div class="outTitle_view">
				<el-image class="outTitle_img" src="http://clfile.zggen.cn/20240919/adbb126f981544b496bf576a0105c615.png" fit="cover"></el-image>
				<div class="outTilte">农产品销售平台登录</div>
			</div>
			<el-form :model="loginForm" class="login_form">
				<div class="list_item" v-if="loginType==1">
					<div class="list_label">
						账号：
					</div>
					<input class="list_inp" v-model="loginForm.username" placeholder="请输入账号" name="username" />
				</div>
				<div class="list_item" v-if="loginType==1">
					<div class="list_label">
						密码：
					</div>
					<input class="list_inp" v-model="loginForm.password" type="password" placeholder="请输入密码" @keydown.enter.native="handleLogin"  />
				</div>
				<div class="list_type" v-if="userList.length>1">
					<div class="list_label">
						用户类型：
					</div>
				  <el-select v-model="loginForm.role" placeholder="请选择用户类型">
				    <el-option v-for="(item,index) in userList" :label="item.roleName" :value="item.roleName"></el-option>
				  </el-select>
				</div>
				<div class="remember_view" v-if="loginType==1">
					<el-checkbox v-model="rememberPassword" label="记住密码" size="large" :true-label="true"
						:false-label="false" />
				</div>
				<div class="btn_view">
					<el-button class="login" v-if="loginType==1" type="success" @click="handleLogin">登录</el-button>
					<el-button class="register" type="primary" @click="handleRegister('supplier')">注册供应商</el-button>
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
	} from "vue";
	import { useStore } from 'vuex'
	const store = useStore()
	const userList = ref([])
	const menus = ref([])
	const loginForm = ref({
		role: '',
		username: '',
		password: ''
	})
	const tableName = ref('')
	const loginType = ref(1)
	//是否记住密码
	const rememberPassword = ref(true)
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	//注册
    const handleRegister = (tableName) => {
    	context?.$router.push(`/${tableName}Register`)
    	
    }
	const handleLogin = () => {
		if (!loginForm.value.username) {
			context?.$toolUtil.message('请输入用户名', 'error')
			
			return;
		}
		if (!loginForm.value.password) {
			context?.$toolUtil.message('请输入密码', 'error')
			return;
		}
		if (userList.value.length > 1) {
			if (!loginForm.value.role) {
				context?.$toolUtil.message('请选择角色', 'error')
				return;
			}
			for (let i = 0; i < menus.value.length; i++) {
				if (menus.value[i].roleName == loginForm.value.role) {
					tableName.value = menus.value[i].tableName;
				}
			}
		} else {
			tableName.value = userList.value[0].tableName;
			loginForm.value.role = userList.value[0].roleName;
		}
		doLogin()
	}
	const doLogin = async () => {
		try {
			const res = await context?.$http({
				url: `${tableName.value}/login?username=${loginForm.value.username}&password=${loginForm.value.password}`,
				method: 'post'
			})
			//是否保存当前账号密码至缓存
			if (rememberPassword.value) {
				let loginForm1 = JSON.parse(JSON.stringify(loginForm.value))
				delete loginForm1.code
				context?.$toolUtil.storageSet("loginForm", JSON.stringify(loginForm1));
			} else {
				context?.$toolUtil.storageRemove("loginForm")
			}
			context?.$toolUtil.storageSet("Token", res.data.token);
			context?.$toolUtil.storageSet("role", loginForm.value.role);
			context?.$toolUtil.storageSet("sessionTable", tableName.value);
			context?.$toolUtil.storageSet("adminName", loginForm.value.username);
			// 修复：加了 try/catch，防止 getSession 失败时 uncaught rejection 弹 overlay
			try {
				await store.dispatch('user/getSession')
			} catch (e) {
				console.warn('getSession failed:', e)
			}
			context?.$router.push('/')
		} catch (err) {
			// http.js 已经弹过 ElMessage.error 了，这里不重复弹
			console.warn('login failed:', err)
		}
	}
	//获取菜单
	const getMenu= async ()=> {
      let params = {
        page: 1,
        limit: 1,
        sort: 'id',
      }

      try {
        const res = await context?.$http({
			url: "menu/list",
			method: "get",
			params: params
		})
		menus.value = JSON.parse(res.data.data.list[0].menujson)
		for (let i = 0; i < menus.value.length; i++) {
			if (menus.value[i].hasBackLogin=='是') {
				userList.value.push(menus.value[i])
			}
		}
		//获取缓存是否有保存的账号密码
		let form = context?.$toolUtil.storageGet('loginForm')
		if (form) {
			
		}else {
			loginForm.value.role = userList.value[0].roleName
		}
        context?.$toolUtil.storageSet("menus", JSON.stringify(menus.value));
      } catch (e) {
          // 修复：401 或其他错误时不要让 Promise unhandled
          console.warn('getMenu failed:', e)
      }
    }
	//初始化
	const init = () => {
		getMenu();
		//获取缓存是否有保存的账号密码
		let form = context?.$toolUtil.storageGet('loginForm')
		if (form) {
			loginForm.value = JSON.parse(form)
		}
	}
	onMounted(()=>{
		init()
		

	})
</script>

<style lang="scss" scoped>
	.login_view {
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
		.login_form {
		}
		// item盒子
		.list_item {
			// label
			.list_label {
			}
			// 输入框
			.list_inp {
			}
		}
		.list_type {
			.list_label {
			}
			// 下拉框样式
			:deep(.el-select) {
				//去掉默认样式
				.select-trigger{
					height: 100%;
					.el-input{
						height: 100%;
					}
				}
			}
		}
		// 记住密码样式
		.remember_view {
		
			// 未选中样式
			:deep(.el-checkbox) {
				// 复选框
				.el-checkbox__inner {
				}
				// 提示文字
				.el-checkbox__label{
				}
			}
			// 选中样式
			:deep(.is-checked) {
				//复选框
				.el-checkbox__inner {
				}
				// 提示文字
				.el-checkbox__label{
				}
			}
		}
		// 按钮盒子
		.btn_view {
			// 登录
			.login {
			}
			// 注册
			.register {
			}
		}
	}

</style>
<style>
.login_view {
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

.login_view .outTitle_view img.el-image__inner {
    width: 78px;
    height: 78px;
}

.login_view .outTilte {
    color: #FFFFFF;
    line-height: 26px;
    font-size: 26px;
    margin-top: 30px;
}

.login_view .outTitle_view {
    text-align: center;
}

.login_view .login_form {
    width: 710px;
    background: #F6F6F6;
    border-radius: 60px 60px 0px 0px;
    margin-top: 40px;
    padding: 80px 80px 30px;
    display: flex;
    flex-wrap: wrap;
}

.login_view .list_item {
    background: #fff;
    display: flex;
    padding: 0;
    width: 100%;
    height: 50px;
    margin-bottom: 40px;
    border-radius: 5px;
    order: 1;
}

.login_view .list_label {
    line-height: 50px;
    position: relative;
    margin-right: 10px;
    width: 90px;
    flex-shrink: 0;
    text-align: center;
}

.login_view input.list_inp {
    height: 50px;
    flex: 1;
    border: none;
}

.login_view .list_label:after,.login_view  .listCode_label:after {
    content: '';
    position: absolute;
    right: 0;
    height: 30px;
    width: 2px;
    background: linear-gradient( 180deg, #FFFFFF 0%, #6F9867 46%, rgba(111,152,103,0) 100%);
    top: 10px;
}

.login_view .list_type {
    width: 100%;
    background: #fff;
    height: 50px;
    display: flex;
    padding: 0;
    margin-bottom: 40px;
    border-radius: 5px;
    order: 2;
}

.login_view .list_type .el-select {
    flex: 1;
}

.login_view .listCode_view {
    width: 100%;
    height: 50px;
    display: flex;
    padding: 0;
    margin-bottom: 40px;
    order: 3;
}

.login_view .listCode_label {
    line-height: 50px;
    width: 90px;
    text-align: center;
    position: relative;
    background: #fff;
    border-radius: 5px 0 0 5px;
}

.login_view input.listCode_inp {
    background: #fff;
    border: none;
    padding-left: 10px;
    width: 250px;
    margin-right: 10px;
    border-radius: 0 5px 5px 0;
}

.login_view .listCode_btn {
    flex: 1;
    background: var(--theme);
    text-align: center;
    line-height: 50px;
    border-radius: 5px;
}

.login_view .btn_view {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    row-gap: 30px;
    justify-content: center;
    column-gap: 20px;
    order: 4;
}

.login_view .el-button.login {
    background: var(--theme);
    border: none;
    width: 336px;
    color: #fff;
    height: 50px;
    order: 1;
}

.login_view .remember_view {display: none;}

.login_view .el-button.register {
    order: 3;
    border: 1px solid var(--theme);
    background: #fff;
    color: var(--theme);
    height: 50px;
    width: 122px;
    margin-left: 0;
}

.login_view .el-button.forget {
    width: 194px;
    order: 2;
    background: #DADEDA;
    height: 50px;
    border-radius: 5px;
    color: var(--theme);
    margin: 0;
}

.login_view .face {
    text-align: center;
    width: 100%;
    margin-top: 30px;
    order: 4;
}

.login_view .el-button {
    font-size: 16px;
}
.login_view .listCode_btn span {
    font-size: 22px!important;
}
.login_view .el-select,.login_view .el-input {
  border: none;
}
</style>