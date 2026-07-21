package com.example.reportservice.util;

import com.example.reportservice.dto.ExpertiseDto;
import com.example.reportservice.dto.QualityDto;
import com.example.reportservice.dto.RequestDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
public class ExcelGenerator {

    public String generateExcel(Long jobId, String templateName, String parameters,
                                String dataType, Object data) {
        String fileName = "report_" + jobId + "_" + System.currentTimeMillis() + ".xlsx";
        String filePath = "./reports/" + fileName;

        try {
            File directory = new File("./reports");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Отчет");

                // Стили
                CellStyle headerStyle = createHeaderStyle(workbook);
                CellStyle dataStyle = createDataStyle(workbook);

                // Заголовок отчета
                String title = switch (dataType) {
                    case "registry" -> "РЕЕСТР ЗАЯВОК";
                    case "expertise" -> "ХОД ЭКСПЕРТИЗ";
                    case "quality" -> "КАЧЕСТВО ЭКСПЕРТИЗ";
                    case "load" -> "НАГРУЗКА ЭКСПЕРТОВ";
                    case "risk" -> "КАРТА РИСКОВ";
                    case "applicant" -> "ОТЧЕТ ЗАЯВИТЕЛЯ";
                    default -> "ОТЧЕТ";
                };

                Row titleRow = sheet.createRow(0);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue(title);
                titleCell.setCellStyle(headerStyle);
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

                // Информация
                Row infoRow = sheet.createRow(1);
                infoRow.createCell(0).setCellValue("ID задания: " + jobId);
                infoRow.createCell(1).setCellValue("Шаблон: " + templateName);
                infoRow.createCell(2).setCellValue("Параметры: " + parameters);
                infoRow.createCell(3).setCellValue("Дата: " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

                sheet.createRow(2);

                // Данные
                switch (dataType) {
                    case "registry":
                        addRegistryData(sheet, (List<RequestDto>) data, headerStyle, dataStyle);
                        break;
                    case "expertise":
                        addExpertiseData(sheet, (List<ExpertiseDto>) data, headerStyle, dataStyle);
                        break;
                    case "quality":
                        addQualityData(sheet, (List<QualityDto>) data, headerStyle, dataStyle);
                        break;
                    case "load":
                        addLoadData(sheet, (List<LoadDto>) data, headerStyle, dataStyle);
                        break;
                    case "risk":
                        addRiskData(sheet, (List<RiskDto>) data, headerStyle, dataStyle);
                        break;
                    case "applicant":
                        addApplicantData(sheet, (List<ApplicantDto>) data, headerStyle, dataStyle);
                        break;
                }

                // Автоподбор ширины
                for (int i = 0; i < 6; i++) {
                    sheet.autoSizeColumn(i);
                    int width = sheet.getColumnWidth(i);
                    sheet.setColumnWidth(i, (int) (width * 1.2));
                }

                try (FileOutputStream out = new FileOutputStream(filePath)) {
                    workbook.write(out);
                }

                log.info("✅ Excel-отчет создан: {}", filePath);
            }

        } catch (IOException e) {
            log.error("❌ Ошибка генерации Excel: {}", e.getMessage());
            throw new RuntimeException("Не удалось сгенерировать Excel: " + e.getMessage());
        }

        return filePath;
    }

    // ============================================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ============================================================

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void addRegistryData(Sheet sheet, List<RequestDto> requests,
                                 CellStyle headerStyle, CellStyle dataStyle) {
        Row headerRow = sheet.createRow(3);
        String[] headers = {"№", "Номер заявки", "Статус", "Тип", "Эксперт"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 4;
        for (RequestDto req : requests) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(req.getId() != null ? req.getId() : 0);
            row.createCell(1).setCellValue(req.getNumber() != null ? req.getNumber() : "N/A");
            row.createCell(2).setCellValue(req.getStatus() != null ? req.getStatus() : "N/A");
            row.createCell(3).setCellValue(req.getType() != null ? req.getType() : "N/A");
            row.createCell(4).setCellValue(req.getExpertName() != null ? req.getExpertName() : "N/A");
            for (int i = 0; i < 5; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
    }

    private void addExpertiseData(Sheet sheet, List<ExpertiseDto> expertises,
                                  CellStyle headerStyle, CellStyle dataStyle) {
        Row headerRow = sheet.createRow(3);
        String[] headers = {"№", "Заявка", "Эксперт", "Статус", "Дедлайн", "Просрочка"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 4;
        for (ExpertiseDto exp : expertises) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(exp.getId() != null ? exp.getId() : 0);
            row.createCell(1).setCellValue(exp.getRequestNumber() != null ? exp.getRequestNumber() : "N/A");
            row.createCell(2).setCellValue(exp.getExpertName() != null ? exp.getExpertName() : "N/A");
            row.createCell(3).setCellValue(exp.getStatus() != null ? exp.getStatus() : "N/A");
            row.createCell(4).setCellValue(exp.getDeadline() != null ? exp.getDeadline().toString() : "N/A");
            row.createCell(5).setCellValue(exp.getDaysOverdue() != null ? exp.getDaysOverdue() : 0);
            for (int i = 0; i < 6; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
    }

    private void addQualityData(Sheet sheet, List<QualityDto> qualities,
                                CellStyle headerStyle, CellStyle dataStyle) {
        Row headerRow = sheet.createRow(3);
        String[] headers = {"№", "Заявка", "Проверяющий", "Статус", "Замечания", "Доп. экспертиза"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 4;
        for (QualityDto q : qualities) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(q.getId() != null ? q.getId() : 0);
            row.createCell(1).setCellValue(q.getRequestNumber() != null ? q.getRequestNumber() : "N/A");
            row.createCell(2).setCellValue(q.getCheckerName() != null ? q.getCheckerName() : "N/A");
            row.createCell(3).setCellValue(q.getStatus() != null ? q.getStatus() : "N/A");
            row.createCell(4).setCellValue(q.getCriticalComments() != null ? q.getCriticalComments() : "Нет");
            row.createCell(5).setCellValue(q.getRequiresAdditionalExpertise() != null &&
                    q.getRequiresAdditionalExpertise() ? "Да" : "Нет");
            for (int i = 0; i < 6; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
    }
    // ============================================================
// EXCEL ДЛЯ НОВЫХ ОТЧЕТОВ
// ============================================================

    private void addLoadData(Sheet sheet, List<LoadDto> loads,
                             CellStyle headerStyle, CellStyle dataStyle) {
        Row headerRow = sheet.createRow(3);
        String[] headers = {"Эксперт", "Назначено", "Выполнено", "Среднее время", "Сложность"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 4;
        for (LoadDto load : loads) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(load.getExpertName() != null ? load.getExpertName() : "N/A");
            row.createCell(1).setCellValue(load.getAssignedCount() != null ? load.getAssignedCount() : 0);
            row.createCell(2).setCellValue(load.getCompletedCount() != null ? load.getCompletedCount() : 0);
            row.createCell(3).setCellValue(load.getAverageTime() != null ? load.getAverageTime() : 0);
            row.createCell(4).setCellValue(load.getComplexity() != null ? load.getComplexity() : 0);
            for (int i = 0; i < 5; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
    }

    private void addRiskData(Sheet sheet, List<RiskDto> risks,
                             CellStyle headerStyle, CellStyle dataStyle) {
        Row headerRow = sheet.createRow(3);
        String[] headers = {"Категория", "Описание", "Оценка", "Митигация", "Статус"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 4;
        for (RiskDto risk : risks) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(risk.getCategory() != null ? risk.getCategory() : "N/A");
            row.createCell(1).setCellValue(risk.getDescription() != null ? risk.getDescription() : "N/A");
            row.createCell(2).setCellValue(risk.getScore() != null ? risk.getScore() : 0);
            row.createCell(3).setCellValue(risk.getMitigation() != null ? risk.getMitigation() : "N/A");
            row.createCell(4).setCellValue(risk.getStatus() != null ? risk.getStatus() : "N/A");
            for (int i = 0; i < 5; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
    }

    private void addApplicantData(Sheet sheet, List<ApplicantDto> applicants,
                                  CellStyle headerStyle, CellStyle dataStyle) {
        Row headerRow = sheet.createRow(3);
        String[] headers = {"№", "Заявка", "Статус", "Рекомендация", "Замечания", "Документы"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 4;
        for (ApplicantDto app : applicants) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(app.getRequestId() != null ? app.getRequestId() : 0);
            row.createCell(1).setCellValue(app.getRequestNumber() != null ? app.getRequestNumber() : "N/A");
            row.createCell(2).setCellValue(app.getStatus() != null ? app.getStatus() : "N/A");
            row.createCell(3).setCellValue(app.getRecommendation() != null ? app.getRecommendation() : "N/A");
            row.createCell(4).setCellValue(app.getNotes() != null ? app.getNotes() : "Нет");
            row.createCell(5).setCellValue(app.getDocuments() != null ? app.getDocuments() : "Нет");
            for (int i = 0; i < 6; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
    }
}
