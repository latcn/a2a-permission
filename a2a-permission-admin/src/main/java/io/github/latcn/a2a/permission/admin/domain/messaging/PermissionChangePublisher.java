package io.github.latcn.a2a.permission.admin.domain.messaging;

import io.github.latcn.a2a.permission.api.dto.PermissionChangeMessage;

public interface PermissionChangePublisher {

    void publishPermissionChange(PermissionChangeMessage message);
}