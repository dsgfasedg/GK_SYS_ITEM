import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const isLoggedIn = computed(() => !!token.value)
  const isVip = computed(() => userInfo.value?.membershipType === 'vip')

  async function login(phone, code) {
    const res = await loginApi({ phone, code })
    token.value = res.token
    userInfo.value = res
    localStorage.setItem('token', res.token)
    localStorage.setItem('userInfo', JSON.stringify(res))
    return res
  }

  async function fetchUserInfo() {
    const res = await getUserInfo()
    userInfo.value = res
    localStorage.setItem('userInfo', JSON.stringify(res))
    return res
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return { token, userInfo, isLoggedIn, isVip, login, fetchUserInfo, logout }
}, {
  persist: true,
})
