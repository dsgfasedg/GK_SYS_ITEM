import request from './request'

export function listErrors(page = 1, size = 20, tag) {
  return request.get('/errors', { params: { page, size, tag } })
}

export function getErrorDetail(errorId) {
  return request.get(`/errors/${errorId}`)
}

export function createError(data) {
  return request.post('/errors', data)
}

export function getTodayReview() {
  return request.get('/errors/review/today')
}

export function submitReview(errorId, correct) {
  return request.post(`/errors/${errorId}/review?correct=${correct}`)
}

export function updateErrorTag(errorId, tag) {
  return request.patch(`/errors/${errorId}/tag?tag=${encodeURIComponent(tag)}`)
}

export function deleteError(errorId) {
  return request.delete(`/errors/${errorId}`)
}

export function getErrorStats() {
  return request.get('/errors/stats')
}
