package com.resume.dto;

import lombok.Data;

/**
 * 简历解析结果
 */
@Data
public class ResumeParseResult {
    private String candidateName;
    private String phone;
    private String email;
    private String education;
    private Integer experienceYears;
    private String skills;
    private String workHistory;
    private String parseMessage; // 解析提示信息
    private Boolean hasMissingFields; // 是否有缺失字段
    private String missingFields; // 缺失的字段列表
}
