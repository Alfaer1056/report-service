package com.example.reportservice.dto;

import lombok.Data;

@Data
public class ScoringDto {
    private Long id;
    private Long requestId;
    private String requestNumber;
    private Integer score;
    private String grade; // "A", "B", "C", "D", "F"
    private String riskLevel; // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    private String recommendations;
}