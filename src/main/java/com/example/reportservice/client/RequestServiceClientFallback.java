package com.example.reportservice.client;

import com.example.reportservice.dto.RequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class RequestServiceClientFallback implements RequestServiceClient {

    @Override
    public List<RequestDto> getAllRequests() {
        log.warn("⚠️ request-service недоступен, возвращаем тестовые данные");
        return Arrays.asList(
                createTestRequest(1L, "REQ-001", "Проверена", "СТАНДАРТ", "Иванов И.И."),
                createTestRequest(2L, "REQ-002", "На доработке", "СТАНДАРТ", "Петров П.П."),
                createTestRequest(3L, "REQ-003", "Отклонена", "ПОВЫШЕННАЯ", "Сидоров С.С.")
        );
    }

    @Override
    public RequestDto getRequestById(Long id) {
        log.warn("⚠️ request-service недоступен, возвращаем тестовые данные для ID: {}", id);
        return createTestRequest(id, "REQ-" + id, "Проверена", "СТАНДАРТ", "Иванов И.И.");
    }

    @Override
    public List<RequestDto> getRequestsByStatus(String status) {
        log.warn("⚠️ request-service недоступен, возвращаем тестовые данные для статуса: {}", status);
        return getAllRequests();
    }

    private RequestDto createTestRequest(Long id, String number, String status, String type, String expert) {
        RequestDto dto = new RequestDto();
        dto.setId(id);
        dto.setNumber(number);
        dto.setStatus(status);
        dto.setType(type);
        dto.setExpertName(expert);
        dto.setCreatedAt(LocalDateTime.now().minusDays(5));
        dto.setUpdatedAt(LocalDateTime.now());
        return dto;
    }
}