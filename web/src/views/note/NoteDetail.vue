<template>
  <div class="note-detail">
    <el-page-header @back="$router.push('/note')" content="笔记详情" />
    <el-card class="detail-card">
      <template #header>
        <div class="card-header">
          <el-tag>{{ note.type }}</el-tag>
          <div>
            <el-button text type="primary" @click="editing = true">编辑</el-button>
            <el-button text type="danger" @click="handleDelete">删除</el-button>
          </div>
        </div>
      </template>
      <div v-if="!editing" class="note-content">
        <div v-html="note.formattedContent || note.rawContent"></div>
        <div class="tags" v-if="note.tags">
          <el-tag v-for="t in note.tags.split(',')" :key="t" size="small">{{ t }}</el-tag>
        </div>
      </div>
      <div v-else>
        <el-input type="textarea" v-model="editContent" :rows="8" />
        <el-button type="primary" @click="handleSave" style="margin-top:12px">保存</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNoteDetail, updateNote, deleteNote } from '@/api/note'

const route = useRoute()
const router = useRouter()
const note = ref({})
const editing = ref(false)
const editContent = ref('')

onMounted(async () => {
  note.value = await getNoteDetail(route.params.noteId)
  editContent.value = note.value.formattedContent || note.value.rawContent || ''
})

async function handleSave() {
  await updateNote(note.value.noteId, editContent.value, note.value.tags)
  ElMessage.success('保存成功')
  editing.value = false
  note.value = await getNoteDetail(route.params.noteId)
}

async function handleDelete() {
  try {
    await ElMessageBox.confirm('确定删除？', '提示')
    await deleteNote(note.value.noteId)
    ElMessage.success('已删除')
    router.push('/note')
  } catch (e) { /* cancelled */ }
}
</script>

<style scoped>
.note-detail { max-width: 800px; margin: 20px auto; padding: 0 20px; }
.detail-card { margin-top: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.note-content { line-height: 1.8; white-space: pre-wrap; }
.tags { margin-top: 16px; display: flex; gap: 6px; flex-wrap: wrap; }
</style>
