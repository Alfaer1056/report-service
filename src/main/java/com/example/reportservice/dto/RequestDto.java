package com.example.reportservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RequestDto {
    private Long id;
    private String number;
    private String status;
    private String type;
    private String expertName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}