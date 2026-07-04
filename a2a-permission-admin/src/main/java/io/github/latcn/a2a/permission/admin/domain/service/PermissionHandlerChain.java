package io.github.latcn.a2a.permission.admin.domain.service;

import io.github.latcn.a2a.permission.api.dto.UserFullPermissionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionHandlerChain {

    private final PermissionCalculateHandler firstHandler;

    public PermissionHandlerChain(DataLoadHandler dataLoadHandler,
                                   ValidationHandler validationHandler,
                                   RowRuleProcessHandler rowRuleProcessHandler,
                                   VersionCalculateHandler versionCalculateHandler) {
        dataLoadHandler.setNext(validationHandler);
        validationHandler.setNext(rowRuleProcessHandler);
        rowRuleProcessHandler.setNext(versionCalculateHandler);
        versionCalculateHandler.setNext(null);
        this.firstHandler = dataLoadHandler;
    }

    public UserFullPermissionDTO execute(PermissionContext context) {
        log.debug("PermissionHandlerChain executing for userId: {}", context.getUserId());
        firstHandler.execute(context);

        if (context.hasError()) {
            log.error("PermissionHandlerChain failed for userId: {}", context.getUserId(), context.getError());
            throw new RuntimeException("Permission calculation failed", context.getError());
        }

        log.debug("PermissionHandlerChain completed for userId: {}", context.getUserId());
        return context.getResult();
    }
}