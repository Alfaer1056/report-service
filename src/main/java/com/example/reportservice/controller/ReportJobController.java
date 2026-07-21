package com.example.reportservice.controller;

import com.example.reportservice.dto.ReportJobRequest;
import com.example.reportservice.dto.ReportJobResponse;
import com.example.reportservice.service.ReportJobService;
import com.example.reportservice.service.ReportGenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class ReportJobController {

    private final ReportJobService jobService;
    private final ReportGenerationService generationService; // 👈 ДОБАВЬТЕ

    // Создать задание и запустить генерацию в фоне
    @PostMapping
    public ResponseEntity<ReportJobResponse> createJob(@Valid @RequestBody ReportJobRequest request) {
        // 1. Создаем задание
        ReportJobResponse response = jobService.createJob(request);

        // 2. Запускаем асинхронную генерацию
        generationService.generateReport(response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Получить все задания
    @GetMapping
    public ResponseEntity<List<ReportJobResponse>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    // Получить задание по ID
    @GetMapping("/{id}")
    public ResponseEntity<ReportJobResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }
}