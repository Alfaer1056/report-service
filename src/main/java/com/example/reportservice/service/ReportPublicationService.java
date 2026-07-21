package com.example.reportservice.service;

import com.example.reportservice.dto.PublishedReportDto;
import com.example.reportservice.entity.PublishedReport;
import com.example.reportservice.repository.PublishedReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportPublicationService {

    private final PublishedReportRepository repository;

    public void publishReport(Long jobId, Long requestId, String requestNumber,
                              String applicantName, String filePath, String fileName) {
        List<PublishedReport> existing = repository.findByRequestId(requestId);
        if (!existing.isEmpty()) {
            PublishedReport report = existing.get(0);
            report.setFilePath(filePath);
            report.setFileName(fileName);
            report.setPublishedAt(LocalDateTime.now());
            report.setViewed(false);
            repository.save(report);
            log.info("📄 Отчет обновлен для заявителя {}", applicantName);
            return;
        }

        PublishedReport report = new PublishedReport();
        report.setJobId(jobId);
        report.setRequestId(requestId);
        report.setRequestNumber(requestNumber);
        report.setApplicantName(applicantName);
        report.setFilePath(filePath);
        report.setFileName(fileName);
        report.setPublishedAt(LocalDateTime.now());
        report.setViewed(false);

        repository.save(report);
        log.info("📄 Отчет опубликован для заявителя {}: {} (заявка {})",
                applicantName, fileName, requestNumber);
    }

    public List<PublishedReportDto> getReportsForApplicant(String applicantName) {
        return repository.findByApplicantName(applicantName).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public PublishedReportDto getReportByRequestId(Long requestId) {
        log.info("🔍 Ищем отчет по requestId: {}", requestId);
        List<PublishedReport> reports = repository.findByRequestId(requestId);
        if (reports.isEmpty()) {
            log.error("❌ Отчет для requestId {} не найден", requestId);
            throw new RuntimeException("Отчет для заявки " + requestId + " не найден");
        }
        log.info("✅ Найден отчет: {}", reports.get(0).getFileName());
        return convertToDto(reports.get(0));
    }

    public void markAsViewed(Long reportId) {
        PublishedReport report = repository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Отчет не найден"));
        report.setViewed(true);
        repository.save(report);
        log.info("👀 Отчет {} отмечен как просмотренный", reportId);
    }

    public void markAsViewedByRequestId(Long requestId) {
        List<PublishedReport> reports = repository.findByRequestId(requestId);
        if (reports.isEmpty()) {
            throw new RuntimeException("Отчет для заявки " + requestId + " не найден");
        }
        PublishedReport report = reports.get(0);
        report.setViewed(true);
        repository.save(report);
        log.info("👀 Отчет по заявке {} отмечен как просмотренный", requestId);
    }

    private PublishedReportDto convertToDto(PublishedReport entity) {
        PublishedReportDto dto = new PublishedReportDto();
        dto.setJobId(entity.getJobId());
        dto.setRequestId(entity.getRequestId());
        dto.setRequestNumber(entity.getRequestNumber());
        dto.setApplicantName(entity.getApplicantName());
        dto.setFilePath(entity.getFilePath());
        dto.setFileName(entity.getFileName());
        dto.setPublishedAt(entity.getPublishedAt());
        dto.setViewed(entity.getViewed());
        return dto;
    }
}