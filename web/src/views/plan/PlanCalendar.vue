<template>
  <div class="plan-page">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button @click="prevDate">&lt; 前一天</el-button>
        <el-button type="primary" @click="goToday">今日</el-button>
        <el-button @click="nextDate">后一天 &gt;</el-button>
        <span class="date-display">{{ currentDate }}</span>
      </div>
      <div class="toolbar-right">
        <el-button type="success" @click="showCreateDialog = true">新建任务</el-button>
      </div>
    </div>

    <el-card class="task-list">
      <template #header>
        <span>任务列表</span>
        <el-progress :percentage="completionRate" :stroke-width="8" style="width: 200px; display: inline-block; margin-left: 20px;" />
      </template>

      <el-empty v-if="tasks.length === 0" description="今天没有任务" />

      <div v-for="task in tasks" :key="task.taskId" class="task-item">
        <el-checkbox :model-value="task.status === 'completed'" @change="handleComplete(task)" />
        <div class="task-info">
          <span :class="{ 'task-done': task.status === 'completed' }">{{ task.title }}</span>
          <el-tag size="small" type="primary" v-if="task.subject">{{ task.subject }}</el-tag>
        </div>
        <div class="task-actions">
          <el-button text type="primary" size="small" @click="showEditDialog(task)">编辑</el-button>
          <el-button text type="primary" size="small" @click="showDelayDialog(task)">延迟</el-button>
          <el-button text type="danger" size="small" @click="handleDelete(task)">删除</el-button>
        </div>
      </div>
    </el-card>

    <!-- 完成情况柱状图（放在任务列表下方） -->
    <el-card class="stats-card" v-if="chartData.length > 0" style="margin-top: 16px;">
      <template #header><span>本周完成情况</span></template>
      <div ref="chartRef" style="height:200px"></div>
    </el-card>

    <!-- Create Task Dialog -->
    <el-dialog v-model="showCreateDialog" :title="isEditing ? '编辑任务' : '新建任务'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="任务标题" />
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="form.subject" placeholder="选择科目" style="width: 100%">
            <el-option label="言语理解" value="言语理解" />
            <el-option label="判断推理" value="判断推理" />
            <el-option label="数量关系" value="数量关系" />
            <el-option label="资料分析" value="资料分析" />
            <el-option label="常识判断" value="常识判断" />
            <el-option label="申论" value="申论" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="form.plannedDate" type="date" style="width: 100%" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-time-picker v-model="form.plannedStartTime" style="width: 100%" />
        </el-form-item>
        <el-form-item label="时长(分)">
          <el-input-number v-model="form.plannedDuration" :min="10" :step="10" />
        </el-form-item>
        <el-form-item label="实际耗时" v-if="isEditing">
          <el-input-number v-model="form.actualDuration" :min="0" :step="5" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="handleSave">{{ isEditing ? '保存' : '确定' }}</el-button>
      </template>
    </el-dialog>

    <!-- Delay Dialog -->
    <el-dialog v-model="showDelayDialogVisible" title="延迟任务" width="400px">
      <el-radio-group v-model="delayType">
        <el-radio value="tomorrow">推迟到明天</el-radio>
        <el-radio value="week">推迟一周</el-radio>
      </el-radio-group>
      <template #footer>
        <el-button @click="showDelayDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDelay">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTasks, createTask, updateTask, completeTask, delayTask, deleteTask, getTaskStats } from '@/api/plan'
import * as echarts from 'echarts'

const currentDate = ref(new Date().toISOString().slice(0, 10))
const tasks = ref([])
const showCreateDialog = ref(false)
const showDelayDialogVisible = ref(false)
const delayTarget = ref(null)
const delayType = ref('tomorrow')

const editingTask = ref(null)
const isEditing = computed(() => !!editingTask.value)

const form = ref({
  title: '',
  subject: '',
  plannedDate: new Date(),
  plannedStartTime: null,
  plannedDuration: 60,
  actualDuration: 0,
})

// Chart
const chartRef = ref(null)
const chartData = ref([])

onMounted(() => {
  fetchTasks()
  fetchStats()
})

watch(() => currentDate.value, () => fetchStats())

const completionRate = computed(() => {
  if (tasks.value.length === 0) return 0
  const completed = tasks.value.filter(t => t.status === 'completed').length
  return Math.round((completed / tasks.value.length) * 100)
})

onMounted(() => {
  fetchTasks()
})

function formatDate(dateStr) {
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

async function fetchTasks() {
  try {
    const res = await getTasks(currentDate.value)
    tasks.value = res.tasks || []
  } catch (e) {
    // handled
  }
}

function prevDate() {
  const d = new Date(currentDate.value)
  d.setDate(d.getDate() - 1)
  currentDate.value = formatDate(d.toISOString())
  fetchTasks()
}

function nextDate() {
  const d = new Date(currentDate.value)
  d.setDate(d.getDate() + 1)
  currentDate.value = formatDate(d.toISOString())
  fetchTasks()
}

function goToday() {
  currentDate.value = new Date().toISOString().slice(0, 10)
  fetchTasks()
}

async function handleSave() {
  if (!form.value.title) {
    ElMessage.warning('请输入标题')
    return
  }
  const data = {
    ...form.value,
    plannedDate: form.value.plannedDate
      ? formatDate(form.value.plannedDate.toISOString())
      : currentDate.value,
  }

  if (isEditing.value) {
    await updateTask(editingTask.value.taskId, data)
    ElMessage.success('保存成功')
  } else {
    await createTask(data)
    ElMessage.success('创建成功')
  }

  closeDialog()
  fetchTasks()
}

function showEditDialog(task) {
  editingTask.value = task
  form.value = {
    title: task.title,
    subject: task.subject || '',
    plannedDate: task.plannedDate ? new Date(task.plannedDate) : new Date(),
    plannedStartTime: task.plannedStartTime ? parseTime(task.plannedStartTime) : null,
    plannedDuration: task.plannedDuration || 60,
    actualDuration: task.actualDuration || 0,
  }
  showCreateDialog.value = true
}

function parseTime(timeStr) {
  if (!timeStr) return null
  const [h, m] = timeStr.split(':')
  const d = new Date()
  d.setHours(parseInt(h), parseInt(m), 0, 0)
  return d
}

function closeDialog() {
  showCreateDialog.value = false
  editingTask.value = null
  form.value = { title: '', subject: '', plannedDate: new Date(), plannedStartTime: null, plannedDuration: 60, actualDuration: 0 }
}

// === 柱状图 ===
async function fetchStats() {
  try {
    const stats = await getTaskStats('week')
    chartData.value = stats.dailyStats || []
    await nextTick()
    renderChart()
  } catch (e) { /* ignore */ }
}

function renderChart() {
  if (!chartRef.value || chartData.value.length === 0) return
  const chart = echarts.init(chartRef.value)
  const dates = chartData.value.map(d => {
    const parts = d.date.split('-')
    return `${parts[1]}/${parts[2]}`
  })
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['已完成', '待处理', '已延迟'], bottom: 0 },
    grid: { left: 40, right: 20, top: 20, bottom: 40 },
    xAxis: { type: 'category', data: dates, axisLabel: { fontSize: 11 } },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: '已完成', type: 'bar', stack: 'total',
        data: chartData.value.map(d => d.completed),
        itemStyle: { color: '#67c23a' },
      },
      {
        name: '待处理', type: 'bar', stack: 'total',
        data: chartData.value.map(d => d.pending),
        itemStyle: { color: '#e6a23c' },
      },
      {
        name: '已延迟', type: 'bar', stack: 'total',
        data: chartData.value.map(d => d.delayed),
        itemStyle: { color: '#f56c6c' },
      },
    ],
  })
}

async function handleComplete(task) {
  if (task.status === 'completed') return
  await completeTask(task.taskId)
  ElMessage.success('已完成')
  fetchTasks()
}

function showDelayDialog(task) {
  delayTarget.value = task
  showDelayDialogVisible.value = true
}

async function confirmDelay() {
  if (delayTarget.value) {
    await delayTask(delayTarget.value.taskId, delayType.value)
    ElMessage.success('已延迟')
    showDelayDialogVisible.value = false
    fetchTasks()
  }
}

async function handleDelete(task) {
  try {
    await ElMessageBox.confirm('确定删除此任务？', '提示')
    await deleteTask(task.taskId)
    ElMessage.success('已删除')
    fetchTasks()
  } catch (e) {
    // cancelled
  }
}
</script>

<style scoped>
.plan-page {
  max-width: 900px;
  margin: 20px auto;
  padding: 0 20px;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.toolbar-left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.date-display {
  font-size: 18px;
  font-weight: bold;
  margin-left: 12px;
}
.task-list {
  min-height: 400px;
}
.task-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}
.task-info {
  flex: 1;
  margin-left: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.task-done {
  text-decoration: line-through;
  color: #c0c4cc;
}
.task-actions {
  display: flex;
  gap: 4px;
}
</style>
