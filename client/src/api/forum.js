import request from './request'

export function getForumPosts(params) {
  return request.get('/api/forum', { params })
}

export function getForumPost(id) {
  return request.get(`/api/forum/${id}`)
}

export function createPost(data) {
  return request.post('/api/forum', data)
}

export function updatePost(id, data) {
  return request.put(`/api/forum/${id}`, data)
}

export function deletePost(id) {
  return request.delete(`/api/forum/${id}`)
}

export function addReply(postId, data) {
  return request.post(`/api/forum/${postId}/replies`, data)
}

export function deleteReply(replyId) {
  return request.delete(`/api/forum/replies/${replyId}`)
}
