package com.example.reportservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApplicantDto {
    private Long requestId;
    private String requestNumber;
    private String status;
    private String recommendation;
    private String notes;
    private String documents;
    private LocalDateTime completedAt;
}