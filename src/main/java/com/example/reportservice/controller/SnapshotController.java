package com.example.reportservice.controller;

import com.example.reportservice.dto.ReportSnapshotDto;
import com.example.reportservice.service.ReportSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/snapshots")
@RequiredArgsConstructor
public class SnapshotController {

    private final ReportSnapshotService snapshotService;

    @GetMapping("/{jobId}")
    public ResponseEntity<List<ReportSnapshotDto>> getSnapshotsByJobId(@PathVariable Long jobId) {
        return ResponseEntity.ok(snapshotService.getSnapshotsByJobId(jobId));
    }
}