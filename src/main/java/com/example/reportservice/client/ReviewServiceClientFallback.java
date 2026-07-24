package com.example.reportservice.client;

import com.example.reportservice.dto.ReviewDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class ReviewServiceClientFallback implements ReviewServiceClient {

    @Override
    public List<ReviewDto> getAllReviews() {
        log.warn("⚠️ review-service недоступен, возвращаем тестовые данные");
        return Arrays.asList(
                createReview(1L, 1L, "REQ-001", "Рецензент А.", "COMPLETED", "Одобрено", LocalDateTime.now().minusDays(2)),
                createReview(2L, 2L, "REQ-002", "Рецензент Б.", "IN_PROGRESS", null, LocalDateTime.now().minusDays(1)),
                createReview(3L, 3L, "REQ-003", "Рецензент В.", "REJECTED", "Требуется доработка", LocalDateTime.now().minusDays(5))
        );
    }

    @Override
    public List<ReviewDto> getCompletedReviews() {
        return getAllReviews().stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()))
                .toList();
    }

    private ReviewDto createReview(Long id, Long requestId, String number, String reviewer,
                                   String status, String comments, LocalDateTime completed) {
        ReviewDto dto = new ReviewDto();
        dto.setId(id);
        dto.setRequestId(requestId);
        dto.setRequestNumber(number);
        dto.setReviewerName(reviewer);
        dto.setStatus(status);
        dto.setComments(comments);
        dto.setAssignedAt(LocalDateTime.now().minusDays(3));
        dto.setCompletedAt(completed);
        return dto;
    }
}