<template>
  <div class="create-note">
    <el-page-header @back="$router.push('/note')" content="新建笔记" />
    <el-card class="form-card">
      <el-form label-width="80px">
        <el-form-item label="类型">
          <el-radio-group v-model="type">
            <el-radio value="text">文本</el-radio>
            <el-radio value="image">图片笔记</el-radio>
            <el-radio value="file">文件上传</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="内容" v-if="type === 'text'">
          <el-input type="textarea" v-model="content" :rows="10" placeholder="请输入笔记内容..." />
        </el-form-item>

        <el-form-item label="上传" v-if="type === 'image'">
          <el-upload ref="uploadRef" :auto-upload="false" list-type="picture-card" accept="image/*" @change="handleFileChange">
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="上传" v-if="type === 'file'">
          <el-upload ref="uploadRef" :auto-upload="false" accept=".pdf,.doc,.docx,.txt,.png,.jpg,.jpeg" drag multiple>
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">拖拽文件到此处或 <em>点击上传</em></div>
          </el-upload>
        </el-form-item>

        <el-form-item v-if="type === 'image' || type === 'file'" label="&nbsp;">
          <el-button type="primary" @click="handleAiOcr" :loading="ocrLoading">
            {{ ocrLoading ? 'AI 解析中...' : 'AI OCR 识别并整理' }}
          </el-button>
          <span style="margin-left:8px;color:#909399;font-size:12px">上传后点击此按钮，AI 自动提取文字并整理为笔记</span>
        </el-form-item>

        <el-button type="primary" @click="handleSubmit" :disabled="type === 'text' && !content">保存</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createNote } from '@/api/note'

const router = useRouter()
const type = ref('text')
const content = ref('')
const fileList = ref([])
const ocrLoading = ref(false)

function handleFileChange(file) {
  fileList.value = [file]
}

async function handleAiOcr() {
  if (fileList.value.length === 0) { ElMessage.warning('请先上传文件'); return }
  ocrLoading.value = true
  try {
    // In production: upload to OSS → send to AI OCR API
    // MVP: simulate OCR result
    ElMessage.success('AI 识别完成，内容已填充')
    content.value = 'AI OCR 识别结果：\n\n这是从上传的图片/文件中自动提取的文字内容。\n\n在实际部署中，这里会调用百度云 OCR + DeepSeek API 进行文字提取和智能整理。'
  } finally {
    ocrLoading.value = false
  }
}

async function handleSubmit() {
  if (type.value === 'text') {
    if (!content.value) { ElMessage.warning('请输入内容'); return }
    await createNote(type.value, content.value)
  } else {
    await createNote(type.value, content.value || '待 AI 解析')
  }
  ElMessage.success('创建成功')
  router.push('/note')
}
</script>

<style scoped>
.create-note { max-width: 700px; margin: 20px auto; padding: 0 20px; }
.form-card { margin-top: 16px; }
</style>
