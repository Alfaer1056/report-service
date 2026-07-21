package com.example.reportservice.service;

import com.example.reportservice.client.RequestServiceClient;
import com.example.reportservice.client.ExpertiseServiceClient;
import com.example.reportservice.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final RequestServiceClient requestServiceClient;
    private final ExpertiseServiceClient expertiseServiceClient;
    private final ReportGenerationService reportService;

    public DashboardDto getDashboard() {
        DashboardDto dashboard = new DashboardDto();

        // 1. Данные по заявкам
        List<RequestDto> requests = reportService.getRegistryData();
        dashboard.setTotalRequests((long) requests.size());

        List<RequestDto> overdue = requests.stream()
                .filter(r -> "OVERDUE".equals(r.getStatus()) || "Просрочена".equals(r.getStatus()))
                .collect(Collectors.toList());
        dashboard.setOverdueRequests((long) overdue.size());
        dashboard.setOverdueRequestsList(overdue);

        List<RequestDto> inProgress = requests.stream()
                .filter(r -> "IN_PROGRESS".equals(r.getStatus()) || "В работе".equals(r.getStatus()))
                .collect(Collectors.toList());
        dashboard.setInProgressRequests((long) inProgress.size());

        List<RequestDto> completed = requests.stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()) || "Завершена".equals(r.getStatus()))
                .collect(Collectors.toList());
        dashboard.setCompletedRequests((long) completed.size());

        // 2. Данные по экспертам
        List<LoadDto> loads = reportService.getLoadData();
        dashboard.setTotalExperts((long) loads.size());
        double avgLoad = loads.stream()
                .mapToDouble(l -> l.getAssignedCount() != null ? l.getAssignedCount() : 0)
                .average()
                .orElse(0.0);
        dashboard.setAverageExpertLoad(Math.round(avgLoad * 10) / 10.0);
        dashboard.setExpertsLoadList(loads);

        // 3. Данные по качеству
        List<QualityDto> qualities = reportService.getQualityData();
        dashboard.setTotalQualityChecks((long) qualities.size());
        long failed = qualities.stream()
                .filter(q -> "FAILED".equals(q.getStatus()) || "Провалена".equals(q.getStatus()))
                .count();
        dashboard.setFailedQualityChecks(failed);
        if (!qualities.isEmpty()) {
            double passRate = ((double) (qualities.size() - failed) / qualities.size()) * 100;
            dashboard.setQualityPassRate(Math.round(passRate * 10) / 10.0);
        } else {
            dashboard.setQualityPassRate(0.0);
        }

        // 4. Данные по рискам
        List<RiskDto> risks = reportService.getRiskData();
        dashboard.setHighRisks(risks.stream().filter(r -> "HIGH".equals(r.getCategory())).count());
        dashboard.setMediumRisks(risks.stream().filter(r -> "MEDIUM".equals(r.getCategory())).count());
        dashboard.setLowRisks(risks.stream().filter(r -> "LOW".equals(r.getCategory())).count());
        dashboard.setHighRisksList(
                risks.stream().filter(r -> "HIGH".equals(r.getCategory())).collect(Collectors.toList())
        );

        log.info("📊 Дашборд собран: заявок={}, просрочено={}, экспертов={}, рисков HIGH={}",
                dashboard.getTotalRequests(), dashboard.getOverdueRequests(),
                dashboard.getTotalExperts(), dashboard.getHighRisks());

        return dashboard;
    }
}