package com.resume.controller;

import com.resume.dto.MatchResultDTO;
import com.resume.dto.Result;
import com.resume.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 匹配控制器
 */
@RestController
@RequestMapping("/api/match")
public class MatchController {
    
    @Autowired
    private MatchService matchService;
    
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
            @RequestParam(required = false) Long positionId) {
        if (resumeId != null) {
            return Result.success(matchService.getByResumeId(resumeId));
        }
        if (positionId != null) {
            return Result.success(matchService.getByPositionId(positionId));
        }
        return Result.success(matchService.list());
    }
}
