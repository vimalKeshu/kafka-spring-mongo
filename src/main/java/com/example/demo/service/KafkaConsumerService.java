package com.example.demo.service;

import com.example.demo.exception.UnExpectedKafkaMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    AuditInfoService auditInfoService;

    @KafkaListener(topics = "activity", groupId = "activity")
    public void receive(@Payload String payload,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received Message: " + payload + "from partition: " + partition);
        LOGGER.info("received payload='{}'", payload);
        auditInfoService.processAndStore(payload);
    }

}
