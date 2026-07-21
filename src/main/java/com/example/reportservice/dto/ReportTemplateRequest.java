package com.example.reportservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportTemplateRequest {

    @NotBlank(message = "Имя шаблона обязательно")
    private String name;

    private String description;

    @NotBlank(message = "Тип отчета обязателен")
    private String type; // Например: "REPORT_REGISTRY"

    private String status = "DRAFT"; // По умолчанию черновик
}