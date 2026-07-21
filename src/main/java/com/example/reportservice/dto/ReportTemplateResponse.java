package com.example.reportservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReportTemplateResponse {
    private Long id;
    private String name;
    private String description;
    private String type;
    private String status;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}