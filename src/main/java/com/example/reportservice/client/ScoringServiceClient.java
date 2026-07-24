package com.example.reportservice.client;

import com.example.reportservice.dto.ScoringDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(
        name = "scoring-service",
        url = "http://localhost:8085",
        fallback = ScoringServiceClientFallback.class
)
public interface ScoringServiceClient {

    @GetMapping("/api/v1/scorings")
    List<ScoringDto> getAllScorings();

    @GetMapping("/api/v1/scorings/high-risk")
    List<ScoringDto> getHighRiskScorings();
}