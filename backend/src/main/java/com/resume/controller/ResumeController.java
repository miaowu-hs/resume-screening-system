package com.resume.controller;

import com.resume.dto.Result;
import com.resume.entity.Resume;
import com.resume.service.ResumeService;
import com.resume.service.ResumeParseService;
import com.resume.service.MatchRecordService;
import com.resume.entity.MatchRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 简历控制器
 */
@RestController
@RequestMapping("/api/resumes")
public class ResumeController {
    
    @Autowired
    private ResumeService resumeService;
    
    @Autowired
    private ResumeParseService resumeParseService;
    
    @Autowired
    private MatchRecordService matchRecordService;
    
    /**
     * 获取简历列表
     */
    @GetMapping
    public Result<List<Resume>> list() {
        return Result.success(resumeService.list());
    }
    
    /**
     * 获取简历详情
     */
    @GetMapping("/{id}")
    public Result<Resume> getById(@PathVariable Long id) {
        return Result.success(resumeService.getById(id));
    }
    
    /**
     * 解析简历文件（AI提取信息）
     */
    @PostMapping("/parse")
    public Result<Map<String, Object>> parseResume(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = resumeParseService.parseResume(file);
            
            // 检查缺失字段
            List<String> missingFields = new java.util.ArrayList<>();
            if (result.get("candidateName") == null || result.get("candidateName").toString().isEmpty()) {
                missingFields.add("姓名");
            }
            if (result.get("phone") == null || result.get("phone").toString().isEmpty()) {
                missingFields.add("电话");
            }
            if (result.get("email") == null || result.get("email").toString().isEmpty()) {
                missingFields.add("邮箱");
            }
            
            // 添加缺失字段提示
            if (!missingFields.isEmpty()) {
                result.put("parseMessage", "简历中未找到：" + String.join("、", missingFields) + "，请手动补充");
                result.put("hasMissingFields", true);
            } else {
                result.put("parseMessage", "AI已成功解析简历信息");
                result.put("hasMissingFields", false);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(500, "解析失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传简历
     */
    @PostMapping("/upload")
    public Result<Resume> upload(@RequestParam("file") MultipartFile file,
                                  @RequestParam(value = "candidateName", required = false) String candidateName,
                                  @RequestParam(value = "phone", required = false) String phone,
                                  @RequestParam(value = "email", required = false) String email,
                                  @RequestParam(value = "education", required = false) String education,
                                  @RequestParam(value = "experienceYears", required = false) Integer experienceYears,
                                  @RequestParam(value = "skills", required = false) String skills) {
        try {
            Resume resume = resumeService.uploadResume(file, candidateName, phone, email, education, experienceYears, skills);
            return Result.success(resume);
        } catch (Exception e) {
            return Result.error(500, "上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除简历（同时删除关联的匹配记录）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            // 先删除关联的匹配记录
            List<MatchRecord> matchRecords = matchRecordService.getByResumeId(id);
            for (MatchRecord record : matchRecords) {
                matchRecordService.removeById(record.getId());
            }
            
            // 再删除简历
            resumeService.removeById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(500, "删除失败: " + e.getMessage());
        }
    }
}
