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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import { getResumes, deleteResume, uploadResume } from '../api/resume'

const router = useRouter()
const loading = ref(false)
const resumes = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

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
  const formData = new FormData()
  formData.append('file', file)
  
  try {
    await uploadResume(formData)
    ElMessage.success('上传成功')
    loadData()
  } catch (e) {
    console.error(e)
  }
  
  return false
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
