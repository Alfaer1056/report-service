package com.example.reportservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CabinetDto {
    private Long id;
    private Long userId;
    private String userName;
    private String userRole; // "APPLICANT", "EXPERT", "REVIEWER", "ADMIN"
    private String email;
    private Boolean active;
    private LocalDateTime lastLogin;
}