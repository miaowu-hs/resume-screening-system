import api from './index'

// 执行匹配
export const executeMatch = (resumeId, positionId) => 
  api.post('/match/execute', null, { params: { resumeId, positionId } })

// 批量匹配
export const batchMatch = (resumeId) => 
  api.post('/match/batch', null, { params: { resumeId } })

// 获取匹配结果
export const getMatchResults = (params) => api.get('/match/result', { params })

// 获取高匹配结果
export const getHighMatchResults = () => api.get('/match/result', { params: { highMatch: true } })
