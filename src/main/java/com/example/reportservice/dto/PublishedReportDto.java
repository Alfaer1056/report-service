package com.example.reportservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PublishedReportDto {
    private Long jobId;
    private Long requestId;
    private String requestNumber;
    private String applicantName;
    private String filePath;
    private String fileName;
    private LocalDateTime publishedAt;
    private Boolean viewed;
}