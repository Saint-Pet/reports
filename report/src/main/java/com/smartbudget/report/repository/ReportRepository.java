package com.smartbudget.report.repository;

import com.smartbudget.report.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
