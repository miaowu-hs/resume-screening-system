package com.resume.controller;

import com.resume.dto.Result;
import com.resume.entity.JobPosition;
import com.resume.service.JobPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 岗位控制器
 */
@RestController
@RequestMapping("/api/positions")
public class JobPositionController {
    
    @Autowired
    private JobPositionService jobPositionService;
    
    /**
     * 获取所有招聘中的岗位
     */
    @GetMapping
    public Result<List<JobPosition>> list() {
        return Result.success(jobPositionService.getActivePositions());
    }
    
    /**
     * 获取岗位详情
     */
    @GetMapping("/{id}")
    public Result<JobPosition> getById(@PathVariable Long id) {
        return Result.success(jobPositionService.getById(id));
    }
    
    /**
     * 创建岗位
     */
    @PostMapping
    public Result<JobPosition> create(@RequestBody JobPosition position) {
        jobPositionService.save(position);
        return Result.success(position);
    }
    
    /**
     * 更新岗位
     */
    @PutMapping("/{id}")
    public Result<JobPosition> update(@PathVariable Long id, @RequestBody JobPosition position) {
        position.setId(id);
        jobPositionService.updateById(position);
        return Result.success(position);
    }
    
    /**
     * 删除岗位
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        jobPositionService.removeById(id);
        return Result.success();
    }
}
