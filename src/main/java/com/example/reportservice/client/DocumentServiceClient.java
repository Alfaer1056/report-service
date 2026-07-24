package com.example.reportservice.client;

import com.example.reportservice.dto.DocumentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(
        name = "document-service",
        url = "http://localhost:8086",
        fallback = DocumentServiceClientFallback.class
)
public interface DocumentServiceClient {

    @GetMapping("/api/v1/documents")
    List<DocumentDto> getAllDocuments();

    @GetMapping("/api/v1/documents/recent")
    List<DocumentDto> getRecentDocuments();
}