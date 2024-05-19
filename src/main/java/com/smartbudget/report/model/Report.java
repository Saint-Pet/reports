package com.smartbudget.report.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reportName;
    private String reportFormat;
    private byte[] reportData;
    private LocalDateTime createdTime;

    public void setId(Long id) {
        this.id = id;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public void setReportFormat(String reportFormat) {
        this.reportFormat = reportFormat;
    }

    public void setReportData(byte[] reportData) {
        this.reportData = reportData;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public Long getId() {
        return id;
    }

    public String getReportName() {
        return reportName;
    }

    public String getReportFormat() {
        return reportFormat;
    }

    public byte[] getReportData() {
        return reportData;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
}
