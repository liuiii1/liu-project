<template>
	<div>
		<div class="forget_view">
			<el-form :model="forgetForm" class="forget_box">
				<div class="forget_title">农产品销售平台{{pageType==1?'找回密码':pageType==2?'输入密保':'重置密码'}}</div>
				<div class="tab_view">
					<div class="tab" :class="pageType==1?'tab_active':''">找回密码</div>
					<div class="tab" :class="pageType==2?'tab_active tab_active1':''"> 输入密保</div>
					<div class="tab" :class="pageType==3?'tab_active tab_active1 tab_active2':''">重置密码</div>
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
    import { useRoute } from 'vue-router'
    const route = useRoute()
	const context = getCurrentInstance()?.appContext.config.globalProperties;
    onMounted(()=>{
    })
	const pageType = ref(1)
	const forgetForm = ref({})
	const userForm = ref({})
	//返回登录
	const close = () => {
		context?.$router.push({
			path: "/login"
		});
	}
</script>

<style lang="scss" scoped>
	.forget_view {
        background-image: url("http://clfile.zggen.cn/20240921/fb6feddfbb544df593bbf3240e3437db.webp");
		// 表单盒子
		.forget_box {
			// 标题
			.forget_title {
			}
			// tab
			// 盒子
			.tab_view {
				padding: 0 0 50px;
				display: flex;
				width: 100%;
				align-items: center;
				// 默认样式
				.tab {
					padding: 16px 0;
					width: calc(100% / 3);
					font-size: 16px;
					box-sizing: border-box;
					text-align: center;
				}
				// 选中样式一
				.tab_active {
					clip-path: polygon(00% 0%, 92% 00%, 100% 50%, 92% 100%, 0 100%, 8% 50%);
					color: #fff;
					background: var(--theme);
				}
				// 选中样式二
				.tab_active1 {
					color: #fff;
					background: var(--theme);
				}
				// 选中样式三
				.tab_active2 {
					color: #fff;
					background: var(--theme);
				}
			}
			// item
			.list_item {
				// label
				.item_label {
				}
				// 输入框
				:deep(.list_inp) {
				}
				//下拉框样式
				:deep(.list_sel) {
					//去掉默认样式
					.select-trigger{
						height: 100%;
						.el-input{
							height: 100%;
						}
					}
				}
			}
			// 按钮盒子
			.list_btn {
				// 获取密保
				:deep(.el-button--success) {
				}
				// 获取密保悬浮
				:deep(.el-button--success:hover) {
				}
				// 确认密保
				:deep(.el-button--primary) {
				}
				// 确认密保悬浮
				:deep(.el-button--primary:hover) {
				}
				// 重置密码
				:deep(.el-button--warning) {
				}
				// 重置密码悬浮
				:deep(.el-button--warning:hover) {
				}
				.r-login {
				}
			}
		}
	}

	
</style>
<style>
.forget_view {
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


.forget_view .forget_box{
    margin: 15vh 0;
    background: #FFFFFF;
    border-radius: 0px 0px 0px 0px;
    border: 5px solid var(--theme);
    position: relative;
    padding: 50px 80px 30px;
    width: 630px;
}


.forget_view .forget_title{
   width: 100%;
   float: left;
   line-height: 46px;
   font-size:24px;
   font-weight: 600;
   letter-spacing: 2px;
   color:var(--theme);
   position: relative;
   margin-bottom:20px;
}


.forget_view .list_item{  display: flex; align-items: center;  width: 100%; justify-content: flex-start; margin: 10px 0px;  }
.forget_view .list_item .item_label { margin-right:10px; white-space: nowrap;  font-size: 16px; }
.forget_view .list_item .item_label i {  font-size:24px; color:#999;  }
.forget_view .list_item .list_inp{ width:100%; border:none; border-bottom:1px solid #ddd; height:40px; line-height:40px; font-size: 16px;  }
.forget_view .list_item .el-select{  width:100%; border-bottom:1px solid #ddd; height:40px; line-height:40px; font-size: 16px;  color:#999;  }
.forget_view .list_item .el-select .el-input__inner{ font-size: 16px;  color:#999;  }

.forget_view .list_type{ margin-bottom:20px;  }


.forget_view .list_btn{  text-align:center;  }

.forget_view .list_btn .get_btn{ width: 100%; height: 50px; line-height: 46px; background: #fff; border: 3px solid #ccc; font-weight: 600; font-size: 20px; color: #999; margin-bottom:20px; padding:0;  margin-top:20px; color: var(--theme); border-color: var(--theme);} 

.forget_view .list_btn .valid_btn{ width: 100%; height: 50px; line-height: 46px; background: #fff; border: 3px solid #ccc; font-weight: 600; font-size: 20px; color: #999; margin-bottom:20px; padding:0;  margin-top:20px; } 
.forget_view .list_btn .valid_btn:hover { color: var(--theme); border-color: var(--theme); } 

.forget_view .list_btn .update_btn{ width: 100%; height: 50px; line-height: 46px; background: #fff; border: 3px solid #ccc; font-weight: 600; font-size: 20px; color: #999; margin-bottom:20px; padding:0;  margin-top:20px; } 
.forget_view .list_btn .update_btn:hover { color: var(--theme); border-color: var(--theme); } 

.forget_view .list_btn .r-login{  font-size: 16px; color: #999;  } 
.forget_view .list_btn .r-login:hover{ cursor:pointer; color: var(--theme);  } 
.forget_view .el-select,.forget_view .el-input {
  border: none;
}

</style>