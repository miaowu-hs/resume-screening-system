package com.resume.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 简历上传DTO
 */
@Data
public class ResumeUploadDTO {
    
    private MultipartFile file;
    private String candidateName;
    private String phone;
    private String email;
}
