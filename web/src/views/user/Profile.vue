<template>
  <div class="profile-page">
    <!-- 会员状态卡片 -->
    <el-card class="vip-card" :class="{ 'is-vip': vipInfo.isVip }">
      <div class="vip-header">
        <div class="vip-left">
          <el-avatar :size="60" :src="userStore.userInfo.avatarUrl || undefined" />
          <div class="vip-user">
            <h3>{{ userStore.userInfo.nickname || '用户' }}</h3>
            <el-tag :type="vipInfo.isVip ? 'warning' : 'info'" size="large">
              {{ vipInfo.isVip ? '👑 VIP会员' : '免费用户' }}
            </el-tag>
          </div>
        </div>
        <div class="vip-right">
          <div v-if="vipInfo.membershipExpire" class="expire-info">
            到期时间：{{ vipInfo.membershipExpire?.slice(0, 10) }}
          </div>
        </div>
      </div>

      <el-divider />

      <!-- 权益对比表 -->
      <el-table :data="vipInfo.benefits || []" stripe style="width:100%">
        <el-table-column prop="name" label="功能" width="140" />
        <el-table-column label="免费用户" width="140">
          <template #default="{ row }">
            <span :class="vipInfo.isVip ? 'free-dim' : ''">{{ row.free }}</span>
          </template>
        </el-table-column>
        <el-table-column label="VIP会员">
          <template #default="{ row }">
            <span class="vip-highlight">{{ row.vip }}</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 升级/续费 -->
      <div v-if="!vipInfo.isVip" class="upgrade-section">
        <el-divider />
        <h4>升级为VIP，解锁全部功能</h4>
        <div class="price-options">
          <el-radio-group v-model="upgradeMonths">
            <el-radio-button :value="1">1个月 ¥29.9</el-radio-button>
            <el-radio-button :value="3">3个月 ¥79.9</el-radio-button>
            <el-radio-button :value="6">6个月 ¥149.9</el-radio-button>
            <el-radio-button :value="12">12个月 ¥299.9</el-radio-button>
          </el-radio-group>
        </div>
        <el-button type="warning" size="large" @click="handleUpgrade" :loading="upgrading" class="upgrade-btn">
          {{ upgrading ? '处理中...' : '立即升级' }}
        </el-button>
      </div>

      <div v-else class="upgrade-section">
        <el-divider />
        <h4>续费VIP</h4>
        <div class="price-options">
          <el-radio-group v-model="upgradeMonths">
            <el-radio-button :value="1">1个月 ¥29.9</el-radio-button>
            <el-radio-button :value="3">3个月 ¥79.9</el-radio-button>
            <el-radio-button :value="6">6个月 ¥149.9</el-radio-button>
            <el-radio-button :value="12">12个月 ¥299.9</el-radio-button>
          </el-radio-group>
        </div>
        <el-button type="warning" size="large" @click="handleUpgrade" :loading="upgrading" class="upgrade-btn">续费</el-button>
      </div>
    </el-card>

    <!-- 信息卡片 -->
    <el-card style="margin-top:16px">
      <template #header>
        <span>账户信息</span>
        <el-button type="danger" size="small" style="float:right" @click="handleLogout">退出登录</el-button>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="手机号">{{ userStore.userInfo.phone || '未绑定' }}</el-descriptions-item>
        <el-descriptions-item label="本月AI调用">{{ userStore.userInfo.monthlyAiCallsUsed || 0 }} 次</el-descriptions-item>
        <el-descriptions-item label="本月申论批改">{{ userStore.userInfo.monthlyEssayUsed || 0 }} 次</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getVipInfo, upgradeMembership } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const vipInfo = ref({ benefits: [], isVip: false })
const upgradeMonths = ref(1)
const upgrading = ref(false)

onMounted(async () => {
  if (!userStore.userInfo.userId) await userStore.fetchUserInfo()
  await fetchVipInfo()
})

async function fetchVipInfo() {
  try { vipInfo.value = await getVipInfo() } catch (e) { /* ignore */ }
}

async function handleUpgrade() {
  upgrading.value = true
  try {
    const res = await upgradeMembership(upgradeMonths.value)
    ElMessage.success(res.message || '升级成功！')
    await userStore.fetchUserInfo()
    await fetchVipInfo()
  } finally {
    upgrading.value = false
  }
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.profile-page { max-width: 700px; margin: 20px auto; padding: 0 20px; }
.vip-card { border: 1px solid #ebeef5; }
.vip-card.is-vip { border-color: #e6a23c; }
.vip-header { display: flex; justify-content: space-between; align-items: center; }
.vip-left { display: flex; align-items: center; gap: 16px; }
.vip-user h3 { margin: 0 0 4px 0; }
.vip-right { text-align: right; }
.expire-info { font-size: 13px; color: #909399; }
.free-dim { color: #c0c4cc; text-decoration: line-through; }
.vip-highlight { color: #e6a23c; font-weight: bold; }
.upgrade-section { text-align: center; margin-top: 8px; }
.upgrade-section h4 { margin-bottom: 16px; }
.price-options { margin-bottom: 16px; }
.upgrade-btn { width: 200px; }
</style>
