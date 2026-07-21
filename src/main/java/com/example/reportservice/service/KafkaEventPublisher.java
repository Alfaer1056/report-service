package com.example.reportservice.service;

import com.example.reportservice.event.ReportGeneratedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private static final String TOPIC = "report-generated";

    private final KafkaTemplate<String, ReportGeneratedEvent> kafkaTemplate;

    public void publishReportGenerated(Long jobId, Long templateId, String status,
                                       String filePath, String fileName, String parameters,
                                       String createdBy) {
        // Создаем событие
        ReportGeneratedEvent event = new ReportGeneratedEvent(
                jobId,
                templateId,
                status,
                filePath,
                fileName,
                parameters,
                createdBy != null ? createdBy : "system",
                LocalDateTime.now()
        );

        log.info("📤 Отправка события в Kafka: {}", event);

        // Асинхронная отправка
        CompletableFuture<SendResult<String, ReportGeneratedEvent>> future =
                kafkaTemplate.send(TOPIC, String.valueOf(jobId), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ Событие успешно отправлено в топик {}: offset = {}",
                        TOPIC, result.getRecordMetadata().offset());
            } else {
                log.error("❌ Ошибка отправки события в Kafka: {}", ex.getMessage(), ex);
            }
        });
    }
}