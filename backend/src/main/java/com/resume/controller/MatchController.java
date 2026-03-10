package com.resume.controller;

import com.resume.dto.MatchRecordDTO;
import com.resume.dto.MatchResultDTO;
import com.resume.dto.Result;
import com.resume.entity.JobPosition;
import com.resume.entity.MatchRecord;
import com.resume.entity.Resume;
import com.resume.service.MatchService;
import com.resume.service.ResumeService;
import com.resume.service.JobPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 匹配控制器
 */
@RestController
@RequestMapping("/api/match")
public class MatchController {
    
    @Autowired
    private MatchService matchService;
    
    @Autowired
    private ResumeService resumeService;
    
    @Autowired
    private JobPositionService jobPositionService;
    
    /**
     * 执行简历与岗位的匹配
     */
    @PostMapping("/execute")
    public Result<MatchResultDTO> executeMatch(
            @RequestParam Long resumeId,
            @RequestParam Long positionId) {
        MatchResultDTO result = matchService.match(resumeId, positionId);
        return Result.success(result);
    }
    
    /**
     * 批量匹配：一个简历匹配所有岗位
     */
    @PostMapping("/batch")
    public Result<?> batchMatch(@RequestParam Long resumeId) {
        return Result.success(matchService.matchAllPositions(resumeId));
    }
    
    /**
     * 获取匹配结果列表
     */
    @GetMapping("/result")
    public Result<?> getResults(
            @RequestParam(required = false) Long resumeId,
            @RequestParam(required = false) Long positionId,
            @RequestParam(required = false, defaultValue = "false") Boolean highMatch) {
        
        List<MatchRecord> records;
        
        if (highMatch) {
            // 获取高匹配记录
            records = matchService.getHighMatchRecords();
        } else if (resumeId != null) {
            records = matchService.getByResumeId(resumeId);
        } else if (positionId != null) {
            records = matchService.getByPositionId(positionId);
        } else {
            records = matchService.list();
        }
        
        // 转换为包含人名和岗位名称的 DTO
        List<MatchRecordDTO> dtos = records.stream().map(this::toDTO).collect(Collectors.toList());
        return Result.success(dtos);
    }
    
    /**
     * 转换为 DTO（包含人名和岗位名称）
     */
    private MatchRecordDTO toDTO(MatchRecord record) {
        MatchRecordDTO dto = new MatchRecordDTO();
        dto.setId(record.getId());
        dto.setResumeId(record.getResumeId());
        dto.setPositionId(record.getPositionId());
        dto.setMatchScore(record.getMatchScore());
        dto.setMatchReason(record.getMatchReason());
        dto.setSkillMatch(record.getSkillMatch());
        dto.setStatus(record.getStatus());
        dto.setCreatedAt(record.getCreatedAt());
        
        // 关联查询人名
        Resume resume = resumeService.getById(record.getResumeId());
        if (resume != null) {
            dto.setCandidateName(resume.getCandidateName());
        }
        
        // 关联查询岗位名称
        JobPosition position = jobPositionService.getById(record.getPositionId());
        if (position != null) {
            dto.setPositionTitle(position.getTitle());
        }
        
        return dto;
    }
}
