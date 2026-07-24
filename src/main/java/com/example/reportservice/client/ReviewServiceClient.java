package com.example.reportservice.client;

import com.example.reportservice.dto.ReviewDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(
        name = "review-service",
        url = "http://localhost:8084",
        fallback = ReviewServiceClientFallback.class
)
public interface ReviewServiceClient {

    @GetMapping("/api/v1/reviews")
    List<ReviewDto> getAllReviews();

    @GetMapping("/api/v1/reviews/status/completed")
    List<ReviewDto> getCompletedReviews();
}