package com.resume.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.resume.entity.Resume;
import com.resume.mapper.ResumeMapper;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 简历服务
 */
@Service
public class ResumeService extends ServiceImpl<ResumeMapper, Resume> {
    
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
