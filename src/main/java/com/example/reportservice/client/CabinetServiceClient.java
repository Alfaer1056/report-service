package com.example.reportservice.client;

import com.example.reportservice.dto.CabinetDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "cabinet-service",
        url = "http://localhost:8087",
        fallback = CabinetServiceClientFallback.class
)
public interface CabinetServiceClient {

    @GetMapping("/api/v1/cabinet/users/{userId}")
    CabinetDto getUserById(@PathVariable("userId") Long userId);

    @GetMapping("/api/v1/cabinet/users/email/{email}")
    CabinetDto getUserByEmail(@PathVariable("email") String email);
}