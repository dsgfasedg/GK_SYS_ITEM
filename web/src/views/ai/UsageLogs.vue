<template>
  <div class="usage-page">
    <!-- 汇总卡片 -->
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="stat-item">
            <p class="stat-label">总调用次数</p>
            <p class="stat-value">{{ stats.totalCalls || 0 }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="stat-item">
            <p class="stat-label">成功次数</p>
            <p class="stat-value success">{{ stats.successCalls || 0 }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="stat-item">
            <p class="stat-label">总费用</p>
            <p class="stat-value cost">¥ {{ totalCost.toFixed(6) }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 调用记录列表 -->
    <el-card>
      <template #header><span>调用记录</span></template>
      <el-table :data="logs" stripe style="width:100%" v-if="logs.length > 0">
        <el-table-column prop="createdAt" label="时间" width="180">
          <template #default="{ row }">{{ row.createdAt?.replace('T', ' ') }}</template>
        </el-table-column>
        <el-table-column prop="apiType" label="接口类型" width="140">
          <template #default="{ row }">
            <el-tag size="small">{{ apiTypeLabel(row.apiType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 'success' ? 'success' : 'danger'">
              {{ row.status === 'success' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="inputTokens" label="输入Token" width="100" />
        <el-table-column prop="outputTokens" label="输出Token" width="100" />
        <el-table-column label="费用" width="100">
          <template #default="{ row }">¥ {{ row.cost }}</template>
        </el-table-column>
        <el-table-column prop="errorMsg" label="错误信息" min-width="150">
          <template #default="{ row }">
            <span style="color:#f56c6c;font-size:12px">{{ row.errorMsg || '-' }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无调用记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/api/request'

const logs = ref([])
const stats = ref({})

const totalCost = computed(() => {
  return logs.value.reduce((sum, log) => sum + parseFloat(log.cost || 0), 0)
})

onMounted(async () => {
  const res = await request.get('/ai/usage-logs')
  stats.value = { totalCalls: res.totalCalls, successCalls: res.successCalls }
  logs.value = res.records || []
})

function apiTypeLabel(type) {
  const map = {
    plan_ocr: 'AI 规划',
    essay_correct: '申论批改',
    note_organize: '笔记整理',
    error_parse: '错题解析',
    question_parse: '题目解析',
  }
  return map[type] || type
}
</script>

<style scoped>
.usage-page { max-width: 1000px; margin: 20px auto; padding: 0 20px; }
.stat-item { text-align: center; padding: 8px 0; }
.stat-label { font-size: 13px; color: #909399; margin-bottom: 4px; }
.stat-value { font-size: 28px; font-weight: bold; color: #303133; margin: 0; }
.stat-value.success { color: #67c23a; }
.stat-value.cost { color: #e6a23c; }
</style>
