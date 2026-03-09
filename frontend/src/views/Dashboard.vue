<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #409EFF">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.resumeCount }}</div>
            <div class="stat-label">简历总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #67C23A">
            <el-icon><Briefcase /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.positionCount }}</div>
            <div class="stat-label">岗位总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #E6A23C">
            <el-icon><Connection /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.matchCount }}</div>
            <div class="stat-label">匹配次数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #F56C6C">
            <el-icon><Star /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.highMatchCount }}</div>
            <div class="stat-label">高匹配数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最近上传的简历</span>
          </template>
          <el-table :data="recentResumes" style="width: 100%">
            <el-table-column prop="candidateName" label="姓名" />
            <el-table-column prop="education" label="学历" />
            <el-table-column prop="experienceYears" label="工作年限" />
            <el-table-column prop="createdAt" label="上传时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>匹配度 TOP 5</span>
          </template>
          <el-table :data="topMatches" style="width: 100%">
            <el-table-column prop="candidateName" label="候选人" />
            <el-table-column prop="positionTitle" label="岗位" />
            <el-table-column prop="matchScore" label="匹配度" width="100">
              <template #default="{ row }">
                <el-tag :type="getScoreType(row.matchScore)">
                  {{ row.matchScore }}%
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Document, Briefcase, Connection, Star } from '@element-plus/icons-vue'

const stats = ref({
  resumeCount: 0,
  positionCount: 0,
  matchCount: 0,
  highMatchCount: 0
})

const recentResumes = ref([])
const topMatches = ref([])

const getScoreType = (score) => {
  if (score >= 80) return 'success'
  if (score >= 60) return 'warning'
  return 'danger'
}

onMounted(async () => {
  // TODO: 调用接口获取数据
  // 模拟数据
  stats.value = {
    resumeCount: 156,
    positionCount: 12,
    matchCount: 423,
    highMatchCount: 28
  }
  
  recentResumes.value = [
    { candidateName: '张三', education: '本科', experienceYears: 3, createdAt: '2024-03-09 16:30' },
    { candidateName: '李四', education: '硕士', experienceYears: 5, createdAt: '2024-03-09 15:20' },
    { candidateName: '王五', education: '本科', experienceYears: 2, createdAt: '2024-03-09 14:10' }
  ]
  
  topMatches.value = [
    { candidateName: '赵六', positionTitle: 'Java开发工程师', matchScore: 95 },
    { candidateName: '孙七', positionTitle: '前端开发工程师', matchScore: 88 },
    { candidateName: '周八', positionTitle: 'Python开发工程师', matchScore: 82 },
    { candidateName: '吴九', positionTitle: '测试工程师', matchScore: 76 },
    { candidateName: '郑十', positionTitle: '运维工程师', matchScore: 71 }
  ]
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  width: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 28px;
  margin-right: 20px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}
</style>
