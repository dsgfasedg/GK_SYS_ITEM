import request from './request'

export function getKnowledgeTree() {
  return request.get('/notes/knowledge-tree')
}

export function listNotes(page = 1, size = 20, keyword) {
  return request.get('/notes', { params: { page, size, keyword } })
}

export function getNoteDetail(noteId) {
  return request.get(`/notes/${noteId}`)
}

export function createNote(type, rawContent) {
  return request.post(`/notes?type=${type}&rawContent=${encodeURIComponent(rawContent)}`)
}

export function updateNote(noteId, formattedContent, tags) {
  return request.patch(`/notes/${noteId}`, null, { params: { formattedContent, tags } })
}

export function deleteNote(noteId) {
  return request.delete(`/notes/${noteId}`)
}
