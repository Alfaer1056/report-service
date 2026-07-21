package com.example.reportservice.controller;

import com.example.reportservice.dto.LoadDto;
import com.example.reportservice.dto.RiskDto;
import com.example.reportservice.dto.RequestDto;
import com.example.reportservice.service.ReportGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/drill-down")
@RequiredArgsConstructor
public class DrillDownController {

    private final ReportGenerationService reportService;

    /**
     * GET /api/v1/drill-down/overdue - список просроченных заявок
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<RequestDto>> getOverdueRequests() {
        List<RequestDto> all = reportService.getRegistryData();
        List<RequestDto> overdue = all.stream()
                .filter(r -> "OVERDUE".equals(r.getStatus()) || "Просрочена".equals(r.getStatus()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(overdue);
    }

    /**
     * GET /api/v1/drill-down/risks?level=HIGH - список рисков по уровню
     */
    @GetMapping("/risks")
    public ResponseEntity<List<RiskDto>> getRisksByLevel(@RequestParam String level) {
        List<RiskDto> all = reportService.getRiskData();
        List<RiskDto> filtered = all.stream()
                .filter(r -> level.equalsIgnoreCase(r.getCategory()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filtered);
    }

    /**
     * GET /api/v1/drill-down/experts - нагрузка экспертов (детально)
     */
    @GetMapping("/experts")
    public ResponseEntity<List<LoadDto>> getExpertsLoad() {
        return ResponseEntity.ok(reportService.getLoadData());
    }
}