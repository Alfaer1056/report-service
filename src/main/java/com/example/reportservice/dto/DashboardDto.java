package com.example.reportservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class DashboardDto {
    // Общие показатели
    private Long totalRequests;
    private Long overdueRequests;
    private Long inProgressRequests;
    private Long completedRequests;

    // Нагрузка экспертов
    private Double averageExpertLoad;
    private Long totalExperts;

    // Качество
    private Double qualityPassRate;
    private Long totalQualityChecks;
    private Long failedQualityChecks;

    // Риски
    private Long highRisks;
    private Long mediumRisks;
    private Long lowRisks;

    // Списки для drill-down
    private List<RequestDto> overdueRequestsList;
    private List<RiskDto> highRisksList;
    private List<LoadDto> expertsLoadList;
}