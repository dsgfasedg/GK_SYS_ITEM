import request from './request'

export function getAiQuota() {
  return request.get('/ai/quota')
}
