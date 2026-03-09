package com.resume.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.resume.entity.JobPosition;
import com.resume.mapper.JobPositionMapper;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 岗位服务
 */
@Service
public class JobPositionService extends ServiceImpl<JobPositionMapper, JobPosition> {
    
    /**
     * 获取所有招聘中的岗位
     */
    public List<JobPosition> getActivePositions() {
        return lambdaQuery()
                .eq(JobPosition::getStatus, 1)
                .orderByDesc(JobPosition::getCreatedAt)
                .list();
    }
    
    /**
     * 根据部门查询岗位
     */
    public List<JobPosition> getByDepartment(String department) {
        return lambdaQuery()
                .eq(JobPosition::getDepartment, department)
                .eq(JobPosition::getStatus, 1)
                .list();
    }
}
