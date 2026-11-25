import request from './request'

export function login(data) {
  return request.post('/auth/login', data)
}

export function getUserInfo() {
  return request.get('/user/profile')
}

export function getAiQuota() {
  return request.get('/ai/quota')
}

export function getVipInfo() {
  return request.get('/vip-info')
}

export function upgradeMembership(months) {
  return request.post(`/user/upgrade?months=${months}`)
}
