package com.resume.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 匹配记录 DTO - 包含关联的人名和岗位名称
 */
@Data
public class MatchRecordDTO {
    private Long id;
    private Long resumeId;
    private Long positionId;
    private String candidateName;
    private String positionTitle;
    private BigDecimal matchScore;
    private String matchReason;
    private String skillMatch;
    private String status;
    private LocalDateTime createdAt;
}
