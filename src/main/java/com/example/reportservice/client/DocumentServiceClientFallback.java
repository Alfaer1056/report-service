package com.example.reportservice.client;

import com.example.reportservice.dto.DocumentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class DocumentServiceClientFallback implements DocumentServiceClient {

    @Override
    public List<DocumentDto> getAllDocuments() {
        log.warn("⚠️ document-service недоступен, возвращаем тестовые данные");
        return Arrays.asList(
                createDocument(1L, 1L, "REQ-001", "Паспорт.pdf", "PDF", "http://docs.com/1", "Иванов И.И.", LocalDateTime.now().minusDays(5)),
                createDocument(2L, 2L, "REQ-002", "Справка.docx", "DOCX", "http://docs.com/2", "Петров П.П.", LocalDateTime.now().minusDays(3)),
                createDocument(3L, 3L, "REQ-003", "Фото.jpg", "IMG", "http://docs.com/3", "Сидоров С.С.", LocalDateTime.now().minusDays(1))
        );
    }

    @Override
    public List<DocumentDto> getRecentDocuments() {
        return getAllDocuments();
    }

    private DocumentDto createDocument(Long id, Long requestId, String number,
                                       String name, String type, String url, String uploadedBy, LocalDateTime uploadedAt) {
        DocumentDto dto = new DocumentDto();
        dto.setId(id);
        dto.setRequestId(requestId);
        dto.setRequestNumber(number);
        dto.setDocumentName(name);
        dto.setDocumentType(type);
        dto.setFileUrl(url);
        dto.setUploadedBy(uploadedBy);
        dto.setUploadedAt(uploadedAt);
        return dto;
    }
}