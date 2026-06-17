package io.github.latcn.a2a.permission.admin.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.latcn.a2a.permission.api.dto.AuditLogDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuditLogProducer {

    private final RocketMQTemplate rocketMQTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rocketmq.producer.audit-log.topic:audit-log-topic}")
    private String topic;

    public AuditLogProducer(RocketMQTemplate rocketMQTemplate, ObjectMapper objectMapper) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendAuditLog(AuditLogDTO auditLogDTO) {
        try {
            String messageJson = objectMapper.writeValueAsString(auditLogDTO);
            Message<String> msg = MessageBuilder.withPayload(messageJson).build();
            
            rocketMQTemplate.asyncSend(topic, msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("Audit log message sent successfully. MsgId: {}, Topic: {}, TraceId: {}", 
                            sendResult.getMsgId(), topic, auditLogDTO.getTraceId());
                }

                @Override
                public void onException(Throwable e) {
                    log.error("Failed to send audit log message. Topic: {}, TraceId: {}, Error: {}", 
                            topic, auditLogDTO.getTraceId(), e.getMessage(), e);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize audit log message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize audit log message", e);
        }
    }

    public void sendAuditLogSync(AuditLogDTO auditLogDTO) {
        try {
            String messageJson = objectMapper.writeValueAsString(auditLogDTO);
            Message<String> msg = MessageBuilder.withPayload(messageJson).build();
            
            SendResult sendResult = rocketMQTemplate.syncSend(topic, msg);
            log.info("Audit log message sent synchronously. MsgId: {}, Topic: {}, TraceId: {}", 
                    sendResult.getMsgId(), topic, auditLogDTO.getTraceId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize audit log message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize audit log message", e);
        }
    }
}