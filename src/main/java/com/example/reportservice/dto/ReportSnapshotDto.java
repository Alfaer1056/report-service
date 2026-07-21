package com.example.reportservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReportSnapshotDto {
    private Long id;
    private Long jobId;
    private String templateType;
    private String snapshotData;
    private LocalDateTime createdAt;
    private String createdBy;
}