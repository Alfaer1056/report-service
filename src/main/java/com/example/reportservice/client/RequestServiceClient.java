package com.example.reportservice.client;

import com.example.reportservice.dto.RequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "request-service",
        url = "http://localhost:8081",
        fallback = RequestServiceClientFallback.class
)
public interface RequestServiceClient {

    @GetMapping("/api/v1/requests")
    List<RequestDto> getAllRequests();

    @GetMapping("/api/v1/requests/{id}")
    RequestDto getRequestById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/requests/by-status")
    List<RequestDto> getRequestsByStatus(@RequestParam("status") String status);
}