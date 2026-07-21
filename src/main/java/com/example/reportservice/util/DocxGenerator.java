package com.example.reportservice.util;

import com.example.reportservice.dto.ExpertiseDto;
import com.example.reportservice.dto.QualityDto;
import com.example.reportservice.dto.RequestDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
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
public class DocxGenerator {

    public String generateDocx(Long jobId, String templateName, String parameters,
                               String dataType, Object data) {
        String fileName = "report_" + jobId + "_" + System.currentTimeMillis() + ".docx";
        String filePath = "./reports/" + fileName;

        try {
            File directory = new File("./reports");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            try (XWPFDocument document = new XWPFDocument()) {

                // ========== ЗАГОЛОВОК ==========
                String title = switch (dataType) {
                    case "registry" -> "РЕЕСТР ЗАЯВОК";
                    case "expertise" -> "ХОД ЭКСПЕРТИЗ";
                    case "quality" -> "КАЧЕСТВО ЭКСПЕРТИЗ";
                    case "load" -> "НАГРУЗКА ЭКСПЕРТОВ";
                    case "risk" -> "КАРТА РИСКОВ";
                    case "applicant" -> "ОТЧЕТ ЗАЯВИТЕЛЯ";
                    default -> "ОТЧЕТ";
                };

                XWPFParagraph titleParagraph = document.createParagraph();
                titleParagraph.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun titleRun = titleParagraph.createRun();
                titleRun.setText(title);
                titleRun.setBold(true);
                titleRun.setFontSize(20);
                titleRun.addBreak();

                // ========== ИНФОРМАЦИЯ ==========
                XWPFParagraph infoParagraph = document.createParagraph();
                XWPFRun infoRun = infoParagraph.createRun();
                infoRun.setText("ID задания: " + jobId);
                infoRun.addBreak();
                infoRun.setText("Шаблон: " + templateName);
                infoRun.addBreak();
                infoRun.setText("Параметры: " + parameters);
                infoRun.addBreak();
                infoRun.setText("Дата генерации: " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
                infoRun.addBreak();
                infoRun.addBreak();

                // ========== ТАБЛИЦА ==========
                XWPFParagraph tableParagraph = document.createParagraph();
                XWPFRun tableRun = tableParagraph.createRun();
                tableRun.setText("Данные:");
                tableRun.setBold(true);
                tableRun.setFontSize(14);
                tableRun.addBreak();

                // Создаем таблицу в зависимости от типа данных
                switch (dataType) {
                    case "registry":
                        createRegistryTable(document, (List<RequestDto>) data);
                        break;
                    case "expertise":
                        createExpertiseTable(document, (List<ExpertiseDto>) data);
                        break;
                    case "quality":
                        createQualityTable(document, (List<QualityDto>) data);
                        break;
                    case "load":
                        createLoadTable(document, (List<LoadDto>) data);
                        break;
                    case "risk":
                        createRiskTable(document, (List<RiskDto>) data);
                        break;
                    case "applicant":
                        createApplicantTable(document, (List<ApplicantDto>) data);
                        break;
                    default:
                        XWPFParagraph defaultParagraph = document.createParagraph();
                        defaultParagraph.createRun().setText("Неизвестный тип данных");
                }

                // ========== ПОДВАЛ ==========
                XWPFParagraph footerParagraph = document.createParagraph();
                footerParagraph.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun footerRun = footerParagraph.createRun();
                footerRun.setText("Отчет сгенерирован автоматически системой Report Service");
                footerRun.setFontSize(10);
                footerRun.setItalic(true);

                // Сохраняем файл
                try (FileOutputStream out = new FileOutputStream(filePath)) {
                    document.write(out);
                }

                log.info("✅ DOCX-отчет создан: {}", filePath);
            }

        } catch (IOException e) {
            log.error("❌ Ошибка генерации DOCX: {}", e.getMessage());
            throw new RuntimeException("Не удалось сгенерировать DOCX: " + e.getMessage());
        }

        return filePath;
    }

    // ============================================================
    // ТАБЛИЦЫ ДЛЯ РАЗНЫХ ТИПОВ ДАННЫХ
    // ============================================================

    private void createRegistryTable(XWPFDocument document, List<RequestDto> requests) {
        // Создаем таблицу с 5 колонками
        XWPFTable table = document.createTable(requests.size() + 1, 5);
        table.setWidth("100%");

        // Заголовки
        String[] headers = {"№", "Номер заявки", "Статус", "Тип", "Эксперт"};
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < headers.length; i++) {
            XWPFParagraph cellParagraph = headerRow.getCell(i).getParagraphs().get(0);
            XWPFRun run = cellParagraph.createRun();
            run.setText(headers[i]);
            run.setBold(true);
        }

        // Данные
        int rowIndex = 1;
        for (RequestDto req : requests) {
            XWPFTableRow row = table.getRow(rowIndex++);
            row.getCell(0).setText(String.valueOf(req.getId() != null ? req.getId() : 0));
            row.getCell(1).setText(req.getNumber() != null ? req.getNumber() : "N/A");
            row.getCell(2).setText(req.getStatus() != null ? req.getStatus() : "N/A");
            row.getCell(3).setText(req.getType() != null ? req.getType() : "N/A");
            row.getCell(4).setText(req.getExpertName() != null ? req.getExpertName() : "N/A");
        }
    }

    private void createExpertiseTable(XWPFDocument document, List<ExpertiseDto> expertises) {
        XWPFTable table = document.createTable(expertises.size() + 1, 6);
        table.setWidth("100%");

        String[] headers = {"№", "Заявка", "Эксперт", "Статус", "Дедлайн", "Просрочка"};
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < headers.length; i++) {
            XWPFParagraph cellParagraph = headerRow.getCell(i).getParagraphs().get(0);
            XWPFRun run = cellParagraph.createRun();
            run.setText(headers[i]);
            run.setBold(true);
        }

        int rowIndex = 1;
        for (ExpertiseDto exp : expertises) {
            XWPFTableRow row = table.getRow(rowIndex++);
            row.getCell(0).setText(String.valueOf(exp.getId() != null ? exp.getId() : 0));
            row.getCell(1).setText(exp.getRequestNumber() != null ? exp.getRequestNumber() : "N/A");
            row.getCell(2).setText(exp.getExpertName() != null ? exp.getExpertName() : "N/A");
            row.getCell(3).setText(exp.getStatus() != null ? exp.getStatus() : "N/A");
            row.getCell(4).setText(exp.getDeadline() != null ? exp.getDeadline().toString() : "N/A");
            row.getCell(5).setText(exp.getDaysOverdue() != null ? String.valueOf(exp.getDaysOverdue()) : "0");
        }
    }

    private void createQualityTable(XWPFDocument document, List<QualityDto> qualities) {
        XWPFTable table = document.createTable(qualities.size() + 1, 6);
        table.setWidth("100%");

        String[] headers = {"№", "Заявка", "Проверяющий", "Статус", "Замечания", "Доп. экспертиза"};
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < headers.length; i++) {
            XWPFParagraph cellParagraph = headerRow.getCell(i).getParagraphs().get(0);
            XWPFRun run = cellParagraph.createRun();
            run.setText(headers[i]);
            run.setBold(true);
        }

        int rowIndex = 1;
        for (QualityDto q : qualities) {
            XWPFTableRow row = table.getRow(rowIndex++);
            row.getCell(0).setText(String.valueOf(q.getId() != null ? q.getId() : 0));
            row.getCell(1).setText(q.getRequestNumber() != null ? q.getRequestNumber() : "N/A");
            row.getCell(2).setText(q.getCheckerName() != null ? q.getCheckerName() : "N/A");
            row.getCell(3).setText(q.getStatus() != null ? q.getStatus() : "N/A");
            row.getCell(4).setText(q.getCriticalComments() != null ? q.getCriticalComments() : "Нет");
            row.getCell(5).setText(q.getRequiresAdditionalExpertise() != null && q.getRequiresAdditionalExpertise()
                    ? "Да" : "Нет");
        }
    }
    // ============================================================
// DOCX ДЛЯ НОВЫХ ОТЧЕТОВ
// ============================================================

    private void createLoadTable(XWPFDocument document, List<LoadDto> loads) {
        XWPFTable table = document.createTable(loads.size() + 1, 5);
        table.setWidth("100%");

        String[] headers = {"Эксперт", "Назначено", "Выполнено", "Среднее время", "Сложность"};
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.getCell(i).setText(headers[i]);
            headerRow.getCell(i).getParagraphs().get(0).getRuns().get(0).setBold(true);
        }

        int rowIndex = 1;
        for (LoadDto load : loads) {
            XWPFTableRow row = table.getRow(rowIndex++);
            row.getCell(0).setText(load.getExpertName() != null ? load.getExpertName() : "N/A");
            row.getCell(1).setText(String.valueOf(load.getAssignedCount() != null ? load.getAssignedCount() : 0));
            row.getCell(2).setText(String.valueOf(load.getCompletedCount() != null ? load.getCompletedCount() : 0));
            row.getCell(3).setText(String.valueOf(load.getAverageTime() != null ? load.getAverageTime() : 0));
            row.getCell(4).setText(String.valueOf(load.getComplexity() != null ? load.getComplexity() : 0));
        }
    }

    private void createRiskTable(XWPFDocument document, List<RiskDto> risks) {
        XWPFTable table = document.createTable(risks.size() + 1, 5);
        table.setWidth("100%");

        String[] headers = {"Категория", "Описание", "Оценка", "Митигация", "Статус"};
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.getCell(i).setText(headers[i]);
            headerRow.getCell(i).getParagraphs().get(0).getRuns().get(0).setBold(true);
        }

        int rowIndex = 1;
        for (RiskDto risk : risks) {
            XWPFTableRow row = table.getRow(rowIndex++);
            row.getCell(0).setText(risk.getCategory() != null ? risk.getCategory() : "N/A");
            row.getCell(1).setText(risk.getDescription() != null ? risk.getDescription() : "N/A");
            row.getCell(2).setText(String.valueOf(risk.getScore() != null ? risk.getScore() : 0));
            row.getCell(3).setText(risk.getMitigation() != null ? risk.getMitigation() : "N/A");
            row.getCell(4).setText(risk.getStatus() != null ? risk.getStatus() : "N/A");
        }
    }

    private void createApplicantTable(XWPFDocument document, List<ApplicantDto> applicants) {
        XWPFTable table = document.createTable(applicants.size() + 1, 6);
        table.setWidth("100%");

        String[] headers = {"№", "Заявка", "Статус", "Рекомендация", "Замечания", "Документы"};
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.getCell(i).setText(headers[i]);
            headerRow.getCell(i).getParagraphs().get(0).getRuns().get(0).setBold(true);
        }

        int rowIndex = 1;
        for (ApplicantDto app : applicants) {
            XWPFTableRow row = table.getRow(rowIndex++);
            row.getCell(0).setText(String.valueOf(app.getRequestId() != null ? app.getRequestId() : 0));
            row.getCell(1).setText(app.getRequestNumber() != null ? app.getRequestNumber() : "N/A");
            row.getCell(2).setText(app.getStatus() != null ? app.getStatus() : "N/A");
            row.getCell(3).setText(app.getRecommendation() != null ? app.getRecommendation() : "N/A");
            row.getCell(4).setText(app.getNotes() != null ? app.getNotes() : "Нет");
            row.getCell(5).setText(app.getDocuments() != null ? app.getDocuments() : "Нет");
        }
    }
}

