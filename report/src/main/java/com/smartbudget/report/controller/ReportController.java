package com.smartbudget.report.controller;

import com.smartbudget.report.model.Report;
import com.smartbudget.report.repository.ReportRepository;
import com.smartbudget.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportRepository reportRepository;

    @GetMapping
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getReportById(@PathVariable Long id) {
        return reportRepository.findById(id)
                .map(report -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + report.getReportName() + "." + report.getReportFormat().toLowerCase());
                    return ResponseEntity.ok()
                            .contentType("PDF".equalsIgnoreCase(report.getReportFormat()) ? MediaType.APPLICATION_PDF : MediaType.APPLICATION_OCTET_STREAM)
                            .headers(headers)
                            .body(report.getReportData());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/generate")
    public ResponseEntity<Report> generateReport(
            @RequestParam Integer userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Report report = reportService.generateReport(userId, startDate, endDate);
        return ResponseEntity.ok(report);
    }
}
