package com.smartbudget.report.dto;

import com.smartbudget.report.model.Transaction;
import java.util.List;

public class KafkaTransactionResponse {
    private String userId;
    private List<Transaction> transactions;

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "KafkaTransactionResponse{" +
                "userId='" + userId + '\'' +
                ", transactions=" + transactions +
                '}';
    }
}
