package com.resume.service;

import com.resume.dto.MatchResultDTO;
import com.resume.entity.JobPosition;
import com.resume.entity.MatchRecord;
import com.resume.entity.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 匹配服务 - 整合 RAG 和 AI 分析
 */
@Service
public class MatchService {
    
    @Autowired
    private RagService ragService;
    
    @Autowired
    private ResumeService resumeService;
    
    @Autowired
    private JobPositionService jobPositionService;
    
    @Autowired
    private MatchRecordService matchRecordService;
    
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;
    
    /**
     * 执行单个简历与岗位的匹配
     */
    public MatchResultDTO match(Long resumeId, Long positionId) {
        Resume resume = resumeService.getById(resumeId);
        JobPosition position = jobPositionService.getById(positionId);
        
        if (resume == null || position == null) {
            throw new RuntimeException("简历或岗位不存在");
        }
        
        // 调用 RAG 服务进行匹配分析
        MatchResultDTO result = ragService.analyzeMatch(resume, position, apiKey);
        
        // 保存匹配记录
        MatchRecord record = new MatchRecord();
        record.setResumeId(resumeId);
        record.setPositionId(positionId);
        record.setMatchScore(BigDecimal.valueOf(result.getMatchScore()));
        record.setMatchReason(result.getMatchReason());
        record.setSkillMatch(result.getSkillMatch());
        record.setStatus("pending");
        matchRecordService.save(record);
        
        return result;
    }
    
    /**
     * 一个简历匹配所有岗位
     */
    public List<MatchResultDTO> matchAllPositions(Long resumeId) {
        Resume resume = resumeService.getById(resumeId);
        if (resume == null) {
            throw new RuntimeException("简历不存在");
        }
        
        List<JobPosition> positions = jobPositionService.getActivePositions();
        List<MatchResultDTO> results = new ArrayList<>();
        
        for (JobPosition position : positions) {
            try {
                MatchResultDTO result = ragService.analyzeMatch(resume, position, apiKey);
                
                // 保存匹配记录
                MatchRecord record = new MatchRecord();
                record.setResumeId(resumeId);
                record.setPositionId(position.getId());
                record.setMatchScore(BigDecimal.valueOf(result.getMatchScore()));
                record.setMatchReason(result.getMatchReason());
                record.setSkillMatch(result.getSkillMatch());
                record.setStatus("pending");
                matchRecordService.save(record);
                
                results.add(result);
            } catch (Exception e) {
                // 记录错误但继续处理其他岗位
                e.printStackTrace();
            }
        }
        
        // 按匹配度排序
        results.sort((a, b) -> Double.compare(
            b.getMatchScore() != null ? b.getMatchScore() : 0,
            a.getMatchScore() != null ? a.getMatchScore() : 0
        ));
        
        return results;
    }
    
    /**
     * 获取简历的所有匹配记录
     */
    public List<MatchRecord> getByResumeId(Long resumeId) {
        return matchRecordService.getByResumeId(resumeId);
    }
    
    /**
     * 获取岗位的所有匹配记录
     */
    public List<MatchRecord> getByPositionId(Long positionId) {
        return matchRecordService.getByPositionId(positionId);
    }
    
    /**
     * 获取所有匹配记录
     */
    public List<MatchRecord> list() {
        return matchRecordService.list();
    }
    
    /**
     * 获取高匹配记录（匹配度 >= 80）
     */
    public List<MatchRecord> getHighMatchRecords() {
        return matchRecordService.lambdaQuery()
                .ge(MatchRecord::getMatchScore, new BigDecimal("80"))
                .orderByDesc(MatchRecord::getMatchScore)
                .list();
    }
}
