<template>
  <div class="question-page">
    <!-- 导入区 -->
    <el-card class="import-card">
      <template #header>
        <span>题目导入</span>
        <el-tag type="info" style="margin-left:8px">支持文件上传 + 答案导入自动算分</el-tag>
      </template>
      <el-tabs v-model="importTab">
        <el-tab-pane label="文件上传" name="file">
          <el-upload ref="uploadRef" :auto-upload="false" accept=".doc,.docx,.pdf,.txt,.xlsx,.xls" drag multiple>
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">拖拽试卷文件到此处或 <em>点击上传</em></div>
          </el-upload>
          <div style="margin-top:12px">
            <el-button type="primary" @click="handleParseFile">AI 解析试卷</el-button>
            <span style="margin-left:8px;color:#909399;font-size:12px">自动提取题目和选项</span>
          </div>
        </el-tab-pane>
        <el-tab-pane label="导入答案" name="answer">
          <el-upload :auto-upload="false" accept=".doc,.docx,.pdf,.txt,.xlsx" drag>
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">上传答案文件或 <em>点击上传</em></div>
          </el-upload>
          <div style="margin-top:12px">
            <el-input type="textarea" v-model="answerKey" :rows="3" placeholder="或手动粘贴答案，如：A,B,C,D,A,B,C,D..." />
          </div>
          <div style="margin-top:8px">
            <el-button type="primary" @click="handleImportAnswer">导入答案并计分</el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 题目列表 -->
    <el-card style="margin-top:16px">
      <template #header>
        <span>题库（共 {{ questions.length }} 题）</span>
      </template>
      <div v-for="q in questions" :key="q.questionId" class="question-item">
        <p class="q-stem">{{ q.stem?.slice(0, 80) }}</p>
        <div class="q-meta">
          <el-tag size="small" :type="q.status === 'active' ? 'success' : 'info'">{{ q.status }}</el-tag>
          <el-button text type="danger" size="small" @click="handleDelete(q)">删除</el-button>
        </div>
      </div>
      <el-empty v-if="questions.length === 0" description="暂无题目，请上传文件导入" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listQuestions, deleteQuestion } from '@/api/question'

const questions = ref([])
const importTab = ref('file')
const answerKey = ref('')

onMounted(() => fetchQuestions())

async function fetchQuestions() { questions.value = await listQuestions() }

function handleParseFile() {
  ElMessage.success('AI 解析完成，已导入 15 道题目（演示）')
  fetchQuestions()
}

function handleImportAnswer() {
  if (!answerKey.value.trim()) { ElMessage.warning('请粘贴答案'); return }
  ElMessage.success('答案匹配完成，正确率 73%（演示）')
}

async function handleDelete(q) {
  try {
    await ElMessageBox.confirm('确定删除？', '提示')
    await deleteQuestion(q.questionId)
    ElMessage.success('已删除')
    fetchQuestions()
  } catch (e) { /* cancelled */ }
}
</script>

<style scoped>
.question-page { max-width: 900px; margin: 20px auto; padding: 0 20px; }
.import-card { margin-bottom: 0; }
.question-item { padding: 12px 0; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center; }
.q-stem { margin: 0; font-size: 14px; flex: 1; }
.q-meta { display: flex; align-items: center; gap: 8px; }
</style>
