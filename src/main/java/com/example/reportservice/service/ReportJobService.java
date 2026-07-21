package com.example.reportservice.service;

import com.example.reportservice.dto.ReportJobRequest;
import com.example.reportservice.dto.ReportJobResponse;
import com.example.reportservice.entity.ReportJob;
import com.example.reportservice.repository.ReportJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportJobService {

    private final ReportJobRepository repository;

    // Создание задания на генерацию
    public ReportJobResponse createJob(ReportJobRequest request) {
        ReportJob job = new ReportJob();
        job.setTemplateId(request.getTemplateId());
        job.setParameters(request.getParameters());
        job.setStatus("QUEUED");
        job.setCreatedBy("system");

        ReportJob saved = repository.save(job);
        return convertToResponse(saved);
    }

    // Получение всех заданий
    public List<ReportJobResponse> getAllJobs() {
        return repository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Получение задания по ID
    public ReportJobResponse getJobById(Long id) {
        ReportJob job = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задание не найдено с ID: " + id));
        return convertToResponse(job);
    }

    // Обновление статуса задания (используется фоновым процессом)
    public void updateJobStatus(Long id, String status, String fileName, String filePath, String errorMessage) {
        ReportJob job = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задание не найдено с ID: " + id));

        job.setStatus(status);
        job.setFileName(fileName);
        job.setFilePath(filePath);
        job.setErrorMessage(errorMessage);

        if ("COMPLETED".equals(status) || "ERROR".equals(status) || "CANCELLED".equals(status)) {
            job.setCompletedAt(LocalDateTime.now());
        }

        repository.save(job);
    }

    // Преобразование Entity → DTO
    private ReportJobResponse convertToResponse(ReportJob entity) {
        ReportJobResponse response = new ReportJobResponse();
        response.setId(entity.getId());
        response.setTemplateId(entity.getTemplateId());
        response.setStatus(entity.getStatus());
        response.setFileName(entity.getFileName());
        response.setFilePath(entity.getFilePath());
        response.setErrorMessage(entity.getErrorMessage());
        response.setParameters(entity.getParameters());
        response.setCreatedBy(entity.getCreatedBy());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setCompletedAt(entity.getCompletedAt());
        return response;
    }
}