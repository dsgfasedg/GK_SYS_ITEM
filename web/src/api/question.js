import request from './request'

export function listQuestions(page = 1, size = 20, tag) {
  return request.get('/questions', { params: { page, size, tag } })
}

export function createQuestion(data) {
  return request.post('/questions', data)
}

export function deleteQuestion(questionId) {
  return request.delete(`/questions/${questionId}`)
}

export function startPractice(tagFilter, count = 10) {
  return request.post('/practice/start', null, { params: { tagFilter, count } })
}

export function getNextQuestion(sessionId) {
  return request.get(`/practice/${sessionId}/next`)
}

export function submitAnswer(sessionId, questionId, answer) {
  return request.post(`/practice/${sessionId}/submit`, null, { params: { questionId, answer } })
}

export function finishPractice(sessionId) {
  return request.post(`/practice/${sessionId}/finish`)
}

export function getPracticeReport(sessionId) {
  return request.get(`/practice/${sessionId}/report`)
}
