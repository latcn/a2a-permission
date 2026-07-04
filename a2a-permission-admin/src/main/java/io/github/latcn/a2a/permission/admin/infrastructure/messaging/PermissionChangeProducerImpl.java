package io.github.latcn.a2a.permission.admin.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.latcn.a2a.permission.admin.domain.messaging.PermissionChangePublisher;
import io.github.latcn.a2a.permission.api.dto.PermissionChangeMessage;
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
public class PermissionChangeProducerImpl implements PermissionChangePublisher {

    private final RocketMQTemplate rocketMQTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rocketmq.producer.permission-change.topic:permission-change-topic}")
    private String topic;

    public PermissionChangeProducerImpl(RocketMQTemplate rocketMQTemplate, ObjectMapper objectMapper) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishPermissionChange(PermissionChangeMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            Message<String> msg = MessageBuilder.withPayload(messageJson).build();

            rocketMQTemplate.asyncSend(topic, msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("Permission change message sent successfully. MsgId: {}, Topic: {}",
                            sendResult.getMsgId(), topic);
                }

                @Override
                public void onException(Throwable e) {
                    log.error("Failed to send permission change message. Topic: {}, Error: {}",
                            topic, e.getMessage(), e);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize permission change message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize permission change message", e);
        }
    }

    public void sendPermissionChangeSync(PermissionChangeMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            Message<String> msg = MessageBuilder.withPayload(messageJson).build();

            SendResult sendResult = rocketMQTemplate.syncSend(topic, msg);
            log.info("Permission change message sent synchronously. MsgId: {}, Topic: {}",
                    sendResult.getMsgId(), topic);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize permission change message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize permission change message", e);
        }
    }
}