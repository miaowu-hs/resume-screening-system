import api from './index'

// 获取岗位列表
export const getPositions = () => api.get('/positions')

// 获取岗位详情
export const getPosition = (id) => api.get(`/positions/${id}`)

// 创建岗位
export const createPosition = (data) => api.post('/positions', data)

// 更新岗位
export const updatePosition = (id, data) => api.put(`/positions/${id}`, data)

// 删除岗位
export const deletePosition = (id) => api.delete(`/positions/${id}`)
