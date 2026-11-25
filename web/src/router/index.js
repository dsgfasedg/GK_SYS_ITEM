import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/user/Login.vue'), meta: { requiresAuth: false } },
  { path: '/', redirect: '/plan' },
  { path: '/plan', name: 'Plan', component: () => import('@/views/plan/PlanCalendar.vue'), meta: { requiresAuth: true } },
  { path: '/plan/records', name: 'TaskRecords', component: () => import('@/views/record/TaskRecords.vue'), meta: { requiresAuth: true } },
  { path: '/plan/ai', name: 'AiPlan', component: () => import('@/views/aiplan/AiPlan.vue'), meta: { requiresAuth: true } },
  { path: '/user', name: 'Profile', component: () => import('@/views/user/Profile.vue'), meta: { requiresAuth: true } },
  // Notes
  { path: '/note', name: 'NoteList', component: () => import('@/views/note/NoteList.vue'), meta: { requiresAuth: true } },
  { path: '/note/create', name: 'CreateNote', component: () => import('@/views/note/CreateNote.vue'), meta: { requiresAuth: true } },
  { path: '/note/:noteId', name: 'NoteDetail', component: () => import('@/views/note/NoteDetail.vue'), meta: { requiresAuth: true } },
  // Errors
  { path: '/error', name: 'ErrorList', component: () => import('@/views/error/ErrorList.vue'), meta: { requiresAuth: true } },
  // Essay
  { path: '/essay', name: 'EssaySubmit', component: () => import('@/views/essay/EssaySubmit.vue'), meta: { requiresAuth: true } },
  // AI Usage
  { path: '/ai/usage', name: 'UsageLogs', component: () => import('@/views/ai/UsageLogs.vue'), meta: { requiresAuth: true } },
  // Questions
  { path: '/question', name: 'QuestionList', component: () => import('@/views/question/QuestionList.vue'), meta: { requiresAuth: true } },
  // Practice
  { path: '/practice', name: 'Practice', component: () => import('@/views/practice/PracticeSession.vue'), meta: { requiresAuth: true } },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLoggedIn) next('/login')
  else next()
})

export default router
