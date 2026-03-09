package com.resume.controller;

import com.resume.dto.Result;
import com.resume.entity.Resume;
import com.resume.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 简历控制器
 */
@RestController
@RequestMapping("/api/resumes")
public class ResumeController {
    
    @Autowired
    private ResumeService resumeService;
    
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
     * 删除简历
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        resumeService.removeById(id);
        return Result.success();
    }
}
