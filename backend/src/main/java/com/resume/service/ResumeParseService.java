package com.resume.service;

import com.alibaba.fastjson2.JSON;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * 简历解析服务
 */
@Service
public class ResumeParseService {
    
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    
    /**
     * 解析简历文件
     */
    public Map<String, Object> parseResume(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        String content;
        
        // 根据文件类型提取文本
        if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
            content = extractPdfText(file);
        } else if (filename != null && (filename.toLowerCase().endsWith(".doc") || filename.toLowerCase().endsWith(".docx"))) {
            content = extractWordText(file);
        } else {
            throw new IllegalArgumentException("不支持的文件格式，请上传 PDF 或 Word 文档");
        }
        
        // 调用 AI 解析
        return parseResumeByAI(content);
    }
    
    /**
     * 提取 PDF 文本
     */
    private String extractPdfText(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    
    /**
     * 提取 Word 文本
     */
    private String extractWordText(MultipartFile file) throws IOException {
        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            StringBuilder sb = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                sb.append(paragraph.getText()).append("\n");
            }
            return sb.toString();
        }
    }
    
    /**
     * 调用 AI 解析简历内容
     */
    private Map<String, Object> parseResumeByAI(String content) {
        String prompt = String.format("""
            请解析以下简历内容，提取关键信息。简历内容：
            
            %s
            
            请以 JSON 格式输出以下字段：
            {
              "candidateName": "姓名",
              "phone": "电话号码",
              "email": "邮箱地址",
              "education": "学历（高中/大专/本科/硕士/博士）",
              "experienceYears": 工作年限（数字，没有填0）,
              "skills": "技能列表，用逗号分隔",
              "workHistory": "工作经历摘要"
            }
            
            注意：
            1. 如果某个字段无法从简历中提取，请填 null
            2. 电话号码只需要数字部分
            3. 学历请标准化为：高中、大专、本科、硕士、博士
            4. 只输出 JSON，不要其他内容
            """, content);
        
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
                    String resultContent = (String) message.get("content");
                    
                    // 提取 JSON
                    if (resultContent.contains("```json")) {
                        resultContent = resultContent.substring(resultContent.indexOf("```json") + 7);
                        resultContent = resultContent.substring(0, resultContent.indexOf("```"));
                    } else if (resultContent.contains("```")) {
                        resultContent = resultContent.substring(resultContent.indexOf("```") + 3);
                        resultContent = resultContent.substring(0, resultContent.lastIndexOf("```"));
                    }
                    resultContent = resultContent.trim();
                    
                    return JSON.parseObject(resultContent, Map.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new HashMap<>();
    }
}
