package com.resume.dto;

import lombok.Data;

/**
 * 匹配结果DTO
 */
@Data
public class MatchResultDTO {
    
    private Long resumeId;
    private String candidateName;
    private Long positionId;
    private String positionTitle;
    private Double matchScore;
    private String matchReason;
    private String skillMatch;
}
