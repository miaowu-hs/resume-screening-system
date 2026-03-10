<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <span></span>
      <el-button type="primary" :icon="Refresh" @click="loadData" :loading="loading">
        刷新数据
      </el-button>
    </div>
    <el-row :gutter="20">
      <el-col :span="6">
        <router-link to="/resumes" class="stat-link">
          <el-card class="stat-card clickable">
            <div class="stat-icon" style="background: #409EFF">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.resumeCount }}</div>
              <div class="stat-label">简历总数</div>
            </div>
          </el-card>
        </router-link>
      </el-col>
      <el-col :span="6">
        <router-link to="/positions" class="stat-link">
          <el-card class="stat-card clickable">
            <div class="stat-icon" style="background: #67C23A">
              <el-icon><Briefcase /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.positionCount }}</div>
              <div class="stat-label">岗位总数</div>
            </div>
          </el-card>
        </router-link>
      </el-col>
      <el-col :span="6">
        <router-link to="/match?showHistory=all" class="stat-link">
          <el-card class="stat-card clickable">
            <div class="stat-icon" style="background: #E6A23C">
              <el-icon><Connection /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.matchCount }}</div>
              <div class="stat-label">匹配次数</div>
            </div>
          </el-card>
        </router-link>
      </el-col>
      <el-col :span="6">
        <router-link to="/match?showHistory=high" class="stat-link">
          <el-card class="stat-card clickable">
            <div class="stat-icon" style="background: #F56C6C">
              <el-icon><Star /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.highMatchCount }}</div>
              <div class="stat-label">高匹配数</div>
            </div>
          </el-card>
        </router-link>
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
          <el-empty v-if="recentResumes.length === 0" description="暂无简历数据" />
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
          <el-empty v-if="topMatches.length === 0" description="暂无匹配数据" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Document, Briefcase, Connection, Star, Refresh } from '@element-plus/icons-vue'
import { getStats, getRecentResumes, getTopMatches } from '../api/stats'

const loading = ref(false)
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

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 获取统计数据
    const statsRes = await getStats()
    if (statsRes.code === 200) {
      stats.value = statsRes.data
    }
    
    // 获取最近简历
    const resumesRes = await getRecentResumes()
    if (resumesRes.code === 200) {
      recentResumes.value = resumesRes.data || []
    }
    
    // 获取 TOP 5 匹配
    const topRes = await getTopMatches()
    if (topRes.code === 200) {
      topMatches.value = topRes.data || []
    }
  } catch (e) {
    console.error('获取数据失败', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.stat-link {
  text-decoration: none;
  display: block;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-card.clickable {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card.clickable:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
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
