package com.example.reportservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExpertiseDto {
    private Long id;
    private String requestNumber;
    private String expertName;
    private String status;  // "IN_PROGRESS", "COMPLETED", "OVERDUE"
    private LocalDateTime assignedAt;
    private LocalDateTime deadline;
    private Integer daysOverdue;
}