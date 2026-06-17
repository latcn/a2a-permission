package io.github.latcn.a2a.permission.api.spi;

import io.github.latcn.a2a.permission.api.dto.*;

public interface PermissionQueryService {

    TokenExchangePrepareResponse prepareTokenExchange(TokenExchangePrepareRequest request);

    UserFullPermissionDTO getUserFullPermissions(Long userId);

    AgentDTO getAgent(String clientId);

    AclCheckResult checkAcl(String sourceClientId, String targetClientId);

}
