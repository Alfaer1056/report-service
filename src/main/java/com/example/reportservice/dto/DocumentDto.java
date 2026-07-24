package com.example.reportservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentDto {
    private Long id;
    private Long requestId;
    private String requestNumber;
    private String documentName;
    private String documentType; // "PDF", "DOCX", "XLSX", "IMG"
    private String fileUrl;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
}