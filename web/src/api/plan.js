import request from './request'

export function getTasks(date, view = 'day', startDate, endDate) {
  const params = { view }
  if (date) params.date = date
  if (startDate) params.startDate = startDate
  if (endDate) params.endDate = endDate
  return request.get('/plans/tasks', { params })
}

export function createTask(data) {
  return request.post('/plans/tasks', data)
}

export function updateTask(taskId, data) {
  return request.patch(`/plans/tasks/${taskId}`, data)
}

export function completeTask(taskId, actualDuration) {
  return request.post(`/plans/tasks/${taskId}/complete`, null, {
    params: { actualDuration },
  })
}

export function delayTask(taskId, delayType) {
  return request.post(`/plans/tasks/${taskId}/delay`, null, {
    params: { delayType },
  })
}

export function deleteTask(taskId) {
  return request.delete(`/plans/tasks/${taskId}`)
}

export function getTaskStats(period = 'week') {
  return request.get('/plans/stats', { params: { period } })
}

export function getTaskRecords(startDate, endDate) {
  return getTasks(null, 'record', startDate, endDate)
}

export function aiGeneratePlan(situation) {
  return request.post('/plans/ai-generate', { situation })
}

export function aiImportPlan(plans) {
  return request.post('/plans/ai-import', plans)
}
