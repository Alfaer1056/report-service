package com.example.reportservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportJobRequest {

    @NotNull(message = "ID шаблона обязательно")
    private Long templateId;

    private String parameters; // JSON с параметрами фильтрации
}