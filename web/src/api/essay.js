import request from './request'

export function submitEssay(questionText, essayText) {
  return request.post(`/essay/correct?questionText=${encodeURIComponent(questionText)}&essayText=${encodeURIComponent(essayText)}`)
}

export function getEssayHistory(page = 1, size = 20) {
  return request.get('/essay/history', { params: { page, size } })
}

export function getEssayDetail(correctionId) {
  return request.get(`/essay/${correctionId}`)
}
