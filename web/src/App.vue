<template>
  <div id="app-layout" v-if="showLayout">
    <el-container style="height: 100vh">
      <el-aside width="200px" class="app-sidebar">
        <div class="sidebar-header">公考智学助手</div>
        <el-menu :router="true" :default-active="route.path" class="sidebar-menu">
          <el-menu-item index="/plan">
            <el-icon><List /></el-icon>
            <span>计划管理</span>
          </el-menu-item>
          <el-menu-item index="/plan/records">
            <el-icon><Timer /></el-icon>
            <span>任务记录</span>
          </el-menu-item>
          <el-menu-item index="/plan/ai">
            <el-icon><MagicStick /></el-icon>
            <span>AI 规划</span>
          </el-menu-item>
          <el-menu-item index="/note">
            <el-icon><Document /></el-icon>
            <span>智能笔记</span>
          </el-menu-item>
          <el-menu-item index="/error">
            <el-icon><WarningFilled /></el-icon>
            <span>错题本</span>
          </el-menu-item>
          <el-menu-item index="/essay">
            <el-icon><Edit /></el-icon>
            <span>申论批改</span>
          </el-menu-item>
          <el-menu-item index="/question">
            <el-icon><Files /></el-icon>
            <span>题库管理</span>
          </el-menu-item>
          <el-menu-item index="/practice">
            <el-icon><Notebook /></el-icon>
            <span>练习模式</span>
          </el-menu-item>
          <el-divider />
          <el-menu-item index="/user">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
          <el-menu-item index="/ai/usage">
            <el-icon><DataBoard /></el-icon>
            <span>AI 调用统计</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </div>
  <router-view v-else />
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const showLayout = computed(() => route.meta.requiresAuth !== false && userStore.isLoggedIn)
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
html, body, #app { height: 100%; font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', sans-serif; }
.app-sidebar { background: #304156; overflow-y: auto; }
.sidebar-header { height: 60px; line-height: 60px; text-align: center; color: #fff; font-size: 16px; font-weight: bold; background: #263445; }
.sidebar-menu { border-right: none; }
.app-main { background: #f0f2f5; padding: 0; overflow-y: auto; }
</style>
