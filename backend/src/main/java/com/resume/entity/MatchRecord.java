package com.resume.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 匹配记录实体
 */
@Data
@TableName("match_record")
public class MatchRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long resumeId;
    
    private Long positionId;
    
    private BigDecimal matchScore;  // 匹配度分数 0-100
    
    private String matchReason;  // AI 匹配理由
    
    private String skillMatch;  // 技能匹配详情 JSON
    
    private String status;  // pending/viewed/accepted/rejected
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
