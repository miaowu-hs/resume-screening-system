package com.resume.controller;

import com.resume.dto.MatchRecordDTO;
import com.resume.dto.Result;
import com.resume.entity.JobPosition;
import com.resume.entity.MatchRecord;
import com.resume.entity.Resume;
import com.resume.mapper.JobPositionMapper;
import com.resume.mapper.MatchRecordMapper;
import com.resume.mapper.ResumeMapper;
import com.resume.service.JobPositionService;
import com.resume.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    
    @Autowired
    private ResumeService resumeService;
    
    @Autowired
    private JobPositionService jobPositionService;

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
                .filter(m -> m.getMatchScore() != null && m.getMatchScore().compareTo(new BigDecimal("80")) >= 0)
                .count();
        stats.put("highMatchCount", highMatchCount);
        
        return Result.success(stats);
    }
    
    /**
     * 获取最近上传的简历（前5条）
     */
    @GetMapping("/recent-resumes")
    public Result<List<Resume>> getRecentResumes() {
        List<Resume> resumes = resumeMapper.selectList(null).stream()
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(5)
                .collect(Collectors.toList());
        return Result.success(resumes);
    }
    
    /**
     * 获取匹配度 TOP 5
     */
    @GetMapping("/top-matches")
    public Result<List<MatchRecordDTO>> getTopMatches() {
        List<MatchRecord> records = matchRecordMapper.selectList(null).stream()
                .filter(m -> m.getMatchScore() != null)
                .sorted((a, b) -> b.getMatchScore().compareTo(a.getMatchScore()))
                .limit(5)
                .collect(Collectors.toList());
        
        // 转换为 DTO，关联人名和岗位名
        List<MatchRecordDTO> dtos = records.stream().map(record -> {
            MatchRecordDTO dto = new MatchRecordDTO();
            dto.setId(record.getId());
            dto.setResumeId(record.getResumeId());
            dto.setPositionId(record.getPositionId());
            dto.setMatchScore(record.getMatchScore());
            
            Resume resume = resumeService.getById(record.getResumeId());
            if (resume != null) {
                dto.setCandidateName(resume.getCandidateName());
            }
            
            JobPosition position = jobPositionService.getById(record.getPositionId());
            if (position != null) {
                dto.setPositionTitle(position.getTitle());
            }
            
            return dto;
        }).collect(Collectors.toList());
        
        return Result.success(dtos);
    }
}
