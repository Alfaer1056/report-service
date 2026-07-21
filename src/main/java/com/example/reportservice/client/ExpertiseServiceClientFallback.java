package com.example.reportservice.client;

import com.example.reportservice.dto.ExpertiseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class ExpertiseServiceClientFallback implements ExpertiseServiceClient {

    @Override
    public List<ExpertiseDto> getAllExpertises() {
        log.warn("⚠️ expertise-service недоступен, возвращаем тестовые данные");
        return Arrays.asList(
                createTestExpertise(1L, "REQ-001", "Иванов И.И.", "COMPLETED", 0),
                createTestExpertise(2L, "REQ-002", "Петров П.П.", "IN_PROGRESS", 0),
                createTestExpertise(3L, "REQ-003", "Сидоров С.С.", "OVERDUE", 5)
        );
    }

    private ExpertiseDto createTestExpertise(Long id, String requestNumber, String expert,
                                             String status, Integer daysOverdue) {
        ExpertiseDto dto = new ExpertiseDto();
        dto.setId(id);
        dto.setRequestNumber(requestNumber);
        dto.setExpertName(expert);
        dto.setStatus(status);
        dto.setAssignedAt(LocalDateTime.now().minusDays(3));
        dto.setDeadline(LocalDateTime.now().plusDays(2));
        dto.setDaysOverdue(daysOverdue);
        return dto;
    }
}