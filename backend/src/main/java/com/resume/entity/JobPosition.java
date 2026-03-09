package com.resume.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 岗位实体
 */
@Data
@TableName("job_position")
public class JobPosition {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;  // 岗位名称
    
    private String department;  // 部门
    
    private String requirements;  // 岗位要求（原始文本）
    
    private String skills;  // 技能要求 JSON ["Java","MySQL"]
    
    private Integer experienceMin;  // 最小工作年限
    
    private String salaryRange;  // 薪资范围
    
    private Integer status;  // 1-招聘中 0-已关闭
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
