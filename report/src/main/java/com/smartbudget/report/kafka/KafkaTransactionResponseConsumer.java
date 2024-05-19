package com.smartbudget.report.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbudget.report.dto.KafkaTransactionResponse;
import com.smartbudget.report.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaTransactionResponseConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    private CompletableFuture<List<Transaction>> future;

    @KafkaListener(topics = "transaction_responses", groupId = "group_id")
    public void consume(String message) {
        try {
            KafkaTransactionResponse response = objectMapper.readValue(message, KafkaTransactionResponse.class);
            System.out.println("Consumed response: " + response);
            if (future != null && response.getTransactions() != null) {
                future.complete(response.getTransactions());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<List<Transaction>> getFuture() {
        this.future = new CompletableFuture<>();
        return this.future;
    }
}
