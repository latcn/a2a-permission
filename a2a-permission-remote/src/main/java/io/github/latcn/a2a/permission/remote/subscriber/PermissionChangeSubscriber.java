package io.github.latcn.a2a.permission.remote.subscriber;

import io.github.latcn.a2a.permission.api.dto.PermissionChangeMessage;
import io.github.latcn.a2a.permission.api.enums.ChangeType;
import io.github.latcn.a2a.permission.common.cache.CacheConstants;
import io.github.latcn.a2a.permission.common.cache.RedisCacheManager;
import io.github.latcn.a2a.permission.common.cache.RoleVersionCache;
import io.github.latcn.a2a.permission.common.cache.UserVersionCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

@Slf4j
@Component
@RocketMQMessageListener(
    topic = "permission-change-topic",
    consumerGroup = "permission-remote-consumer"
)
public class PermissionChangeSubscriber implements RocketMQListener<PermissionChangeMessage> {

    private final RedisCacheManager redisCacheManager;
    private final UserVersionCache userVersionCache;
    private final RoleVersionCache roleVersionCache;

    public PermissionChangeSubscriber(
            RedisCacheManager redisCacheManager,
            UserVersionCache userVersionCache,
            RoleVersionCache roleVersionCache) {
        this.redisCacheManager = redisCacheManager;
        this.userVersionCache = userVersionCache;
        this.roleVersionCache = roleVersionCache;
    }

    @Override
    public void onMessage(PermissionChangeMessage message) {
        log.info("Received permission change message: type={}, userId={}, roleId={}, newVersion={}", 
                message.getType(), message.getUserIds(), message.getRoleId(), message.getNewVersion());

        try {
            if (message.getType() == ChangeType.USER) {
                handleUserPermissionChange(message);
            } else if (message.getType() == ChangeType.ROLE) {
                handleRolePermissionChange(message);
            } else {
                log.warn("Unknown change type: {}", message.getType());
            }
        } catch (Exception e) {
            log.error("Failed to process permission change message: {}", message, e);
        }
    }

    private void handleUserPermissionChange(PermissionChangeMessage message) {
        Set<Long> userIds = message.getUserIds();
        Long newVersion = message.getNewVersion();

        if (CollectionUtils.isEmpty(userIds)) {
            log.warn("User permission change message missing userId");
            return;
        }
        for (Long userId: userIds) {
            String redisKey = CacheConstants.buildUserPermissionKey(userId);
            redisCacheManager.delete(redisKey);
            log.info("Invalidated user permission cache for userId: {}", userId);

            if (newVersion != null) {
                userVersionCache.setUserVersion(userId, newVersion);
                log.info("Updated user version for userId: {} to version: {}", userId, newVersion);
            }
        }
    }

    private void handleRolePermissionChange(PermissionChangeMessage message) {
        Long roleId = message.getRoleId();
        Long newVersion = message.getNewVersion();

        if (roleId == null) {
            log.warn("Role permission change message missing roleId");
            return;
        }

        if (newVersion != null) {
            roleVersionCache.setRoleVersion(roleId, newVersion);
            log.info("Updated role version for roleId: {} to version: {}", roleId, newVersion);
        }

        log.info("Role permission changed for roleId: {}. Local cache will be invalidated on next access via version check.", roleId);
    }
}