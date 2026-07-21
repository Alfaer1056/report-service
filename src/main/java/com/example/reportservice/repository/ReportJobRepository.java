package com.example.reportservice.repository;

import com.example.reportservice.entity.ReportJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportJobRepository extends JpaRepository<ReportJob, Long> {
    List<ReportJob> findByStatus(String status);
    List<ReportJob> findByTemplateId(Long templateId);
}