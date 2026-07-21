package com.example.reportservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportGeneratedEvent {

    private Long jobId;
    private Long templateId;
    private String status;
    private String filePath;
    private String fileName;
    private String parameters;
    private String generatedBy;
    private LocalDateTime generatedAt;
}