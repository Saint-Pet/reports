package com.smartbudget.report.service;

import com.smartbudget.report.dto.KafkaRequest;
import com.smartbudget.report.kafka.KafkaTransactionRequestProducer;
import com.smartbudget.report.kafka.KafkaTransactionResponseConsumer;
import com.smartbudget.report.model.Report;
import com.smartbudget.report.model.Transaction;
import com.smartbudget.report.repository.ReportRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private KafkaTransactionRequestProducer kafkaTransactionRequestProducer;

    @Autowired
    private KafkaTransactionResponseConsumer kafkaTransactionResponseConsumer;

    public Report generateReport(Integer userId, LocalDate startDate, LocalDate endDate) {
        KafkaRequest request = new KafkaRequest();
        request.setUserId(userId);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        kafkaTransactionRequestProducer.sendTransactionRequest(request);

        try {
            CompletableFuture<List<Transaction>> future = kafkaTransactionResponseConsumer.getFuture();
            List<Transaction> transactions = future.get(30, TimeUnit.SECONDS);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(transactions);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("createdBy", "Report Service");

            JasperReport jasperReport = JasperCompileManager.compileReport("src/main/resources/reports/transactions.jrxml");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            byte[] reportData = JasperExportManager.exportReportToPdf(jasperPrint);

            // Сохранение отчета в базе данных
            Report report = new Report();
            report.setReportName("Transaction Report");
            report.setReportFormat("PDF");
            report.setReportData(reportData);
            report.setCreatedTime(LocalDateTime.now());
            return reportRepository.save(report);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate report", e);
        }
    }
}
