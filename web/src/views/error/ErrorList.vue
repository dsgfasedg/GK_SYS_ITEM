<template>
  <div class="error-page">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="错题列表" name="list">
        <el-card>
          <template #header>
            <span>错题列表</span>
            <el-button type="primary" size="small" style="float:right" @click="showCreateDialog = true">添加错题（支持图片）</el-button>
          </template>
          <div v-for="e in errors" :key="e.errorId" class="error-item" @click="showErrorDetail(e)">
            <p class="error-stem">{{ e.stem?.slice(0, 60) || '无' }}</p>
            <div class="error-meta">
              <el-tag size="small">{{ e.source }}</el-tag>
              <el-progress :percentage="e.mastery || 0" :width="60" type="circle" :stroke-width="6" />
            </div>
          </div>
          <el-empty v-if="errors.length === 0" description="暂无错题" />
        </el-card>
      </el-tab-pane>
      <el-tab-pane label="今日复习" name="review">
        <el-card v-if="reviewList.length > 0">
          <div v-for="(item, idx) in reviewList" :key="item.errorId" class="review-item">
            <h4>{{ idx + 1 }}. {{ item.stem?.slice(0, 80) }}</h4>
            <div class="review-actions">
              <el-button type="success" size="small" @click="handleReview(item, true)">答对了</el-button>
              <el-button type="danger" size="small" @click="handleReview(item, false)">答错了</el-button>
            </div>
          </div>
        </el-card>
        <el-empty v-else description="今日无待复习错题" />
      </el-tab-pane>
      <el-tab-pane label="常错看板" name="stats">
        <el-card><div ref="chartRef" style="height:400px"></div></el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- Create Error Dialog with Image Upload -->
    <el-dialog v-model="showCreateDialog" title="添加错题" width="600px">
      <el-tabs v-model="createTab">
        <el-tab-pane label="手动录入" name="manual">
          <el-form label-width="80px">
            <el-form-item label="题干"><el-input type="textarea" v-model="createForm.stem" :rows="3" /></el-form-item>
            <el-form-item label="正确答案"><el-input v-model="createForm.correctAnswer" style="width:100px" /></el-form-item>
            <el-form-item label="你的答案"><el-input v-model="createForm.userAnswer" style="width:100px" /></el-form-item>
            <el-form-item label="错因">
              <el-select v-model="createForm.userErrorTag">
                <el-option label="知识盲区" value="知识盲区" /><el-option label="审题失误" value="审题失误" />
                <el-option label="计算错误" value="计算错误" /><el-option label="方法错误" value="方法错误" />
              </el-select>
            </el-form-item>
            <el-form-item label="知识点"><el-input v-model="createForm.knowledgeTags" placeholder="如:行测-言语理解-中心理解" /></el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="图片上传" name="image">
          <el-upload ref="uploadRef" :auto-upload="false" list-type="picture-card" accept="image/*" multiple>
            <el-icon><Plus /></el-icon>
          </el-upload>
          <p style="color:#909399;font-size:12px;margin-top:8px">上传错题截图，后续可对接AI自动识别题干和答案</p>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">保存</el-button>
      </template>
    </el-dialog>

    <!-- Detail Dialog -->
    <el-dialog v-model="showDetail" title="错题详情" width="600px">
      <p><b>题干：</b>{{ currentError.stem }}</p>
      <p v-if="currentError.imageUrl"><b>图片：</b><el-image :src="currentError.imageUrl" style="max-width:100%;max-height:200px" /></p>
      <p><b>答案：</b>{{ currentError.correctAnswer }}（你的答案：{{ currentError.userAnswer }}）</p>
      <p><b>解析：</b>{{ currentError.analysis }}</p>
      <p><b>掌握度：</b><el-progress :percentage="currentError.mastery || 0" :stroke-width="10" style="width:200px" /></p>
      <el-select v-model="currentError.userErrorTag" placeholder="选择错因" @change="updateTag(currentError)">
        <el-option label="知识盲区" value="知识盲区" /><el-option label="审题失误" value="审题失误" />
        <el-option label="计算错误" value="计算错误" /><el-option label="方法错误" value="方法错误" />
      </el-select>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { listErrors, getTodayReview, submitReview, updateErrorTag, getErrorStats, createError } from '@/api/error'
import * as echarts from 'echarts'

const activeTab = ref('list')
const createTab = ref('manual')
const errors = ref([])
const reviewList = ref([])
const showDetail = ref(false)
const showCreateDialog = ref(false)
const currentError = ref({})
const chartRef = ref(null)
const createForm = ref({ stem: '', correctAnswer: '', userAnswer: '', userErrorTag: '', knowledgeTags: '' })

onMounted(() => fetchErrors())

async function fetchErrors() { errors.value = await listErrors() }
async function fetchReview() { reviewList.value = await getTodayReview() }

async function handleCreate() {
  await createError({ ...createForm.value, source: 'manual' })
  ElMessage.success('添加成功')
  showCreateDialog.value = false
  createForm.value = { stem: '', correctAnswer: '', userAnswer: '', userErrorTag: '', knowledgeTags: '' }
  fetchErrors()
}

async function handleReview(item, correct) {
  await submitReview(item.errorId, correct)
  ElMessage.success(correct ? '继续加油！' : '已记录！')
  fetchReview()
}

function showErrorDetail(e) {
  currentError.value = { ...e }
  showDetail.value = true
}

async function updateTag(e) {
  await updateErrorTag(e.errorId, e.userErrorTag)
  ElMessage.success('标签已更新')
}

watch(activeTab, async (val) => {
  if (val === 'review') fetchReview()
  if (val === 'stats') {
    await nextTick()
    const stats = await getErrorStats()
    if (chartRef.value && stats.length > 0) {
      const chart = echarts.init(chartRef.value)
      chart.setOption({
        title: { text: '常错考点统计' },
        xAxis: { type: 'category', data: stats.map(s => s.tag) },
        yAxis: { type: 'value' },
        series: [{ type: 'bar', data: stats.map(s => s.count), itemStyle: { color: '#409eff' } }],
      })
    }
  }
})
</script>

<style scoped>
.error-page { max-width: 900px; margin: 20px auto; padding: 0 20px; }
.error-item { padding: 12px 0; border-bottom: 1px solid #eee; cursor: pointer; display: flex; justify-content: space-between; align-items: center; }
.error-item:hover { background: #f5f7fa; }
.error-stem { margin: 0; font-size: 14px; flex: 1; }
.error-meta { display: flex; align-items: center; gap: 12px; }
.review-item { padding: 16px 0; border-bottom: 1px solid #eee; }
.review-actions { margin-top: 8px; display: flex; gap: 8px; }
</style>
