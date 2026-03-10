import api from './index'

// 获取简历列表
export const getResumes = () => api.get('/resumes')

// 获取简历详情
export const getResume = (id) => api.get(`/resumes/${id}`)

// 解析简历（AI提取信息）
export const parseResume = (formData) => api.post('/resumes/parse', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})

// 上传简历
export const uploadResume = (formData) => api.post('/resumes/upload', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})

// 删除简历
export const deleteResume = (id) => api.delete(`/resumes/${id}`)
