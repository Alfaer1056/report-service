package com.example.reportservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_id", nullable = false)
    private Long templateId;  // ID шаблона, по которому генерируем отчет

    @Column(nullable = false)
    private String status; // QUEUED, IN_PROGRESS, COMPLETED, ERROR, CANCELLED

    @Column(name = "file_name")
    private String fileName; // Имя сгенерированного файла

    @Column(name = "file_path")
    private String filePath; // Путь к файлу (или ссылка в S3)

    @Column(name = "error_message", length = 1000)
    private String errorMessage; // Текст ошибки, если статус ERROR

    @Column(name = "parameters", columnDefinition = "TEXT")
    private String parameters; // JSON с параметрами фильтрации (период, статусы и т.д.)

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt; // Время завершения (успех или ошибка)

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "QUEUED";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}