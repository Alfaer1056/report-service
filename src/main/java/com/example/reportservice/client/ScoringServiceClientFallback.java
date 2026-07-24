package com.example.reportservice.client;

import com.example.reportservice.dto.ScoringDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class ScoringServiceClientFallback implements ScoringServiceClient {

    @Override
    public List<ScoringDto> getAllScorings() {
        log.warn("⚠️ scoring-service недоступен, возвращаем тестовые данные");
        return Arrays.asList(
                createScoring(1L, 1L, "REQ-001", 85, "B", "LOW", "Рекомендуется к одобрению"),
                createScoring(2L, 2L, "REQ-002", 45, "D", "HIGH", "Требуется дополнительная проверка"),
                createScoring(3L, 3L, "REQ-003", 20, "F", "CRITICAL", "Отклонить с обоснованием")
        );
    }

    @Override
    public List<ScoringDto> getHighRiskScorings() {
        return getAllScorings().stream()
                .filter(s -> "HIGH".equals(s.getRiskLevel()) || "CRITICAL".equals(s.getRiskLevel()))
                .toList();
    }

    private ScoringDto createScoring(Long id, Long requestId, String number,
                                     Integer score, String grade, String risk, String recommendations) {
        ScoringDto dto = new ScoringDto();
        dto.setId(id);
        dto.setRequestId(requestId);
        dto.setRequestNumber(number);
        dto.setScore(score);
        dto.setGrade(grade);
        dto.setRiskLevel(risk);
        dto.setRecommendations(recommendations);
        return dto;
    }
}