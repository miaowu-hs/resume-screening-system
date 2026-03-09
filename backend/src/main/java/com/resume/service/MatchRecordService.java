package com.resume.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.resume.entity.MatchRecord;
import com.resume.mapper.MatchRecordMapper;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 匹配记录服务
 */
@Service
public class MatchRecordService extends ServiceImpl<MatchRecordMapper, MatchRecord> {
    
    /**
     * 获取简历的所有匹配记录
     */
    public List<MatchRecord> getByResumeId(Long resumeId) {
        return lambdaQuery()
                .eq(MatchRecord::getResumeId, resumeId)
                .orderByDesc(MatchRecord::getMatchScore)
                .list();
    }
    
    /**
     * 获取岗位的所有匹配记录
     */
    public List<MatchRecord> getByPositionId(Long positionId) {
        return lambdaQuery()
                .eq(MatchRecord::getPositionId, positionId)
                .orderByDesc(MatchRecord::getMatchScore)
                .list();
    }
    
    /**
     * 获取高匹配度记录
     */
    public List<MatchRecord> getHighMatchRecords(double minScore) {
        return lambdaQuery()
                .ge(MatchRecord::getMatchScore, minScore)
                .orderByDesc(MatchRecord::getMatchScore)
                .list();
    }
}
