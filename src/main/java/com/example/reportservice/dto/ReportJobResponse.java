package com.example.reportservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReportJobResponse {
    private Long id;
    private Long templateId;
    private String status;
    private String fileName;
    private String filePath;
    private String errorMessage;
    private String parameters;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}