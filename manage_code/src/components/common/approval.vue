<template>
	<div>
		<el-dialog class="edit_form" v-model="approvalVisible" :title="'审核'" width="70%" destroy-on-close>
			<el-form ref="ruleFormRef" :model="approvalForm" label-width="120px" :rules="rules">
				<el-row>
					<el-col :span="24" v-if="approvalForm.shhf && approvalForm.shhf.startsWith('用户申请')">
						<el-form-item label="用户申请">
							<div style="padding:10px 16px;background:#f5f7fa;border-radius:6px;color:#606266;font-size:14px;line-height:1.6;white-space:pre-wrap;">
								{{ approvalForm.shhf }}
							</div>
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item :label="approvalForm.shhf && approvalForm.shhf.startsWith('用户申请') ? '审核回复' : '审核回复'" prop="_auditReply">
							<el-input v-model="approvalForm._auditReply" type="textarea"
								:placeholder="approvalForm.shhf && approvalForm.shhf.startsWith('用户申请') ? '请输入审核回复（可选）' : '请输入审核回复'" />
						</el-form-item>
					</el-col>

				</el-row>
			</el-form>
			<template #footer>
				<span class="dialog-footer" v-if="sfshType==1">
					<el-button type="danger" @click="approvalSave('否')">拒绝</el-button>
					<el-button type="primary" @click="approvalSave('是')">
						通过
					</el-button>
				</span>
				<span class="dialog-footer" v-if="sfshType==2">
					<el-button type="danger" @click="approvalVisible=false">取消</el-button>
					<el-button type="primary" @click="approvalSave()">
						回复
					</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
	import {
		ref,
		toRefs,
		getCurrentInstance,
		defineEmits
	} from 'vue';
	const emit = defineEmits(['shChange'])
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	//props
	const props = defineProps({
		tableName: String
	})
	const {
		tableName
	} = toRefs(props)
	//props

	const rules = ref({
		_auditReply: [{
			required: false,
			message: '请输入审核回复',
			trigger: 'blur'
		}, ],
	})
	const approvalForm = ref({})
	const approvalVisible = ref(false)
	//ref
	const ruleFormRef = ref(null)
	const sfshType = ref(1)

	const approvalClick = (row,type=1) => {
		sfshType.value = type
		approvalForm.value = JSON.parse(JSON.stringify(row))
		approvalVisible.value = true
	}

	//声明父级调用
	defineExpose({
		approvalClick
	})
	const approvalSave = (type) => {
		if(sfshType.value == 1){
			approvalForm.value.sfsh = type
		}
		ruleFormRef.value.validate((valid) => {
			if (valid) {
				// 如果已有用户申请理由，把管理员的审核回复追加到后面（用换行分隔）
				const originalShhf = approvalForm.value.shhf || ''
				const auditReply = approvalForm.value._auditReply || ''
				let finalShhf = ''
				if (originalShhf.startsWith('用户申请') && auditReply) {
					finalShhf = originalShhf + '\n管理员回复：' + auditReply
				} else if (auditReply) {
					finalShhf = auditReply
				} else {
					finalShhf = originalShhf
				}
				approvalForm.value.shhf = finalShhf
				delete approvalForm.value._auditReply

				let url = `${tableName.value}/update`
				context?.$http({
					url: url,
					method: 'post',
					data: approvalForm.value
				}).then(res => {

					context?.$toolUtil.message('审核成功', 'success', obj => {
						approvalVisible.value = false
					})
					emit('shChange',type,approvalForm.value)
				})
			}
		})
	}
</script>

<style>
</style>
