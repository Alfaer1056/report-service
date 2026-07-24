package com.example.reportservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDto {
    private Long id;
    private Long requestId;
    private String requestNumber;
    private String reviewerName;
    private String status; // "PENDING", "IN_PROGRESS", "COMPLETED", "REJECTED"
    private String comments;
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;
}