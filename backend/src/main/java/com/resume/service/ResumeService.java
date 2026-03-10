package com.resume.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.resume.entity.Resume;
import com.resume.mapper.ResumeMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 简历服务
 */
@Service
public class ResumeService extends ServiceImpl<ResumeMapper, Resume> {
    
    // 简历文件存储路径
    private static final String UPLOAD_DIR = "/data/resumes/";
    
    /**
     * 上传简历
     */
    public Resume uploadResume(MultipartFile file, String candidateName, String phone, 
                               String email, String education, Integer experienceYears, 
                               String skills) throws IOException {
        // 确保目录存在
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : ".pdf";
        String fileName = UUID.randomUUID().toString() + extension;
        String filePath = UPLOAD_DIR + fileName;
        
        // 保存文件
        file.transferTo(new File(filePath));
        
        // 创建简历记录
        Resume resume = new Resume();
        resume.setFileName(originalFilename);
        resume.setFilePath(filePath);
        resume.setCandidateName(candidateName != null && !candidateName.isEmpty() ? candidateName : "未知");
        resume.setPhone(phone);
        resume.setEmail(email);
        resume.setEducation(education);
        resume.setExperienceYears(experienceYears);
        
        // 处理 skills 字段 - 转换为 JSON 数组格式
        if (skills != null && !skills.isEmpty()) {
            // 如果已经是 JSON 数组格式，直接使用
            if (skills.trim().startsWith("[")) {
                resume.setSkills(skills);
            } else {
                // 否则转换为 JSON 数组
                String[] skillArray = skills.split(",");
                StringBuilder jsonSkills = new StringBuilder("[");
                for (int i = 0; i < skillArray.length; i++) {
                    String skill = skillArray[i].trim();
                    if (!skill.isEmpty()) {
                        if (i > 0 && jsonSkills.length() > 1) {
                            jsonSkills.append(",");
                        }
                        jsonSkills.append("\"").append(skill).append("\"");
                    }
                }
                jsonSkills.append("]");
                resume.setSkills(jsonSkills.toString());
            }
        }
        
        save(resume);
        return resume;
    }
    
    /**
     * 根据学历查询简历
     */
    public List<Resume> getByEducation(String education) {
        return lambdaQuery()
                .eq(Resume::getEducation, education)
                .orderByDesc(Resume::getCreatedAt)
                .list();
    }
    
    /**
     * 根据工作年限范围查询
     */
    public List<Resume> getByExperienceRange(int minYears, int maxYears) {
        return lambdaQuery()
                .ge(Resume::getExperienceYears, minYears)
                .le(Resume::getExperienceYears, maxYears)
                .orderByDesc(Resume::getExperienceYears)
                .list();
    }
}
