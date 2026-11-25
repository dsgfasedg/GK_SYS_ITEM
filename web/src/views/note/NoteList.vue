<template>
  <div class="note-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header><span>知识树</span></template>
          <el-tree :data="treeData" :props="{ children: 'children', label: 'name' }" node-key="nodeId" @node-click="handleNodeClick" />
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>笔记列表</span>
            <el-button type="primary" size="small" style="float:right" @click="$router.push('/note/create')">新建笔记</el-button>
          </template>
          <el-input v-model="keyword" placeholder="搜索笔记" prefix-icon="Search" clearable @input="fetchNotes" style="margin-bottom:12px" />
          <div v-for="n in notes" :key="n.noteId" class="note-item" @click="$router.push(`/note/${n.noteId}`)">
            <p class="note-title">{{ n.rawContent?.slice(0, 50) || '无内容' }}</p>
            <span class="note-date">{{ n.createdAt?.slice(0, 10) }}</span>
          </div>
          <el-empty v-if="notes.length === 0" description="暂无笔记" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getKnowledgeTree, listNotes } from '@/api/note'

const treeData = ref([])
const notes = ref([])
const keyword = ref('')

onMounted(async () => {
  treeData.value = await getKnowledgeTree()
  fetchNotes()
})

async function fetchNotes() {
  notes.value = await listNotes(1, 50, keyword.value)
}

function handleNodeClick(data) {
  // Could filter notes by knowledge node
}
</script>

<style scoped>
.note-page { margin: 20px; }
.note-item { padding: 10px 0; border-bottom: 1px solid #eee; cursor: pointer; }
.note-item:hover { background: #f5f7fa; }
.note-title { margin: 0; font-size: 14px; }
.note-date { font-size: 12px; color: #909399; }
</style>
