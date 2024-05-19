package com.smartbudget.report.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbudget.report.dto.KafkaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaTransactionRequestProducer {

    private static final String REQUEST_TOPIC = "transaction_requests";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendTransactionRequest(KafkaRequest request) {
        try {
            String message = objectMapper.writeValueAsString(request);
            kafkaTemplate.send(REQUEST_TOPIC, String.valueOf(request.getUserId()), message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
