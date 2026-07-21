package com.example.reportservice.dto;

import lombok.Data;

@Data
public class LoadDto {
    private String expertName;
    private Long assignedCount;
    private Long completedCount;
    private Double averageTime; // среднее время выполнения в часах
    private Integer complexity; // сложность дел (1-10)
}