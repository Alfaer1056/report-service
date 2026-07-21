package com.example.reportservice.controller;

import com.example.reportservice.dto.PublishedReportDto;
import com.example.reportservice.dto.ReportSnapshotDto;
import com.example.reportservice.service.ReportPublicationService;
import com.example.reportservice.service.ReportSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/v1/applicant")
@RequiredArgsConstructor
public class ApplicantController {

    private final ReportPublicationService publicationService;
    private final ReportSnapshotService snapshotService;  // 👈 ДОБАВЛЕНО

    @GetMapping("/reports")
    public ResponseEntity<List<PublishedReportDto>> getMyReports(
            @RequestParam String name) {
        return ResponseEntity.ok(publicationService.getReportsForApplicant(name));
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadReport(
            @RequestParam Long requestId) {

        PublishedReportDto report = publicationService.getReportByRequestId(requestId);

        File file = new File(report.getFilePath());
        if (!file.exists()) {
            throw new RuntimeException("Файл не найден: " + report.getFilePath());
        }

        publicationService.markAsViewedByRequestId(requestId);

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + report.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /**
     * Получить снимок данных для отчета заявителя
     * GET /api/v1/applicant/snapshot?jobId=22
     */
    @GetMapping("/snapshot")
    public ResponseEntity<List<ReportSnapshotDto>> getSnapshot(
            @RequestParam Long jobId) {
        return ResponseEntity.ok(snapshotService.getSnapshotsByJobId(jobId));
    }
}