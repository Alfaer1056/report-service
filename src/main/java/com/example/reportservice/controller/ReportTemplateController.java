package com.example.reportservice.controller;

import com.example.reportservice.dto.ReportTemplateRequest;
import com.example.reportservice.dto.ReportTemplateResponse;
import com.example.reportservice.service.ReportTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class ReportTemplateController {

    private final ReportTemplateService service;

    // Создать шаблон: POST /api/v1/templates
    @PostMapping
    public ResponseEntity<ReportTemplateResponse> createTemplate(@Valid @RequestBody ReportTemplateRequest request) {
        ReportTemplateResponse response = service.createTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Получить все шаблоны: GET /api/v1/templates
    @GetMapping
    public ResponseEntity<List<ReportTemplateResponse>> getAllTemplates() {
        return ResponseEntity.ok(service.getAllTemplates());
    }

    // Получить шаблон по ID: GET /api/v1/templates/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ReportTemplateResponse> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTemplateById(id));
    }
}