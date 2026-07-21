package com.example.reportservice.service;

import com.example.reportservice.dto.ReportSnapshotDto;
import com.example.reportservice.entity.ReportSnapshot;
import com.example.reportservice.repository.ReportSnapshotRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportSnapshotService {

    private final ReportSnapshotRepository repository;
    private final ObjectMapper objectMapper;

    /**
     * Сохраняет снимок данных для отчета
     */
    public void saveSnapshot(Long jobId, String templateType, Object data) {
        try {
            String jsonData = objectMapper.writeValueAsString(data);

            ReportSnapshot snapshot = new ReportSnapshot();
            snapshot.setJobId(jobId);
            snapshot.setTemplateType(templateType);
            snapshot.setSnapshotData(jsonData);
            snapshot.setCreatedBy("system");

            repository.save(snapshot);
            log.info("📸 Снимок сохранен для задания {}", jobId);

        } catch (JsonProcessingException e) {
            log.error("❌ Ошибка сериализации данных для снимка: {}", e.getMessage());
            throw new RuntimeException("Не удалось сохранить снимок: " + e.getMessage());
        }
    }

    /**
     * Получить снимок по ID задания
     */
    public List<ReportSnapshotDto> getSnapshotsByJobId(Long jobId) {
        return repository.findByJobId(jobId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получить данные снимка (десериализованные)
     */
    public <T> T getSnapshotData(Long jobId, Class<T> clazz) {
        List<ReportSnapshot> snapshots = repository.findByJobId(jobId);
        if (snapshots.isEmpty()) {
            throw new RuntimeException("Снимок для задания " + jobId + " не найден");
        }

        try {
            String jsonData = snapshots.get(0).getSnapshotData();
            return objectMapper.readValue(jsonData, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка десериализации снимка: " + e.getMessage());
        }
    }

    private ReportSnapshotDto convertToDto(ReportSnapshot entity) {
        ReportSnapshotDto dto = new ReportSnapshotDto();
        dto.setId(entity.getId());
        dto.setJobId(entity.getJobId());
        dto.setTemplateType(entity.getTemplateType());
        dto.setSnapshotData(entity.getSnapshotData());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        return dto;
    }
}