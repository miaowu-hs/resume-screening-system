<template>
  <div class="match-result">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>匹配结果</span>
          <div>
            <el-select v-model="selectedResume" placeholder="选择简历" style="width: 200px; margin-right: 10px">
              <el-option v-for="r in resumes" :key="r.id" :label="r.candidateName" :value="r.id" />
            </el-select>
            <el-button type="primary" @click="handleBatchMatch" :loading="matching">
              批量匹配
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="matchResults" v-loading="loading" style="width: 100%">
        <el-table-column prop="candidateName" label="候选人" width="120" />
        <el-table-column prop="positionTitle" label="岗位" width="180" />
        <el-table-column prop="matchScore" label="匹配度" width="120">
          <template #default="{ row }">
            <el-progress 
              :percentage="row.matchScore" 
              :color="getScoreColor(row.matchScore)"
              :format="val => val + '%'"
            />
          </template>
        </el-table-column>
        <el-table-column prop="matchReason" label="匹配理由" min-width="300">
          <template #default="{ row }">
            <el-text line-clamp="2">{{ row.matchReason }}</el-text>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="handleAccept(row)">通过</el-button>
            <el-button size="small" type="danger" @click="handleReject(row)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 匹配详情对话框 -->
    <el-dialog v-model="detailVisible" title="匹配详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="候选人">{{ currentMatch.candidateName }}</el-descriptions-item>
        <el-descriptions-item label="岗位">{{ currentMatch.positionTitle }}</el-descriptions-item>
        <el-descriptions-item label="匹配度" :span="2">
          <el-progress :percentage="currentMatch.matchScore" :color="getScoreColor(currentMatch.matchScore)" />
        </el-descriptions-item>
        <el-descriptions-item label="匹配理由" :span="2">
          {{ currentMatch.matchReason }}
        </el-descriptions-item>
      </el-descriptions>
      
      <h4 style="margin-top: 20px">技能分析</h4>
      <div v-if="skillMatch">
        <p><strong>匹配技能：</strong>
          <el-tag v-for="s in skillMatch.matched" :key="s" type="success" style="margin-right: 5px">{{ s }}</el-tag>
        </p>
        <p><strong>缺失技能：</strong>
          <el-tag v-for="s in skillMatch.missing" :key="s" type="danger" style="margin-right: 5px">{{ s }}</el-tag>
        </p>
        <p><strong>分析：</strong>{{ skillMatch.analysis }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMatchResults, batchMatch } from '../api/match'
import { getResumes } from '../api/resume'

const route = useRoute()
const loading = ref(false)
const matching = ref(false)
const matchResults = ref([])
const resumes = ref([])
const selectedResume = ref(null)
const detailVisible = ref(false)
const currentMatch = ref({})
const skillMatch = ref(null)

const getScoreColor = (score) => {
  if (score >= 80) return '#67C23A'
  if (score >= 60) return '#E6A23C'
  return '#F56C6C'
}

const getStatusType = (status) => {
  const types = { pending: 'info', viewed: 'warning', accepted: 'success', rejected: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { pending: '待处理', viewed: '已查看', accepted: '已通过', rejected: '已拒绝' }
  return texts[status] || status
}

const loadData = async () => {
  loading.value = true
  try {
    const [matchRes, resumeRes] = await Promise.all([
      getMatchResults({ resumeId: route.query.resumeId }),
      getResumes()
    ])
    matchResults.value = matchRes.data || []
    resumes.value = resumeRes.data || []
    
    if (route.query.resumeId) {
      selectedResume.value = Number(route.query.resumeId)
    }
  } finally {
    loading.value = false
  }
}

const handleBatchMatch = async () => {
  if (!selectedResume.value) {
    ElMessage.warning('请选择简历')
    return
  }
  
  matching.value = true
  try {
    const res = await batchMatch(selectedResume.value)
    matchResults.value = res.data || []
    ElMessage.success('匹配完成')
  } finally {
    matching.value = false
  }
}

const handleAccept = (row) => {
  ElMessage.success('已标记为通过')
}

const handleReject = (row) => {
  ElMessage.info('已标记为拒绝')
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
