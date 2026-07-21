package com.example.reportservice.dto;

import lombok.Data;

@Data
public class QualityDto {
    private Long id;
    private String requestNumber;
    private String checkerName;
    private String status;  // "Пройдена", "На доработке", "Провалена"
    private String criticalComments;
    private Boolean requiresAdditionalExpertise;
}