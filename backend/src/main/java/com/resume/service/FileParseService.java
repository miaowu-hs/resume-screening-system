package com.resume.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 文件解析服务
 */
@Service
public class FileParseService {
    
    /**
     * 解析 PDF 文件
     */
    public String parsePdf(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             PDDocument document = PDDocument.load(is)) {
            
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    
    /**
     * 解析 Word 文件
     */
    public String parseWord(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             XWPFDocument document = new XWPFDocument(is)) {
            
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            StringBuilder content = new StringBuilder();
            
            for (XWPFParagraph para : paragraphs) {
                content.append(para.getText()).append("\n");
            }
            
            return content.toString();
        }
    }
    
    /**
     * 根据文件类型自动解析
     */
    public String parseFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IOException("文件名不能为空");
        }
        
        String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        
        switch (extension) {
            case ".pdf":
                return parsePdf(file);
            case ".doc":
            case ".docx":
                return parseWord(file);
            default:
                throw new IOException("不支持的文件格式: " + extension);
        }
    }
    
    /**
     * 从解析内容中提取基本信息
     */
    public ResumeInfo extractInfo(String content) {
        ResumeInfo info = new ResumeInfo();
        
        // TODO: 使用正则或 AI 提取结构化信息
        // 这里先做简单实现
        
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            
            // 提取姓名（第一行通常是姓名）
            if (info.candidateName == null && line.length() >= 2 && line.length() <= 10) {
                info.candidateName = line;
            }
            
            // 提取电话
            if (line.contains("电话") || line.contains("手机") || line.contains("联系方式")) {
                String phone = line.replaceAll("[^0-9-]", "");
                if (phone.length() >= 7) {
                    info.phone = phone;
                }
            }
            
            // 提取邮箱
            if (line.contains("@") && line.contains(".")) {
                String email = line.replaceAll("[^a-zA-Z0-9@._-]", "");
                if (email.contains("@")) {
                    info.email = email;
                }
            }
            
            // 提取学历
            if (line.contains("本科") || line.contains("学士")) {
                info.education = "本科";
            } else if (line.contains("硕士") || line.contains("研究生")) {
                info.education = "硕士";
            } else if (line.contains("博士")) {
                info.education = "博士";
            } else if (line.contains("大专") || line.contains("专科")) {
                info.education = "大专";
            }
        }
        
        info.fullContent = content;
        return info;
    }
    
    /**
     * 简历信息临时对象
     */
    public static class ResumeInfo {
        public String candidateName;
        public String phone;
        public String email;
        public String education;
        public Integer experienceYears;
        public String fullContent;
    }
}
