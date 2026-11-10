<template>
	<div>
		<el-dialog modal-class="edit_form_modal" class="edit_form" v-model="formVisible" :title="formTitle" width="80%" destroy-on-close :fullscreen='false'>
			<el-form class="formModel_form" ref="formRef" :model="form" :rules="rules">
				<el-row>
					<el-col :span="12">
						<el-form-item label="产品名称" prop="productname">
							<el-input class="list_inp" v-model="form.productname" placeholder="产品名称"
								 type="text" 								:readonly="!isAdd||disabledForm.productname?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="12">
						<el-form-item label="产品分类" prop="productcategory">
							<el-select
								class="list_sel"
								:disabled="!isAdd||disabledForm.productcategory?true:false"
								v-model="form.productcategory"
								placeholder="请选择产品分类"
								>
								<el-option v-for="(item,index) in productcategoryLists" :label="item"
									:value="item"
									>
								</el-option>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item prop="image"
									  label="图片"
						>
							<uploads
								:disabled="!isAdd||disabledForm.image?true:false"
								action="file/upload"

								tip="请上传图片"
								:limit="3"
								style="width: 100%;text-align: left;"
								:fileUrls="form.image?form.image:''"
								@change="imageUploadSuccess">
							</uploads>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="规格" prop="specification">
							<el-input class="list_inp" v-model="form.specification" placeholder="规格"
								 type="text" 								:readonly="!isAdd||disabledForm.specification?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="12">
						<el-form-item label="出产地" prop="placeofproduction">
							<el-input class="list_inp" v-model="form.placeofproduction" placeholder="出产地"
								 type="text" 								:readonly="!isAdd||disabledForm.placeofproduction?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="12">
						<el-form-item label="账号" prop="account">
							<el-input class="list_inp" v-model="form.account" placeholder="账号"
								 type="text" 								:readonly="!isAdd||disabledForm.account?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="12">
						<el-form-item label="名称" prop="name">
							<el-input class="list_inp" v-model="form.name" placeholder="名称"
								 type="text" 								:readonly="!isAdd||disabledForm.name?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="12">
						<el-form-item label="价格" prop="price">
							<el-input class="list_inp" v-model.number="form.price" placeholder="价格"
								 type="number" 								:readonly="!isAdd||disabledForm.price?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="12">
						<el-form-item label="单限" prop="onelimittimes">
							<el-input class="list_inp" v-model.number="form.onelimittimes" placeholder="单限"
								 type="text" 								:readonly="!isAdd||disabledForm.onelimittimes?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="12">
						<el-form-item label="库存" prop="alllimittimes">
							<el-input class="list_inp" v-model.number="form.alllimittimes" placeholder="库存"
								 type="text" 								:readonly="!isAdd||disabledForm.alllimittimes?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="产品详情" prop="productdetails">
							<editor :value="form.productdetails" placeholder="请输入产品详情" :readonly="!isAdd||disabledForm.productdetails?true:false"
								class="list_editor" @change="(e)=>editorChange(e,'productdetails')"></editor>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form>
			<template #footer v-if="isAdd||type=='logistics'||type=='reply'">
				<span class="formModel_btn_box">
					<el-button class="cancel_btn" @click="closeClick">取消</el-button>
					<el-button class="confirm_btn" type="primary" @click="save"
						>
						提交
					</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>
<script setup>
	import {
		reactive,
		ref,
		getCurrentInstance,
		nextTick,
		computed,
		defineEmits
	} from 'vue'
    import {
        useStore
    } from 'vuex';
    const store = useStore()
    const user = computed(()=>store.getters['user/session'])
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const emit = defineEmits(['formModelChange'])
	//基础信息
	const tableName = 'agriculturalproductinformation'
	const formName = '农产品信息'
	//基础信息
	//form表单
	const form = ref({})
	const disabledForm = ref({
		productname : false,
		productcategory : false,
		image : false,
		specification : false,
		placeofproduction : false,
		productdetails : false,
		account : false,
		name : false,
		storeupNumber : false,
		discussNumber : false,
		price : false,
		onelimittimes : false,
		alllimittimes : false,
	})
	const formVisible = ref(false)
	const isAdd = ref(false)
	const formTitle = ref('')
	//表单验证
	//匹配整数
	const validateIntNumber = (rule, value, callback) => {
		if (!value) {
			callback();
		} else if (!context?.$toolUtil.isIntNumer(value)) {
			callback(new Error("请输入整数"));
		} else {
			callback();
		}
	}
	//匹配数字
	const validateNumber = (rule, value, callback) => {
		if(!value){
			callback();
		} else if (!context?.$toolUtil.isNumber(value)) {
			callback(new Error("请输入数字"));
		} else {
			callback();
		}
	}
	//匹配手机号码
	const validateMobile = (rule, value, callback) => {
		if(!value){
			callback();
		} else if (!context?.$toolUtil.isMobile(value)) {
			callback(new Error("请输入正确的手机号码"));
		} else {
			callback();
		}
	}
	//匹配电话号码
	const validatePhone = (rule, value, callback) => {
		if(!value){
			callback();
		} else if (!context?.$toolUtil.isPhone(value)) {
			callback(new Error("请输入正确的电话号码"));
		} else {
			callback();
		}
	}
	//匹配邮箱
	const validateEmail = (rule, value, callback) => {
		if(!value){
			callback();
		} else if (!context?.$toolUtil.isEmail(value)) {
			callback(new Error("请输入正确的邮箱地址"));
		} else {
			callback();
		}
	}
	//匹配身份证
	const validateIdCard = (rule, value, callback) => {
		if(!value){
			callback();
		} else if (!context?.$toolUtil.checkIdCard(value)) {
			callback(new Error("请输入正确的身份证号码"));
		} else {
			callback();
		}
	}
	//匹配网站地址
	const validateUrl = (rule, value, callback) => {
		if(!value){
			callback();
		} else if (!context?.$toolUtil.isURL(value)) {
			callback(new Error("请输入正确的URL地址"));
		} else {
			callback();
		}
	}
	const rules = ref({
		productname: [
		],
		productcategory: [
		],
		image: [
		],
		specification: [
		],
		placeofproduction: [
		],
		productdetails: [
		],
		account: [
		],
		name: [
		],
		storeupNumber: [
			{ validator: validateIntNumber, trigger: 'blur' },
		],
		discussNumber: [
			{ validator: validateIntNumber, trigger: 'blur' },
		],
		price: [
			{ validator: validateNumber, trigger: 'blur' },
		],
		onelimittimes: [
			{ validator: validateIntNumber, trigger: 'blur' },
		],
		alllimittimes: [
			{ validator: validateIntNumber, trigger: 'blur' },
		],
	})
	//表单验证

	const formRef = ref(null)
	const id = ref(0)
	const type = ref('')
	//产品分类列表
	const productcategoryLists = ref([])
	//图片上传回调
	const imageUploadSuccess=(e)=>{
		form.value.image = e
	}
	//methods

	//获取唯一标识
	const getUUID =()=> {
      return new Date().getTime();
    }
	//重置
	const resetForm = () => {
		form.value = {
			productname: '',
			productcategory: '',
			image: '',
			specification: '',
			placeofproduction: '',
			productdetails: '',
			account: '',
			name: '',
		}
	}
	//获取info
	const getInfo = ()=>{
		context?.$http({
			url: `${tableName}/info/${id.value}`,
			method: 'get'
		}).then(res => {
			let reg=new RegExp('../../../file','g')
			res.data.data.productdetails = res.data.data.productdetails?(res.data.data.productdetails.replace(reg,'../../../project/file')):'';
			form.value = res.data.data
			formVisible.value = true
		})
	}
	const crossRow = ref('')
	const crossTable = ref('')
	const crossTips = ref('')
	const crossColumnName = ref('')
	const crossColumnValue = ref('')
	//初始化
	const init=(formId=null,formType='add',formNames='',row=null,table=null,statusColumnName=null,tips=null,statusColumnValue=null)=>{
		resetForm()
		if(formId){
			id.value = formId
			type.value = formType
		}
		if(formType == 'add'){
			isAdd.value = true
			formTitle.value = '新增' + formName
			formVisible.value = true
		}else if(formType == 'info'){
			isAdd.value = false
			formTitle.value = '查看' + formName
			getInfo()
		}else if(formType == 'edit'){
			isAdd.value = true
			formTitle.value = '修改' + formName
			getInfo()
		}
		else if(formType == 'cross'){
			isAdd.value = true
			formTitle.value = formNames
			// getInfo()
			for(let x in row){
				if(x=='productname'){
					form.value.productname = row[x];
					disabledForm.value.productname = true;
					continue;
				}
				if(x=='productcategory'){
					form.value.productcategory = row[x];
					disabledForm.value.productcategory = true;
					continue;
				}
				if(x=='image'){
					form.value.image = row[x];
					disabledForm.value.image = true;
					continue;
				}
				if(x=='specification'){
					form.value.specification = row[x];
					disabledForm.value.specification = true;
					continue;
				}
				if(x=='placeofproduction'){
					form.value.placeofproduction = row[x];
					disabledForm.value.placeofproduction = true;
					continue;
				}
				if(x=='productdetails'){
					form.value.productdetails = row[x];
					disabledForm.value.productdetails = true;
					continue;
				}
				if(x=='account'){
					form.value.account = row[x];
					disabledForm.value.account = true;
					continue;
				}
				if(x=='name'){
					form.value.name = row[x];
					disabledForm.value.name = true;
					continue;
				}
				if(x=='storeupNumber'){
					form.value.storeupNumber = row[x];
					disabledForm.value.storeupNumber = true;
					continue;
				}
				if(x=='discussNumber'){
					form.value.discussNumber = row[x];
					disabledForm.value.discussNumber = true;
					continue;
				}
				if(x=='price'){
					form.value.price = row[x];
					disabledForm.value.price = true;
					continue;
				}
				if(x=='onelimittimes'){
					form.value.onelimittimes = row[x];
					disabledForm.value.onelimittimes = true;
					continue;
				}
				if(x=='alllimittimes'){
					form.value.alllimittimes = row[x];
					disabledForm.value.alllimittimes = true;
					continue;
				}
			}
			if(row){
				crossRow.value = row
			}
			if(table){
				crossTable.value = table
			}
			if(tips){
				crossTips.value = tips
			}
			if(statusColumnName){
				crossColumnName.value = statusColumnName
			}
			if(statusColumnValue){
				crossColumnValue.value = statusColumnValue
			}
			formVisible.value = true
		}

		context?.$http({
			url: `${context?.$toolUtil.storageGet('sessionTable')}/session`,
			method: 'get'
		}).then(res => {
			var json = res.data.data
			if(json.hasOwnProperty('account')&& context?.$toolUtil.storageGet("role")!="管理员"){
				form.value.account = json.account
				disabledForm.value.account = true;
			}
			if(json.hasOwnProperty('name')&& context?.$toolUtil.storageGet("role")!="管理员"){
				form.value.name = json.name
				disabledForm.value.name = true;
			}
		})
		context?.$http({
			url: `option/productcategory/productcategory`,
			method: 'get'
		}).then(res=>{
			productcategoryLists.value = res.data.data
		})
	}
	//初始化
	//声明父级调用
	defineExpose({
		init
	})
	//关闭
	const closeClick = () => {
		formVisible.value = false
	}
	//富文本
	const editorChange = (e,name) =>{
		form.value[name] = e
	}
	//提交
	const save= async ()=>{
		if(form.value.image!=null) {
			form.value.image = form.value.image.replace(new RegExp(context?.$config.url,"g"),"");
		}
		var table = crossTable.value
		var objcross = JSON.parse(JSON.stringify(crossRow.value))
		let crossUserId = ''
		let crossRefId = ''
		let crossOptNum = ''
		if(type.value == 'cross'){
			if(crossColumnName.value!=''){
				if(!crossColumnName.value.startsWith('[')){
					for(let o in objcross){
						if(o == crossColumnName.value){
							objcross[o] = crossColumnValue.value
						}
					}
					//修改跨表数据
					changeCrossData(objcross)
				}else{
					crossUserId = user.value.id
					crossRefId = objcross['id']
					crossOptNum = crossColumnName.value.replace(/\[/,"").replace(/\]/,"")
				}
			}
		}
		formRef.value.validate((valid)=>{
			if(valid){
				if(crossUserId&&crossRefId){
					form.value.crossuserid = crossUserId
					form.value.crossrefid = crossRefId
					let params = {
						page: 1,
						limit: 1000,
						crossuserid:form.value.crossuserid,
						crossrefid:form.value.crossrefid,
					}
					context?.$http({
						url: `${tableName}/page`,
						method: 'get',
						params: params
					}).then(res=>{
						if(res.data.data.total>=crossOptNum){
							context?.$toolUtil.message(`${crossTips.value}`,'error')
							return false
						}else{
							context?.$http({
								url: `${tableName}/${!form.value.id ? "save" : "update"}`,
								method: 'post',
								data: form.value
							}).then(async res=>{
								emit('formModelChange')
								context?.$toolUtil.message(`操作成功`,'success')
                                formVisible.value = false
							})
						}
					})
				}else{
					context?.$http({
						url: `${tableName}/${!form.value.id ? "save" : "update"}`,
						method: 'post',
						data: form.value
					}).then(async (res)=>{
						emit('formModelChange')
						context?.$toolUtil.message(`操作成功`,'success')
                        formVisible.value = false
					})
				}
			}
		})
	}
	//修改跨表数据
	const changeCrossData = async (row)=>{
		await context?.$http({
			url: `${crossTable.value}/update`,
			method: 'post',
			data: row
		}).then(res=>{})
	}
</script>
<style lang="scss" scoped>
	// 表单
	.formModel_form{
		// form item
		:deep(.el-form-item) {
			//label
			.el-form-item__label {
			}
			// 内容盒子
			.el-form-item__content {
				// 输入框
				.list_inp {
				}
				// 下拉框
				.list_sel {
					//去掉默认样式
					.select-trigger{
						height: 100%;
						.el-input{
							height: 100%;
						}
					}
				}
				// 富文本
				.list_editor {
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
		}
	}
	// 按钮盒子
	.formModel_btn_box {
		.cancel_btn {
		}
		.cancel_btn:hover {
		}

		.confirm_btn {
		}
		.confirm_btn:hover {
		}
	}
</style>
