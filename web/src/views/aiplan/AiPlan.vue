<template>
  <div class="aiplan-page">
    <el-row :gutter="20">
      <el-col :span="10">
        <el-card>
          <template #header><span>AI 学习规划</span></template>
          <p class="ai-desc">描述你的备考情况，AI 将为你生成个性化学习计划</p>
          <el-input type="textarea" v-model="situation" :rows="6" placeholder="例如：&#10;我今年大三，准备参加国考，行测基础薄弱，申论还没开始，距离考试还有3个月，每天能学习6小时。&#10;&#10;或者简单描述：&#10;只剩一个月冲刺，想重点突破行测和申论" />
          <div class="quick-tags">
            <el-tag v-for="tag in quickTags" :key="tag" size="small" class="quick-tag" @click="situation = tag">{{ tag }}</el-tag>
          </div>
          <el-button type="primary" @click="handleGenerate" :loading="generating" class="gen-btn">
            {{ generating ? 'AI思考中...' : '开始生成计划' }}
          </el-button>
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card v-if="plans.length > 0">
          <template #header>
            <span>AI 推荐计划</span>
            <el-button type="success" size="small" style="float:right" @click="handleImport" :loading="importing">
              {{ importing ? '导入中...' : '一键导入到我的计划' }}
            </el-button>
          </template>
          <el-empty v-if="plans.length === 0" description="暂无生成计划" />

          <div v-for="(req, idx) in plans" :key="idx" class="plan-card">
            <div class="plan-header">
              <span class="plan-day">{{ req.plannedDate }} {{ getWeekDay(req.plannedDate) }}</span>
            </div>
            <div class="plan-row">
              <span class="plan-idx">{{ idx + 1 }}</span>
              <div class="plan-info">
                <span class="plan-title">{{ req.title }}</span>
                <el-tag size="small">{{ req.subject }}</el-tag>
              </div>
              <div class="plan-time">
                <span class="plan-st">{{ req.plannedStartTime?.slice(0, 5) || '--:--' }}</span>
                <span class="plan-dur">{{ req.plannedDuration }}分</span>
              </div>
              <el-checkbox v-model="selected[idx]" />
            </div>
          </div>

          <div v-if="plans.length > 0" class="selected-info">
            已选中 <b>{{ selectedCount }}</b> / {{ plans.length }} 条计划
          </div>
        </el-card>
        <el-card v-else>
          <template #header><span>使用说明</span></template>
          <div class="help-list">
            <p>📝 <b>如何描述你的情况？</b></p>
            <p>建议包含：备考阶段、薄弱科目、每日可用时间、考试时间</p>
            <p>🎯 <b>示例：</b></p>
            <p class="example-text">"距离国考还有一个月，行测资料分析比较薄弱，申论还没开始复习，每天能学8小时，想要冲刺提分"</p>
            <p>⚡ 支持关键词识别：冲刺、基础、刷题、申论等</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { aiGeneratePlan, aiImportPlan } from '@/api/plan'

const situation = ref('')
const plans = ref([])
const selected = ref({})
const generating = ref(false)
const importing = ref(false)

const quickTags = [
  '还有一个月冲刺，行测需要快速提分',
  '刚开始备考，基础薄弱，想系统学习',
  '申论写作很差，需要专项训练',
  '想大量刷题，每天能做100道',
]

const selectedCount = computed(() => Object.values(selected.value).filter(Boolean).length)

function getWeekDay(dateStr) {
  if (!dateStr) return ''
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const d = new Date(dateStr)
  const today = new Date()
  const fmt = (d) => `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
  if (fmt(d) === fmt(today)) return '(今天)'
  return `(${weekdays[d.getDay()]})`
}

async function handleGenerate() {
  if (!situation.value.trim()) { ElMessage.warning('请描述你的备考情况'); return }
  generating.value = true
  try {
    plans.value = await aiGeneratePlan(situation.value)
    selected.value = {}
    for (let i = 0; i < plans.value.length; i++) {
      selected.value[i] = true
    }
    ElMessage.success(`已生成 ${plans.value.length} 条计划，请检查并导入`)
  } finally {
    generating.value = false
  }
}

async function handleImport() {
  const toImport = plans.value.filter((_, i) => selected.value[i])
  if (toImport.length === 0) { ElMessage.warning('请选择要导入的计划'); return }
  importing.value = true
  try {
    await aiImportPlan(toImport)
    ElMessage.success(`成功导入 ${toImport.length} 条计划到今日任务`)
    plans.value = []
    selected.value = {}
  } finally {
    importing.value = false
  }
}
</script>

<style scoped>
.aiplan-page { max-width: 1200px; margin: 20px auto; padding: 0 20px; }
.ai-desc { font-size: 14px; color: #606266; margin-bottom: 12px; }
.quick-tags { margin-top: 12px; display: flex; flex-wrap: wrap; gap: 6px; }
.quick-tag { cursor: pointer; }
.gen-btn { margin-top: 16px; width: 100%; }
.plan-card { margin-bottom: 12px; }
.plan-header { font-size: 13px; color: #409eff; font-weight: bold; margin-bottom: 4px; }
.plan-row { display: flex; align-items: center; padding: 8px 12px; border: 1px solid #ebeef5; border-radius: 6px; gap: 12px; }
.plan-row:hover { background: #f5f7fa; }
.plan-idx { width: 24px; color: #c0c4cc; font-size: 12px; }
.plan-info { flex: 1; display: flex; align-items: center; gap: 8px; }
.plan-title { font-size: 14px; }
.plan-time { display: flex; align-items: center; gap: 8px; min-width: 100px; }
.plan-st { font-size: 12px; background: #f0f2f5; padding: 2px 8px; border-radius: 4px; color: #606266; }
.plan-dur { font-size: 12px; color: #909399; }
.selected-info { margin-top: 12px; text-align: right; color: #909399; font-size: 13px; }
.help-list p { margin-bottom: 8px; font-size: 14px; line-height: 1.6; }
.example-text { background: #f5f7fa; padding: 8px 12px; border-radius: 4px; color: #606266; }
</style>
