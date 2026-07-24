package com.example.reportservice.client;

import com.example.reportservice.dto.CabinetDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class CabinetServiceClientFallback implements CabinetServiceClient {

    @Override
    public CabinetDto getUserById(Long userId) {
        log.warn("⚠️ cabinet-service недоступен, возвращаем тестовые данные для ID: {}", userId);
        return createCabinet(userId, "applicant_" + userId, "APPLICANT", "user" + userId + "@example.com", true);
    }

    @Override
    public CabinetDto getUserByEmail(String email) {
        log.warn("⚠️ cabinet-service недоступен, возвращаем тестовые данные для email: {}", email);
        return createCabinet(1L, "Test User", "APPLICANT", email, true);
    }

    private CabinetDto createCabinet(Long id, String name, String role, String email, Boolean active) {
        CabinetDto dto = new CabinetDto();
        dto.setId(id);
        dto.setUserName(name);
        dto.setUserRole(role);
        dto.setEmail(email);
        dto.setActive(active);
        dto.setLastLogin(LocalDateTime.now().minusDays(1));
        return dto;
    }
}