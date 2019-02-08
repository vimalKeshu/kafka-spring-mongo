package com.example.demo.service;

import com.example.demo.entities.AuditInfo;
import com.example.demo.entities.SourceObjectEntity;
import com.example.demo.exception.UnExpectedKafkaMessageException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service(value = "AuditInfo")
public class AuditInfoService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private KafkaProducerService kafkaProducerService;
    @Value(value = "${kafka.error-topic}")
    private String errorTopic;
    @Value(value = "${kafka.confirmation-topic}")
    private String confirmationTopic;

    public void processAndStore(String message) {
        try{
            SourceObjectEntity sourceObjectEntity = objectMapper.readValue(message,SourceObjectEntity.class);
            AuditInfo auditInfo = new AuditInfo();
            auditInfo.setSourceObjId(sourceObjectEntity.getSourceObjId());
            auditInfo.setSourceObjName(sourceObjectEntity.getSourceObjName());
            auditInfo.setSourceObj(sourceObjectEntity.getSourceObj());
            AuditInfo tAuditInfo = this.mongoTemplate.insert(auditInfo);
            kafkaProducerService.sendMessage(confirmationTopic,"Successfully processed message, ID:"+ tAuditInfo.getId());
        }catch(Exception ex){
            kafkaProducerService.sendMessage(errorTopic,"Failed to process message: "+message);
        }
    }
}
