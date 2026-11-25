import request from '../utils/request';

export const authApi = {
  login: (data: any) => request.post('/auth/login', data),
  getProfile: () => request.get('/user/profile'),
  getAiQuota: () => request.get('/user/ai-quota'),
  checkMembership: () => request.post('/user/check-membership'),
  health: () => request.get('/health'),
};

export const planApi = {
  createTask: (data: any) => request.post('/plans/tasks', data),
  getDayView: (date: string) => request.get(`/plans/tasks?date=${date}&view=day`),
  getWeekView: () => request.get('/plans/tasks?view=week'),
  getStats: (period: string = 'week') => request.get(`/plans/stats?period=${period}`),
  updateTask: (taskId: number, data: any) => request.patch(`/plans/tasks/${taskId}`, data),
  completeTask: (taskId: number, actualDuration?: number) => 
    request.post(`/plans/tasks/${taskId}/complete`, null, { actualDuration }),
  delayTask: (taskId: number, delayType: string) => 
    request.post(`/plans/tasks/${taskId}/delay`, null, { delayType }),
  deleteTask: (taskId: number) => request.delete(`/plans/tasks/${taskId}`),
};