package com.resume.controller;

import com.resume.dto.Result;
import com.resume.mapper.JobPositionMapper;
import com.resume.mapper.MatchRecordMapper;
import com.resume.mapper.ResumeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计数据控制器
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private JobPositionMapper jobPositionMapper;

    @Autowired
    private MatchRecordMapper matchRecordMapper;

    /**
     * 获取概览统计数据
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> stats = new HashMap<>();
        
        // 简历总数
        stats.put("resumeCount", resumeMapper.selectCount(null));
        
        // 岗位总数
        stats.put("positionCount", jobPositionMapper.selectCount(null));
        
        // 匹配次数
        stats.put("matchCount", matchRecordMapper.selectCount(null));
        
        // 高匹配数（匹配度 >= 80%）
        long highMatchCount = matchRecordMapper.selectList(null).stream()
                .filter(m -> m.getMatchScore() != null && m.getMatchScore().compareTo(new java.math.BigDecimal("80")) >= 0)
                .count();
        stats.put("highMatchCount", highMatchCount);
        
        return Result.success(stats);
    }
}
