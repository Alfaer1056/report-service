package com.example.reportservice.service;

import com.example.reportservice.client.ExpertiseServiceClient;
import com.example.reportservice.client.RequestServiceClient;
import com.example.reportservice.dto.*;
import com.example.reportservice.entity.ReportJob;
import com.example.reportservice.entity.ReportTemplate;
import com.example.reportservice.repository.ReportJobRepository;
import com.example.reportservice.repository.ReportTemplateRepository;
import com.example.reportservice.util.DocxGenerator;
import com.example.reportservice.util.ExcelGenerator;
import com.example.reportservice.util.PdfGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportGenerationService {

    private final ReportJobRepository jobRepository;
    private final ReportJobService jobService;
    private final ReportTemplateRepository templateRepository;
    private final RequestServiceClient requestServiceClient;
    private final ExpertiseServiceClient expertiseServiceClient;
    private final PdfGenerator pdfGenerator;
    private final ExcelGenerator excelGenerator;
    private final DocxGenerator docxGenerator;
    private final ReportPublicationService publicationService;
    private final ReportSnapshotService snapshotService;

    @Async
    public void generateReport(Long jobId) {
        log.info("🔄 Начинаем генерацию отчета для задания ID: {}", jobId);

        try {
            jobService.updateJobStatus(jobId, "IN_PROGRESS", null, null, null);
            log.info("⏳ Задание {} в процессе выполнения...", jobId);

            ReportJob job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Задание не найдено"));

            ReportTemplate template = templateRepository.findById(job.getTemplateId())
                    .orElseThrow(() -> new RuntimeException("Шаблон не найден"));

            log.info("📋 Тип отчета: {}", template.getType());

            String reportType = template.getType();
            Object reportData = null;
            String dataType = "unknown";

            switch (reportType) {
                case "REPORT_REGISTRY":
                    reportData = getRegistryData();
                    dataType = "registry";
                    break;
                case "REPORT_EXPERTISE":
                    reportData = getExpertiseData();
                    dataType = "expertise";
                    break;
                case "REPORT_QUALITY":
                    reportData = getQualityData();
                    dataType = "quality";
                    break;
                case "REPORT_LOAD":
                    reportData = getLoadData();
                    dataType = "load";
                    break;
                case "REPORT_RISK":
                    reportData = getRiskData();
                    dataType = "risk";
                    break;
                case "REPORT_APPLICANT":
                    reportData = getApplicantData();
                    dataType = "applicant";
                    break;
                default:
                    log.warn("⚠️ Неизвестный тип отчета: {}, используем registry", reportType);
                    reportData = getRegistryData();
                    dataType = "registry";
            }

            Thread.sleep(2000);

            // 📸 СОХРАНЯЕМ СНИМОК ДАННЫХ
            snapshotService.saveSnapshot(jobId, dataType, reportData);
            log.info("📸 Снимок сохранен для задания {}", jobId);

            // 1. Генерация PDF
            String pdfPath = pdfGenerator.generateReport(jobId, template.getName(), job.getParameters(), dataType, reportData);
            log.info("✅ PDF создан: {}", pdfPath);

            // 2. Генерация Excel
            String excelPath = excelGenerator.generateExcel(jobId, template.getName(), job.getParameters(), dataType, reportData);
            log.info("✅ Excel создан: {}", excelPath);

            // 3. Генерация DOCX
            String docxPath = docxGenerator.generateDocx(jobId, template.getName(), job.getParameters(), dataType, reportData);
            log.info("✅ DOCX создан: {}", docxPath);

            // 4. ИЗВЛЕКАЕМ ИМЯ ФАЙЛА ИЗ ПУТИ
            String fileName = pdfPath.substring(pdfPath.lastIndexOf("/") + 1);

            // 5. Обновляем статус задания на COMPLETED
            jobService.updateJobStatus(jobId, "COMPLETED", fileName, pdfPath, null);
            log.info("✅ Отчет для задания {} успешно сгенерирован! PDF: {}, Excel: {}, DOCX: {}",
                    jobId, pdfPath, excelPath, docxPath);

            // 6. ПУБЛИКУЕМ ОТЧЕТ В КАБИНЕТ ЗАЯВИТЕЛЯ (ПЕРЕДАЕМ fileName)
            String applicantName = "applicant_" + jobId;
            String requestNumber = "REQ-" + jobId;

            publicationService.publishReport(
                    jobId,
                    jobId,
                    requestNumber,
                    applicantName,
                    pdfPath,
                    fileName  // 🔥 ТЕПЕРЬ ПЕРЕДАЕМ fileName, А НЕ null!
            );
            log.info("📄 Отчет опубликован в кабинет заявителя: {}", applicantName);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            jobService.updateJobStatus(jobId, "CANCELLED", null, null, "Задание отменено пользователем");
            log.warn("⚠️ Задание {} отменено", jobId);

        } catch (Exception e) {
            String errorMsg = "Ошибка генерации: " + e.getMessage();
            jobService.updateJobStatus(jobId, "ERROR", null, null, errorMsg);
            log.error("❌ Ошибка при генерации отчета {}: {}", jobId, e.getMessage(), e);
        }
    }

    // ============================================================
    // МЕТОДЫ ДЛЯ ПОЛУЧЕНИЯ ДАННЫХ
    // ============================================================

    public List<RequestDto> getRegistryData() {
        try {
            return requestServiceClient.getAllRequests();
        } catch (Exception e) {
            log.warn("⚠️ request-service недоступен, используем fallback");
            return getFallbackRegistryData();
        }
    }

    public List<ExpertiseDto> getExpertiseData() {
        try {
            return expertiseServiceClient.getAllExpertises();
        } catch (Exception e) {
            log.warn("⚠️ expertise-service недоступен, используем fallback");
            return getFallbackExpertiseData();
        }
    }

    public List<QualityDto> getQualityData() {
        try {
            return getFallbackQualityData();
        } catch (Exception e) {
            log.warn("⚠️ quality-service недоступен, используем fallback");
            return getFallbackQualityData();
        }
    }

    public List<LoadDto> getLoadData() {
        try {
            return getFallbackLoadData();
        } catch (Exception e) {
            log.warn("⚠️ load-service недоступен, используем fallback");
            return getFallbackLoadData();
        }
    }

    public List<RiskDto> getRiskData() {
        try {
            return getFallbackRiskData();
        } catch (Exception e) {
            log.warn("⚠️ risk-service недоступен, используем fallback");
            return getFallbackRiskData();
        }
    }

    public List<ApplicantDto> getApplicantData() {
        try {
            return getFallbackApplicantData();
        } catch (Exception e) {
            log.warn("⚠️ applicant-service недоступен, используем fallback");
            return getFallbackApplicantData();
        }
    }

    // ============================================================
    // FALLBACK-ДАННЫЕ
    // ============================================================

    private List<RequestDto> getFallbackRegistryData() {
        return Arrays.asList(
                createTestRequest(1L, "REQ-001", "Проверена", "СТАНДАРТ", "Иванов И.И."),
                createTestRequest(2L, "REQ-002", "На доработке", "СТАНДАРТ", "Петров П.П."),
                createTestRequest(3L, "REQ-003", "Отклонена", "ПОВЫШЕННАЯ", "Сидоров С.С.")
        );
    }

    private List<ExpertiseDto> getFallbackExpertiseData() {
        return Arrays.asList(
                createTestExpertise(1L, "REQ-001", "Иванов И.И.", "Завершена", LocalDateTime.now().minusDays(2), 0),
                createTestExpertise(2L, "REQ-002", "Петров П.П.", "В работе", LocalDateTime.now().minusDays(1), 0),
                createTestExpertise(3L, "REQ-003", "Сидоров С.С.", "Просрочена", LocalDateTime.now().minusDays(5), 3)
        );
    }

    private List<QualityDto> getFallbackQualityData() {
        return Arrays.asList(
                createTestQuality(1L, "REQ-001", "Контролер А.", "Пройдена", null, false),
                createTestQuality(2L, "REQ-002", "Контролер Б.", "На доработке", "Несоответствие п.3.2", true),
                createTestQuality(3L, "REQ-003", "Контролер В.", "Провалена", "Критические ошибки", false)
        );
    }

    private List<LoadDto> getFallbackLoadData() {
        return Arrays.asList(
                createLoad("Иванов И.И.", 12L, 10L, 2.5, 5),
                createLoad("Петров П.П.", 8L, 5L, 3.2, 7),
                createLoad("Сидоров С.С.", 15L, 12L, 1.8, 4)
        );
    }

    private List<RiskDto> getFallbackRiskData() {
        return Arrays.asList(
                createRisk("HIGH", "Несоответствие документации", 85, "Запросить недостающие документы", "OPEN"),
                createRisk("MEDIUM", "Просрочка сроков", 60, "Назначить дополнительного эксперта", "MITIGATED"),
                createRisk("LOW", "Незначительные замечания", 20, "Уведомить заявителя", "CLOSED")
        );
    }

    private List<ApplicantDto> getFallbackApplicantData() {
        return Arrays.asList(
                createApplicant(1L, "REQ-001", "Проверена", "Одобрить", "Замечаний нет", "http://docs.com/1", LocalDateTime.now().minusDays(2)),
                createApplicant(2L, "REQ-002", "На доработке", "Отклонить", "Требуется уточнение", "http://docs.com/2", LocalDateTime.now().minusDays(1))
        );
    }

    // ============================================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ СОЗДАНИЯ ТЕСТОВЫХ ОБЪЕКТОВ
    // ============================================================

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

    private ExpertiseDto createTestExpertise(Long id, String requestNumber, String expert, String status, LocalDateTime deadline, Integer daysOverdue) {
        ExpertiseDto dto = new ExpertiseDto();
        dto.setId(id);
        dto.setRequestNumber(requestNumber);
        dto.setExpertName(expert);
        dto.setStatus(status);
        dto.setAssignedAt(LocalDateTime.now().minusDays(3));
        dto.setDeadline(deadline);
        dto.setDaysOverdue(daysOverdue);
        return dto;
    }

    private QualityDto createTestQuality(Long id, String requestNumber, String checker, String status, String comments, Boolean requiresAdditional) {
        QualityDto dto = new QualityDto();
        dto.setId(id);
        dto.setRequestNumber(requestNumber);
        dto.setCheckerName(checker);
        dto.setStatus(status);
        dto.setCriticalComments(comments);
        dto.setRequiresAdditionalExpertise(requiresAdditional);
        return dto;
    }

    private LoadDto createLoad(String expert, Long assigned, Long completed, Double avgTime, Integer complexity) {
        LoadDto dto = new LoadDto();
        dto.setExpertName(expert);
        dto.setAssignedCount(assigned);
        dto.setCompletedCount(completed);
        dto.setAverageTime(avgTime);
        dto.setComplexity(complexity);
        return dto;
    }

    private RiskDto createRisk(String category, String desc, Integer score, String mitigation, String status) {
        RiskDto dto = new RiskDto();
        dto.setCategory(category);
        dto.setDescription(desc);
        dto.setScore(score);
        dto.setMitigation(mitigation);
        dto.setStatus(status);
        return dto;
    }

    private ApplicantDto createApplicant(Long id, String number, String status, String rec, String notes, String docs, LocalDateTime completed) {
        ApplicantDto dto = new ApplicantDto();
        dto.setRequestId(id);
        dto.setRequestNumber(number);
        dto.setStatus(status);
        dto.setRecommendation(rec);
        dto.setNotes(notes);
        dto.setDocuments(docs);
        dto.setCompletedAt(completed);
        return dto;
    }
}