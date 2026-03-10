package com.resume.service;

import com.alibaba.fastjson2.JSON;
import com.resume.dto.MatchResultDTO;
import com.resume.entity.JobPosition;
import com.resume.entity.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * RAG 服务 - 核心匹配逻辑
 */
@Service
public class RagService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;
    
    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    
    /**
     * 构建匹配 Prompt
     */
    public String buildMatchPrompt(Resume resume, JobPosition position) {
        return String.format("""
            你是一个专业的HR招聘助手。请分析以下简历与岗位要求的匹配度。
            
            ## 简历信息
            - 姓名：%s
            - 学历：%s
            - 工作年限：%d年
            - 技能：%s
            - 工作经历：%s
            
            ## 岗位要求
            - 岗位名称：%s
            - 部门：%s
            - 技能要求：%s
            - 最小工作年限：%d年
            - 岗位详细要求：%s
            
            ## 请输出
            1. 匹配度评分（0-100分）
            2. 匹配理由（说明为什么匹配或不匹配）
            3. 技能匹配详情（逐项分析技能匹配情况）
            
            请直接输出JSON格式，不要包含markdown代码块:
            {
              "score": 数字,
              "reason": "匹配理由",
              "skillMatch": {
                "matched": ["匹配的技能"],
                "missing": ["缺失的技能"],
                "analysis": "技能分析"
              }
            }
            """,
            resume.getCandidateName(),
            resume.getEducation(),
            resume.getExperienceYears() != null ? resume.getExperienceYears() : 0,
            resume.getSkills(),
            resume.getWorkHistory() != null ? resume.getWorkHistory() : "无",
            position.getTitle(),
            position.getDepartment(),
            position.getSkills(),
            position.getExperienceMin() != null ? position.getExperienceMin() : 0,
            position.getRequirements()
        );
    }
    
    /**
     * 调用 GLM API 获取匹配结果
     */
    public Map<String, Object> callLLM(String prompt, String apiKey) {
        Exception error = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            Map<String, Object> body = new HashMap<>();
            body.put("model", "glm-4");
            body.put("messages", List.of(Map.of("role", "user", "content", prompt)));
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(API_URL, request, Map.class);
            
            if (response != null && response.containsKey("choices")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    
                    // 提取 JSON（处理 markdown 代码块）
                    if (content.contains("```json")) {
                        content = content.substring(content.indexOf("```json") + 7);
                        content = content.substring(0, content.indexOf("```"));
                    } else if (content.contains("```")) {
                        content = content.substring(content.indexOf("```") + 3);
                        content = content.substring(0, content.lastIndexOf("```"));
                    }
                    content = content.trim();
                    
                    // 解析 JSON 响应
                    return JSON.parseObject(content, Map.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            error = e;
        }
        
        // 返回默认值
        Map<String, Object> defaultResult = new HashMap<>();
        defaultResult.put("score", 0);
        defaultResult.put("reason", "AI分析失败: " + (error != null ? error.getMessage() : "未知错误"));
        defaultResult.put("skillMatch", Map.of("matched", List.of(), "missing", List.of(), "analysis", "无法分析"));
        return defaultResult;
    }
    
    /**
     * 执行匹配分析
     */
    public MatchResultDTO analyzeMatch(Resume resume, JobPosition position, String apiKey) {
        String prompt = buildMatchPrompt(resume, position);
        Map<String, Object> result = callLLM(prompt, apiKey);
        
        MatchResultDTO dto = new MatchResultDTO();
        dto.setResumeId(resume.getId());
        dto.setCandidateName(resume.getCandidateName());
        dto.setPositionId(position.getId());
        dto.setPositionTitle(position.getTitle());
        
        Object scoreObj = result.get("score");
        if (scoreObj instanceof Number) {
            dto.setMatchScore(((Number) scoreObj).doubleValue());
        } else {
            dto.setMatchScore(0.0);
        }
        
        dto.setMatchReason((String) result.get("reason"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> skillMatch = (Map<String, Object>) result.get("skillMatch");
        if (skillMatch != null) {
            dto.setSkillMatch(JSON.toJSONString(skillMatch));
        }
        
        return dto;
    }
}
