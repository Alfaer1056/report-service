package com.example.reportservice.service;

import com.example.reportservice.dto.ReportTemplateRequest;
import com.example.reportservice.dto.ReportTemplateResponse;
import com.example.reportservice.entity.ReportTemplate;
import com.example.reportservice.repository.ReportTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportTemplateService {

    private final ReportTemplateRepository repository;

    // Создание нового шаблона
    public ReportTemplateResponse createTemplate(ReportTemplateRequest request) {
        ReportTemplate template = new ReportTemplate();
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setType(request.getType());
        template.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
        template.setVersion(1);
        template.setCreatedBy("system"); // Позже заменим на реального пользователя

        ReportTemplate saved = repository.save(template);
        return convertToResponse(saved);
    }

    // Получение всех шаблонов
    public List<ReportTemplateResponse> getAllTemplates() {
        return repository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Получение шаблона по ID
    public ReportTemplateResponse getTemplateById(Long id) {
        ReportTemplate template = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Шаблон не найден с ID: " + id));
        return convertToResponse(template);
    }

    // Преобразование Entity → DTO
    private ReportTemplateResponse convertToResponse(ReportTemplate entity) {
        ReportTemplateResponse response = new ReportTemplateResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setType(entity.getType());
        response.setStatus(entity.getStatus());
        response.setVersion(entity.getVersion());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        return response;
    }
}