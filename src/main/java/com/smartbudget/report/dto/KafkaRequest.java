package com.smartbudget.report.dto;

import java.time.LocalDate;

public class KafkaRequest {
    private Integer userId;
    private LocalDate startDate;
    private LocalDate endDate;

    // Getters and setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "KafkaRequest{" +
                "userId=" + userId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
