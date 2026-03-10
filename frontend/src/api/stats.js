import api from './index'

// 获取统计数据
export const getStats = () => api.get('/stats/overview')

// 获取趋势数据
export const getTrend = () => api.get('/stats/trend')
