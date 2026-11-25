<template>
  <div class="record-page">
    <el-card>
      <template #header>
        <div class="record-header">
          <span>任务流水记录</span>
          <div class="record-actions">
            <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" size="small" @change="fetchRecords" />
            <el-tag type="success" style="margin-left:8px">已完成 {{ stats.completed }}</el-tag>
            <el-tag type="warning">待处理 {{ stats.pending }}</el-tag>
            <el-tag type="danger">已延迟 {{ stats.delayed }}</el-tag>
          </div>
        </div>
      </template>

      <!-- 汇总统计 -->
      <div class="summary-row">
        <span class="summary-label">任务总数 <b>{{ stats.total }}</b></span>
        <el-progress :percentage="completionRate" :stroke-width="12" style="width:300px">
          <span>完成率 {{ completionRate }}%</span>
        </el-progress>
      </div>

      <!-- 流水列表 -->
      <div v-if="Object.keys(records).length > 0" class="record-body">
        <div v-for="(tasks, dateStr) in records" :key="dateStr" class="record-date-group">
          <div class="date-header">
            <span class="date-label">{{ formatDisplayDate(dateStr) }}</span>
            <span class="date-summary">{{ tasks.length }} 项 · {{ tasks.filter(t => t.status === 'completed').length }} 已完成</span>
          </div>
          <div v-for="(task, idx) in tasks" :key="task.taskId"
               class="record-row"
               :class="{ 'row-completed': task.status === 'completed', 'row-delayed': task.status === 'delayed' }">
            <span class="row-idx">{{ idx + 1 }}</span>
            <div class="row-main">
              <span class="row-title">{{ task.title }}</span>
              <el-tag size="small" v-if="task.subject">{{ task.subject }}</el-tag>
            </div>
            <div class="row-time">
              <span v-if="task.plannedStartTime" class="time-badge">{{ task.plannedStartTime?.slice(0, 5) }}</span>
              <span class="duration">{{ task.plannedDuration || '-' }}分</span>
            </div>
            <div class="row-status">
              <el-tag size="small" :type="statusType(task.status)">{{ statusText(task.status) }}</el-tag>
              <span v-if="task.actualDuration && task.actualDuration > 0" class="actual-time">实耗 {{ task.actualDuration }}分</span>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="请选择日期范围查看记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getTaskRecords } from '@/api/plan'

const dateRange = ref([])
const stats = ref({ total: 0, completed: 0, pending: 0, delayed: 0 })
const records = ref({})

const completionRate = computed(() => {
  if (stats.value.total === 0) return 0
  return Math.round((stats.value.completed / stats.value.total) * 100)
})

onMounted(() => {
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 6)
  dateRange.value = [start, end]
  fetchRecords()
})

function formatDate(d) {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

function formatDisplayDate(dateStr) {
  const d = new Date(dateStr)
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  const today = new Date()
  const yesterday = new Date()
  yesterday.setDate(yesterday.getDate() - 1)

  if (formatDate(d) === formatDate(today)) return '今天'
  if (formatDate(d) === formatDate(yesterday)) return '昨天'
  return `${dateStr} 周${weekdays[d.getDay()]}`
}

function statusType(s) {
  if (s === 'completed') return 'success'
  if (s === 'delayed') return 'danger'
  return 'warning'
}

function statusText(s) {
  if (s === 'completed') return '已完成'
  if (s === 'delayed') return '已延迟'
  return '待处理'
}

async function fetchRecords() {
  if (!dateRange.value || !dateRange.value[0]) return
  const start = formatDate(dateRange.value[0])
  const end = formatDate(dateRange.value[1])
  const res = await getTaskRecords(start, end)
  stats.value = { total: res.total, completed: res.completed, pending: res.pending, delayed: res.delayed }
  records.value = res.records || {}
}
</script>

<style scoped>
.record-page { max-width: 1000px; margin: 20px auto; padding: 0 20px; }
.record-header { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 8px; }
.record-actions { display: flex; align-items: center; }
.summary-row { display: flex; align-items: center; gap: 20px; margin-bottom: 20px; padding: 12px 16px; background: #f5f7fa; border-radius: 6px; }
.summary-label { font-size: 14px; white-space: nowrap; }
.record-date-group { margin-bottom: 20px; }
.date-header { display: flex; justify-content: space-between; padding: 8px 12px; background: #ecf5ff; border-radius: 4px; margin-bottom: 4px; font-size: 14px; }
.date-label { font-weight: bold; color: #409eff; }
.date-summary { color: #909399; font-size: 13px; }
.record-row { display: flex; align-items: center; padding: 10px 12px; border-bottom: 1px solid #f0f0f0; transition: background 0.2s; gap: 12px; }
.record-row:hover { background: #fafafa; }
.row-completed { opacity: 0.7; }
.row-delayed .row-title { color: #f56c6c; text-decoration: line-through; }
.row-idx { width: 24px; color: #c0c4cc; font-size: 12px; }
.row-main { flex: 1; display: flex; align-items: center; gap: 8px; }
.row-title { font-size: 14px; }
.row-time { display: flex; align-items: center; gap: 6px; min-width: 100px; }
.time-badge { font-size: 12px; background: #f0f2f5; padding: 2px 8px; border-radius: 4px; color: #606266; }
.duration { font-size: 12px; color: #909399; }
.row-status { display: flex; align-items: center; gap: 8px; min-width: 120px; justify-content: flex-end; }
.actual-time { font-size: 12px; color: #67c23a; }
</style>
