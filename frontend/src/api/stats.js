import api from './index'

// 获取统计数据
export const getStats = () => api.get('/stats/overview')

// 获取趋势数据
export const getTrend = () => api.get('/stats/trend')

// 获取最近上传的简历
export const getRecentResumes = () => api.get('/stats/recent-resumes')

// 获取匹配度 TOP 5
export const getTopMatches = () => api.get('/stats/top-matches')
