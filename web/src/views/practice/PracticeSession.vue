<template>
  <div class="practice-page">
    <el-card v-if="!sessionId">
      <template #header><span>练习设置</span></template>
      <el-form label-width="100px">
        <el-form-item label="知识点">
          <el-select v-model="tagFilter" placeholder="全部" clearable style="width:100%">
            <el-option label="言语理解" value="言语理解" />
            <el-option label="判断推理" value="判断推理" />
            <el-option label="数量关系" value="数量关系" />
            <el-option label="资料分析" value="资料分析" />
          </el-select>
        </el-form-item>
        <el-form-item label="题目数量">
          <el-radio-group v-model="count">
            <el-radio :value="10">10 题</el-radio>
            <el-radio :value="20">20 题</el-radio>
            <el-radio :value="30">30 题</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-button type="primary" @click="handleStart">开始练习</el-button>
      </el-form>
    </el-card>

    <el-card v-else-if="currentQuestion" class="practice-card">
      <div class="progress">第 {{ answered + 1 }} 题</div>
      <h4 class="stem">{{ currentQuestion.stem }}</h4>
      <div class="options">
        <el-radio-group v-model="userAnswer" class="option-group">
          <el-radio v-for="opt in options" :key="opt.key" :value="opt.key" class="option-item">
            {{ opt.key }}. {{ opt.text }}
          </el-radio>
        </el-radio-group>
      </div>
      <el-button type="primary" @click="handleSubmit" :disabled="!userAnswer" :loading="submitting">提交答案</el-button>

      <div v-if="showResult" class="result-box" :class="isCorrect ? 'correct' : 'wrong'">
        <p>{{ isCorrect ? '✅ 回答正确！' : '❌ 回答错误' }}</p>
        <p v-if="!isCorrect">正确答案：{{ currentQuestion.correctAnswer }}</p>
        <p v-if="currentQuestion.analysis">解析：{{ currentQuestion.analysis }}</p>
        <el-button @click="handleNext" type="primary" style="margin-top:8px">{{ hasMore ? '下一题' : '查看报告' }}</el-button>
      </div>
    </el-card>

    <el-card v-else class="practice-card">
      <div class="report">
        <h2>练习报告</h2>
        <el-progress type="dashboard" :percentage="report.accuracy || 0" :width="150" />
        <p>正确：{{ report.correctCount }} / {{ report.totalCount }}</p>
        <el-button type="primary" @click="reset">再来一次</el-button>
        <el-button @click="$router.push('/plan')">返回首页</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { startPractice, getNextQuestion, submitAnswer, finishPractice, getPracticeReport } from '@/api/question'

const sessionId = ref(null)
const currentQuestion = ref(null)
const userAnswer = ref('')
const showResult = ref(false)
const isCorrect = ref(false)
const submitting = ref(false)
const answered = ref(0)
const totalCount = ref(0)
const hasMore = ref(true)
const report = ref({})
const tagFilter = ref('')
const count = ref(10)

const options = computed(() => {
  if (!currentQuestion.value?.options) return []
  try { return JSON.parse(currentQuestion.value.options) }
  catch { return [] }
})

async function handleStart() {
  const session = await startPractice(tagFilter.value || null, count.value)
  sessionId.value = session.sessionId
  totalCount.value = session.totalCount
  await loadNextQuestion()
}

async function loadNextQuestion() {
  currentQuestion.value = await getNextQuestion(sessionId.value)
  showResult.value = false
  userAnswer.value = ''
}

async function handleSubmit() {
  if (!userAnswer.value) return
  submitting.value = true
  try {
    const res = await submitAnswer(sessionId.value, currentQuestion.value.questionId, userAnswer.value)
    isCorrect.value = res.correct
    showResult.value = true
    answered.value++
  } finally {
    submitting.value = false
  }
}

async function handleNext() {
  if (answered.value >= totalCount.value) {
    await finishPractice(sessionId.value)
    report.value = await getPracticeReport(sessionId.value)
    currentQuestion.value = null
    hasMore.value = false
  } else {
    await loadNextQuestion()
  }
}

function reset() {
  sessionId.value = null
  currentQuestion.value = null
  answered.value = 0
  showResult.value = false
  hasMore.value = true
}
</script>

<style scoped>
.practice-page { max-width: 700px; margin: 20px auto; padding: 0 20px; }
.practice-card { min-height: 400px; }
.progress { font-size: 16px; font-weight: bold; margin-bottom: 16px; color: #409eff; }
.stem { line-height: 1.8; margin-bottom: 20px; }
.option-group { display: flex; flex-direction: column; gap: 12px; }
.option-item { padding: 10px 16px; border: 1px solid #dcdfe6; border-radius: 6px; width: 100%; }
.option-item.is-checked { border-color: #409eff; background: #ecf5ff; }
.result-box { margin-top: 16px; padding: 16px; border-radius: 6px; }
.result-box.correct { background: #f0f9eb; border: 1px solid #b7eb8f; }
.result-box.wrong { background: #fef0f0; border: 1px solid #fbc4c4; }
.report { text-align: center; padding: 40px 0; }
.report p { margin-top: 16px; font-size: 16px; }
</style>
