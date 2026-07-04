package io.github.latcn.a2a.permission.api.service;

import io.github.latcn.a2a.permission.api.dto.*;

public interface PermissionQueryService {

    TokenExchangePrepareResponse prepareTokenExchange(TokenExchangePrepareRequest request);

    UserFullPermissionDTO getUserFullPermissions(Long userId);

    AgentDTO getAgent(String clientId);

    AclCheckResultDTO checkAcl(String sourceClientId, String targetClientId);

}
