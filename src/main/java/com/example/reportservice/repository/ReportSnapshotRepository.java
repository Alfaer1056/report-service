package com.example.reportservice.repository;

import com.example.reportservice.entity.ReportSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportSnapshotRepository extends JpaRepository<ReportSnapshot, Long> {
    List<ReportSnapshot> findByJobId(Long jobId);
}