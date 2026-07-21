package com.example.reportservice.dto;

import lombok.Data;

@Data
public class RiskDto {
    private String category;
    private String description;
    private Integer score;
    private String mitigation;
    private String status; // "OPEN", "MITIGATED", "CLOSED"
}