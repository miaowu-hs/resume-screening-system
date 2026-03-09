<template>
  <div class="position-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>岗位列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增岗位
          </el-button>
        </div>
      </template>

      <el-table :data="positions" v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="岗位名称" width="150" />
        <el-table-column prop="department" label="部门" width="120" />
        <el-table-column prop="skills" label="技能要求" min-width="200">
          <template #default="{ row }">
            <el-tag v-for="skill in parseSkills(row.skills)" :key="skill" size="small" style="margin-right: 5px">
              {{ skill }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="experienceMin" label="年限要求" width="100">
          <template #default="{ row }">
            {{ row.experienceMin }}年以上
          </template>
        </el-table-column>
        <el-table-column prop="salaryRange" label="薪资范围" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '招聘中' : '已关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑岗位' : '新增岗位'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="岗位名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-input v-model="form.department" placeholder="请输入部门" />
        </el-form-item>
        <el-form-item label="技能要求" prop="skills">
          <el-input v-model="skillsInput" placeholder="多个技能用逗号分隔" />
        </el-form-item>
        <el-form-item label="年限要求">
          <el-input-number v-model="form.experienceMin" :min="0" :max="20" />
          <span style="margin-left: 10px">年</span>
        </el-form-item>
        <el-form-item label="薪资范围">
          <el-input v-model="form.salaryRange" placeholder="如: 15k-25k" />
        </el-form-item>
        <el-form-item label="岗位要求">
          <el-input v-model="form.requirements" type="textarea" :rows="4" placeholder="请输入详细岗位要求" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getPositions, createPosition, updatePosition, deletePosition } from '../api/position'

const loading = ref(false)
const positions = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const form = reactive({
  id: null,
  title: '',
  department: '',
  skills: '',
  experienceMin: 0,
  salaryRange: '',
  requirements: '',
  status: 1
})

const skillsInput = computed({
  get: () => {
    try {
      return JSON.parse(form.skills || '[]').join(', ')
    } catch {
      return form.skills
    }
  },
  set: (val) => {
    form.skills = JSON.stringify(val.split(',').map(s => s.trim()).filter(Boolean))
  }
})

const rules = {
  title: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
  department: [{ required: true, message: '请输入部门', trigger: 'blur' }]
}

const parseSkills = (skills) => {
  if (!skills) return []
  try {
    return JSON.parse(skills)
  } catch {
    return skills.split(',').map(s => s.trim())
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPositions()
    positions.value = res.data || []
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.id = null
  form.title = ''
  form.department = ''
  form.skills = ''
  form.experienceMin = 0
  form.salaryRange = ''
  form.requirements = ''
  form.status = 1
}

const handleAdd = () => {
  resetForm()
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  isEdit.value = true
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  
  if (isEdit.value) {
    await updatePosition(form.id, form)
    ElMessage.success('更新成功')
  } else {
    await createPosition(form)
    ElMessage.success('创建成功')
  }
  
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该岗位？', '提示', { type: 'warning' })
  await deletePosition(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
