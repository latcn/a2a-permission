package io.github.latcn.a2a.permission.admin.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.latcn.a2a.permission.admin.entity.AuditLog;
import io.github.latcn.a2a.permission.admin.mapper.AuditLogMapper;
import io.github.latcn.a2a.permission.api.dto.AuditLogDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RocketMQMessageListener(
    topic = "${rocketmq.consumer.audit-log.topic:audit-log-topic}",
    consumerGroup = "${rocketmq.consumer.audit-log.group:audit-log-consumer-group}"
)
public class AuditLogConsumer implements RocketMQListener<String> {

    private final AuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper;

    public AuditLogConsumer(AuditLogMapper auditLogMapper, ObjectMapper objectMapper) {
        this.auditLogMapper = auditLogMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(String message) {
        try {
            log.info("Received audit log message: {}", message);
            
            AuditLogDTO auditLogDTO = objectMapper.readValue(message, AuditLogDTO.class);
            
            AuditLog auditLog = convertToEntity(auditLogDTO);
            
            auditLogMapper.insert(auditLog);
            
            log.info("Audit log saved successfully. TraceId: {}, OperationType: {}", 
                    auditLogDTO.getTraceId(), auditLogDTO.getOperationType());
        } catch (Exception e) {
            log.error("Failed to process audit log message: {}. Error: {}", message, e.getMessage(), e);
            throw new RuntimeException("Failed to process audit log message", e);
        }
    }

    private AuditLog convertToEntity(AuditLogDTO dto) {
        AuditLog auditLog = new AuditLog();
        auditLog.setTraceId(dto.getTraceId());
        auditLog.setOperationType(dto.getOperationType() != null ? dto.getOperationType().name() : null);
        auditLog.setOperatorId(dto.getOperatorId());
        auditLog.setTargetUserId(dto.getTargetUserId());
        auditLog.setTargetRoleId(dto.getTargetRoleId());
        auditLog.setTargetPermissionId(dto.getTargetPermissionId());
        auditLog.setTargetClientId(dto.getTargetClientId());
        auditLog.setOperationDetail(dto.getOperationDetail());
        auditLog.setOperationResult(dto.getOperationResult() != null ? dto.getOperationResult().name() : null);
        auditLog.setClientIp(dto.getClientIp());
        auditLog.setUserAgent(dto.getUserAgent());
        auditLog.setCreatedAt(LocalDateTime.now());
        return auditLog;
    }
}