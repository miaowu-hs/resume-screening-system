<template>
  <div class="resume-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>简历列表</span>
          <el-upload
            :show-file-list="false"
            accept=".pdf,.doc,.docx"
            :before-upload="handleUpload"
          >
            <el-button type="primary">
              <el-icon><Upload /></el-icon>
              上传简历
            </el-button>
          </el-upload>
        </div>
      </template>

      <el-table :data="resumes" v-loading="loading" style="width: 100%">
        <el-table-column prop="candidateName" label="姓名" width="120" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="education" label="学历" width="100" />
        <el-table-column prop="experienceYears" label="工作年限" width="100">
          <template #default="{ row }">
            {{ row.experienceYears }}年
          </template>
        </el-table-column>
        <el-table-column prop="skills" label="技能" min-width="200">
          <template #default="{ row }">
            <el-tag v-for="skill in parseSkills(row.skills)" :key="skill" size="small" style="margin-right: 5px">
              {{ skill }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="上传时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleMatch(row)">匹配</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 上传信息填写弹窗 -->
    <el-dialog v-model="uploadDialogVisible" title="简历信息确认" width="500px">
      <!-- AI解析提示 -->
      <el-alert 
        v-if="parseMessage" 
        :title="parseMessage" 
        :type="hasMissingFields ? 'warning' : 'success'"
        :closable="false"
        style="margin-bottom: 20px"
      />
      
      <el-form :model="uploadForm" label-width="80px">
        <el-form-item label="姓名" required>
          <el-input v-model="uploadForm.candidateName" placeholder="请输入候选人姓名" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="uploadForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="uploadForm.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="学历">
          <el-select v-model="uploadForm.education" placeholder="请选择学历">
            <el-option label="高中" value="高中" />
            <el-option label="大专" value="大专" />
            <el-option label="本科" value="本科" />
            <el-option label="硕士" value="硕士" />
            <el-option label="博士" value="博士" />
          </el-select>
        </el-form-item>
        <el-form-item label="工作年限">
          <el-input-number v-model="uploadForm.experienceYears" :min="0" :max="30" />
        </el-form-item>
        <el-form-item label="技能">
          <el-input v-model="uploadForm.skills" placeholder="多个技能用逗号分隔，如：Java,MySQL,Spring" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmUpload" :loading="uploading">确认上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import { getResumes, deleteResume, uploadResume, parseResume } from '../api/resume'

const router = useRouter()
const loading = ref(false)
const resumes = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 上传相关
const uploadDialogVisible = ref(false)
const uploading = ref(false)
const parsing = ref(false)
const pendingFile = ref(null)
const parseMessage = ref('')
const hasMissingFields = ref(false)
const uploadForm = ref({
  candidateName: '',
  phone: '',
  email: '',
  education: '本科',
  experienceYears: 0,
  skills: ''
})

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
    const res = await getResumes()
    resumes.value = res.data || []
    total.value = resumes.value.length
  } finally {
    loading.value = false
  }
}

const handleUpload = async (file) => {
  pendingFile.value = file
  parsing.value = true
  parseMessage.value = ''
  hasMissingFields.value = false
  
  // 重置表单
  uploadForm.value = {
    candidateName: '',
    phone: '',
    email: '',
    education: '本科',
    experienceYears: 0,
    skills: ''
  }
  
  // 先调用 AI 解析
  try {
    const formData = new FormData()
    formData.append('file', file)
    
    const res = await parseResume(formData)
    if (res.code === 200 && res.data) {
      const data = res.data
      uploadForm.value.candidateName = data.candidateName || ''
      uploadForm.value.phone = data.phone || ''
      uploadForm.value.email = data.email || ''
      uploadForm.value.education = data.education || '本科'
      uploadForm.value.experienceYears = data.experienceYears || 0
      uploadForm.value.skills = data.skills || ''
      
      parseMessage.value = data.parseMessage || ''
      hasMissingFields.value = data.hasMissingFields || false
    }
  } catch (e) {
    console.error('解析失败', e)
    parseMessage.value = 'AI解析失败，请手动填写信息'
    hasMissingFields.value = true
  } finally {
    parsing.value = false
  }
  
  uploadDialogVisible.value = true
  return false
}

const confirmUpload = async () => {
  if (!uploadForm.value.candidateName) {
    ElMessage.warning('请输入候选人姓名')
    return
  }
  
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', pendingFile.value)
    formData.append('candidateName', uploadForm.value.candidateName)
    formData.append('phone', uploadForm.value.phone || '')
    formData.append('email', uploadForm.value.email || '')
    formData.append('education', uploadForm.value.education || '')
    formData.append('experienceYears', uploadForm.value.experienceYears || 0)
    formData.append('skills', uploadForm.value.skills || '')
    
    await uploadResume(formData)
    ElMessage.success('上传成功')
    uploadDialogVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
}

const handleMatch = (row) => {
  router.push({ path: '/match', query: { resumeId: row.id } })
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该简历？', '提示', { type: 'warning' })
  await deleteResume(row.id)
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
