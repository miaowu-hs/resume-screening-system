package com.resume.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 简历实体
 */
@Data
@TableName("resume")
public class Resume {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String fileName;  // 原始文件名
    
    private String filePath;  // 存储路径
    
    private String candidateName;  // 姓名
    
    private String phone;  // 电话
    
    private String email;  // 邮箱
    
    private String education;  // 学历
    
    private Integer experienceYears;  // 工作年限
    
    private String skills;  // 技能列表 JSON
    
    private String workHistory;  // 工作经历
    
    private String parsedContent;  // 解析后的完整内容
    
    private Long uploadUserId;  // 上传人
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
