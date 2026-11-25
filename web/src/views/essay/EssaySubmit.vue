<template>
  <div class="essay-page">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header><span>提交申论批改</span></template>
          <el-form label-width="80px">
            <el-form-item label="题目">
              <el-input type="textarea" v-model="questionText" :rows="4" placeholder="请输入申论题目..." />
            </el-form-item>
            <el-form-item label="作答">
              <el-input type="textarea" v-model="essayText" :rows="8" placeholder="请输入你的作答..." />
            </el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">{{ submitting ? '批改中...' : '提交批改' }}</el-button>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card v-if="result">
          <template #header><span>评分结果</span></template>
          <div ref="radarRef" style="height:250px"></div>
          <el-descriptions :column="2" border size="small" style="margin-top:12px">
            <el-descriptions-item label="逻辑结构">{{ result.logicScore }}/10</el-descriptions-item>
            <el-descriptions-item label="内容要点">{{ result.contentScore }}/10</el-descriptions-item>
            <el-descriptions-item label="语言表达">{{ result.languageScore }}/10</el-descriptions-item>
            <el-descriptions-item label="规范评分">{{ result.standardScore }}/10</el-descriptions-item>
          </el-descriptions>
          <el-divider />
          <div v-for="s in suggestions" :key="s">✦ {{ s }}</div>
        </el-card>
        <el-card v-else>
          <template #header><span>批改历史</span></template>
          <div v-for="h in history" :key="h.correctionId" class="history-item" @click="$router.push(`/essay/${h.correctionId}`)">
            <p>{{ h.questionText?.slice(0, 40) }}...</p>
            <span class="history-score">均分: {{ ((h.logicScore + h.contentScore + h.languageScore + h.standardScore) / 4).toFixed(1) }}</span>
          </div>
          <el-empty v-if="history.length === 0" description="暂无记录" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { submitEssay, getEssayHistory } from '@/api/essay'
import * as echarts from 'echarts'

const questionText = ref('')
const essayText = ref('')
const submitting = ref(false)
const result = ref(null)
const suggestions = ref([])
const history = ref([])
const radarRef = ref(null)

onMounted(async () => {
  history.value = await getEssayHistory()
})

async function handleSubmit() {
  if (!questionText.value || !essayText.value) { ElMessage.warning('请填写完整'); return }
  submitting.value = true
  try {
    const res = await submitEssay(questionText.value, essayText.value)
    result.value = res
    suggestions.value = JSON.parse(res.suggestions || '[]')
    await nextTick()
    const chart = echarts.init(radarRef.value)
    chart.setOption({
      radar: { indicator: [
        { name: '逻辑结构', max: 10 }, { name: '内容要点', max: 10 },
        { name: '语言表达', max: 10 }, { name: '规范评分', max: 10 },
      ]},
      series: [{ type: 'radar', data: [{ value: [res.logicScore, res.contentScore, res.languageScore, res.standardScore] }] }],
    })
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.essay-page { margin: 20px; }
.history-item { padding: 10px 0; border-bottom: 1px solid #eee; cursor: pointer; display: flex; justify-content: space-between; }
.history-item:hover { background: #f5f7fa; }
.history-score { color: #409eff; font-size: 13px; }
</style>
