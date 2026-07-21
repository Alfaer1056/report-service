package com.example.reportservice.util;

import com.example.reportservice.dto.ExpertiseDto;
import com.example.reportservice.dto.QualityDto;
import com.example.reportservice.dto.RequestDto;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.example.reportservice.dto.*;

@Slf4j
@Component
public class PdfGenerator {

    public String generateReport(Long jobId, String templateName, String parameters,
                                 String dataType, Object data) {
        String fileName = "report_" + jobId + "_" + System.currentTimeMillis() + ".pdf";
        String filePath = "./reports/" + fileName;

        try {
            File directory = new File("./reports");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // ========== ЗАГОЛОВОК ==========
            String title = switch (dataType) {
                case "registry" -> "РЕЕСТР ЗАЯВОК";
                case "expertise" -> "ХОД ЭКСПЕРТИЗ";
                case "quality" -> "КАЧЕСТВО ЭКСПЕРТИЗ";
                case "load" -> "НАГРУЗКА ЭКСПЕРТОВ";       // 👈 НОВЫЙ
                case "risk" -> "КАРТА РИСКОВ";              // 👈 НОВЫЙ
                case "applicant" -> "ОТЧЕТ ЗАЯВИТЕЛЯ";      // 👈 НОВЫЙ
                default -> "ОТЧЕТ";
            };

            Paragraph titleParagraph = new Paragraph(title)
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold();
            document.add(titleParagraph);

            // ========== ИНФОРМАЦИЯ ОБ ОТЧЕТЕ ==========
            document.add(new Paragraph(" ").setFontSize(12));
            document.add(new Paragraph("ID задания: " + jobId).setFontSize(12));
            document.add(new Paragraph("Шаблон: " + templateName).setFontSize(12));
            document.add(new Paragraph("Параметры: " + parameters).setFontSize(12));
            document.add(new Paragraph("Дата генерации: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))
                    .setFontSize(12));

            // ========== ТАБЛИЦА С ДАННЫМИ ==========
            document.add(new Paragraph("\nДанные:").setFontSize(14).setBold());

            switch (dataType) {
                case "registry":
                    addRegistryTable(document, (List<RequestDto>) data);
                    break;
                case "expertise":
                    addExpertiseTable(document, (List<ExpertiseDto>) data);
                    break;
                case "quality":
                    addQualityTable(document, (List<QualityDto>) data);
                    break;
                case "load":      // 👈 НОВЫЙ
                    addLoadTable(document, (List<LoadDto>) data);
                    break;
                case "risk":      // 👈 НОВЫЙ
                    addRiskTable(document, (List<RiskDto>) data);
                    break;
                case "applicant": // 👈 НОВЫЙ
                    addApplicantTable(document, (List<ApplicantDto>) data);
                    break;
                default:
                    document.add(new Paragraph("Неизвестный тип данных").setFontSize(12));
            }

            // ========== ПОДВАЛ ==========
            document.add(new Paragraph("\nОтчет сгенерирован автоматически системой Report Service")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic());

            document.close();
            log.info("✅ PDF-отчет создан: {}", filePath);

        } catch (IOException e) {
            log.error("❌ Ошибка генерации PDF: {}", e.getMessage());
            throw new RuntimeException("Не удалось сгенерировать PDF: " + e.getMessage());
        }

        return filePath;
    }

    // ============================================================
    // ТАБЛИЦЫ ДЛЯ РАЗНЫХ ТИПОВ ДАННЫХ
    // ============================================================

    private void addRegistryTable(Document document, List<RequestDto> requests) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 3, 3, 3, 3}))
                .useAllAvailableWidth();

        table.addCell("№");
        table.addCell("Номер заявки");
        table.addCell("Статус");
        table.addCell("Тип");
        table.addCell("Эксперт");

        for (RequestDto req : requests) {
            table.addCell(String.valueOf(req.getId()));
            table.addCell(req.getNumber() != null ? req.getNumber() : "N/A");
            table.addCell(req.getStatus() != null ? req.getStatus() : "N/A");
            table.addCell(req.getType() != null ? req.getType() : "N/A");
            table.addCell(req.getExpertName() != null ? req.getExpertName() : "N/A");
        }

        document.add(table);
    }

    private void addExpertiseTable(Document document, List<ExpertiseDto> expertises) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 3, 3, 3, 3, 2}))
                .useAllAvailableWidth();

        table.addCell("№");
        table.addCell("Заявка");
        table.addCell("Эксперт");
        table.addCell("Статус");
        table.addCell("Дедлайн");
        table.addCell("Просрочка");

        for (ExpertiseDto exp : expertises) {
            table.addCell(String.valueOf(exp.getId()));
            table.addCell(exp.getRequestNumber() != null ? exp.getRequestNumber() : "N/A");
            table.addCell(exp.getExpertName() != null ? exp.getExpertName() : "N/A");
            table.addCell(exp.getStatus() != null ? exp.getStatus() : "N/A");
            table.addCell(exp.getDeadline() != null ? exp.getDeadline().toString() : "N/A");
            table.addCell(exp.getDaysOverdue() != null ? String.valueOf(exp.getDaysOverdue()) : "0");
        }

        document.add(table);
    }

    private void addQualityTable(Document document, List<QualityDto> qualities) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 3, 3, 3, 4, 3}))
                .useAllAvailableWidth();

        table.addCell("№");
        table.addCell("Заявка");
        table.addCell("Проверяющий");
        table.addCell("Статус");
        table.addCell("Замечания");
        table.addCell("Доп. экспертиза");

        for (QualityDto q : qualities) {
            table.addCell(String.valueOf(q.getId()));
            table.addCell(q.getRequestNumber() != null ? q.getRequestNumber() : "N/A");
            table.addCell(q.getCheckerName() != null ? q.getCheckerName() : "N/A");
            table.addCell(q.getStatus() != null ? q.getStatus() : "N/A");
            table.addCell(q.getCriticalComments() != null ? q.getCriticalComments() : "Нет");
            table.addCell(q.getRequiresAdditionalExpertise() != null && q.getRequiresAdditionalExpertise()
                    ? "Да" : "Нет");
        }

        document.add(table);
    }
    // ============================================================
// ТАБЛИЦЫ ДЛЯ НОВЫХ ОТЧЕТОВ
// ============================================================

    private void addLoadTable(Document document, List<LoadDto> loads) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 2}))
                .useAllAvailableWidth();

        table.addCell("Эксперт");
        table.addCell("Назначено");
        table.addCell("Выполнено");
        table.addCell("Среднее время (ч)");
        table.addCell("Сложность");

        for (LoadDto load : loads) {
            table.addCell(load.getExpertName() != null ? load.getExpertName() : "N/A");
            table.addCell(String.valueOf(load.getAssignedCount() != null ? load.getAssignedCount() : 0));
            table.addCell(String.valueOf(load.getCompletedCount() != null ? load.getCompletedCount() : 0));
            table.addCell(String.valueOf(load.getAverageTime() != null ? load.getAverageTime() : 0));
            table.addCell(String.valueOf(load.getComplexity() != null ? load.getComplexity() : 0));
        }

        document.add(table);
    }

    private void addRiskTable(Document document, List<RiskDto> risks) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 3, 2, 3, 2}))
                .useAllAvailableWidth();

        table.addCell("Категория");
        table.addCell("Описание");
        table.addCell("Оценка");
        table.addCell("Митигация");
        table.addCell("Статус");

        for (RiskDto risk : risks) {
            table.addCell(risk.getCategory() != null ? risk.getCategory() : "N/A");
            table.addCell(risk.getDescription() != null ? risk.getDescription() : "N/A");
            table.addCell(String.valueOf(risk.getScore() != null ? risk.getScore() : 0));
            table.addCell(risk.getMitigation() != null ? risk.getMitigation() : "N/A");
            table.addCell(risk.getStatus() != null ? risk.getStatus() : "N/A");
        }

        document.add(table);
    }

    private void addApplicantTable(Document document, List<ApplicantDto> applicants) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 3, 3, 3, 3, 3}))
                .useAllAvailableWidth();

        table.addCell("№");
        table.addCell("Заявка");
        table.addCell("Статус");
        table.addCell("Рекомендация");
        table.addCell("Замечания");
        table.addCell("Документы");

        for (ApplicantDto app : applicants) {
            table.addCell(String.valueOf(app.getRequestId() != null ? app.getRequestId() : 0));
            table.addCell(app.getRequestNumber() != null ? app.getRequestNumber() : "N/A");
            table.addCell(app.getStatus() != null ? app.getStatus() : "N/A");
            table.addCell(app.getRecommendation() != null ? app.getRecommendation() : "N/A");
            table.addCell(app.getNotes() != null ? app.getNotes() : "Нет");
            table.addCell(app.getDocuments() != null ? app.getDocuments() : "Нет");
        }

        document.add(table);
    }
}

