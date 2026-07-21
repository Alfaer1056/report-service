package com.example.reportservice.listener;

import com.example.reportservice.event.ReportGeneratedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReportEventListener {

    @KafkaListener(topics = "report-generated", groupId = "report-service-group")
    public void handleReportGenerated(ReportGeneratedEvent event) {
        log.info("📩 Получено событие из Kafka: {}", event);
        log.info("📄 Отчет готов: ID задания = {}, Файл = {}",
                event.getJobId(), event.getFileName());

        // Здесь можно отправить уведомление пользователю
        // или сохранить событие в базу для истории
    }
}