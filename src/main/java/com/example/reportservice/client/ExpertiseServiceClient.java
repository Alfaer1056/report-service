package com.example.reportservice.client;

import com.example.reportservice.dto.ExpertiseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(
        name = "expertise-service",
        url = "http://localhost:8083",  // Позже заменим на реальный порт
        fallback = ExpertiseServiceClientFallback.class
)
public interface ExpertiseServiceClient {

    @GetMapping("/api/v1/expertises")
    List<ExpertiseDto> getAllExpertises();
}