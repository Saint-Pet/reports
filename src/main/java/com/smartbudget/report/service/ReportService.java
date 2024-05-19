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

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ReportService {

    @Autowired
    private KafkaTransactionRequestProducer kafkaProducer;

    @Autowired
    private KafkaTransactionResponseConsumer kafkaConsumer;

    @Autowired
    private ReportRepository reportRepository;

    public Report generateReport(Integer userId, LocalDate startDate, LocalDate endDate) {
        KafkaRequest request = new KafkaRequest();
        request.setUserId(userId);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        kafkaProducer.sendTransactionRequest(request);

        CompletableFuture<List<Transaction>> future = kafkaConsumer.getFuture();

        List<Transaction> transactions = future.join(); // Дождаться ответа от Kafka

        // Логика создания отчета на основе транзакций
        Report report = new Report();
        report.setUserId(userId);
        report.setStartDate(startDate.atStartOfDay());
        report.setEndDate(endDate.atTime(LocalTime.MAX));
        report.setReportName("Financial Report for User " + userId);
        report.setReportFormat("PDF");
        report.setCreatedTime(LocalDateTime.now());

        // Генерация данных отчета в PDF формате
        try {
            byte[] reportData = generateReportData(transactions, report);
            report.setReportData(reportData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Сохранение отчета в базе данных
        reportRepository.save(report);

        return report;
    }

    private byte[] generateReportData(List<Transaction> transactions, Report report) throws Exception {
        JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/reports/transactions.jrxml"));

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(transactions);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userId", report.getUserId());
        parameters.put("totalAmount", transactions.stream().mapToDouble(Transaction::getAmount).sum());

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Сохранение отчета в виде PDF файла
        String fileName = "transactions" + report.getUserId() + report.getStartDate() + report.getEndDate() + ".pdf";
        File pdfFile = new File(fileName);
        JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdfFile));

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
