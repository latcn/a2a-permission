package io.github.latcn.a2a.permission.remote.client;

import io.github.latcn.a2a.permission.api.dto.AclCheckResultDTO;
import io.github.latcn.a2a.permission.api.dto.AgentDTO;
import io.github.latcn.a2a.permission.api.dto.TokenExchangePrepareRequest;
import io.github.latcn.a2a.permission.api.dto.TokenExchangePrepareResponse;
import io.github.latcn.a2a.permission.api.dto.UserFullPermissionDTO;
import io.github.latcn.a2a.permission.remote.fallback.PermissionQueryFallbackFactory;
import io.github.latcn.archbase.foundation.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "a2a-permission-service",
    contextId = "permissionClient",
    url= "http://localhost:8080",
    fallbackFactory = PermissionQueryFallbackFactory.class
)
public interface RemotePermissionQueryService {

    @PostMapping("/api/v1/permission/token-exchange/prepare")
    Result<TokenExchangePrepareResponse> prepareTokenExchange(@RequestBody TokenExchangePrepareRequest request);

    @GetMapping("/api/v1/permission/user/{userId}/full-permissions")
    Result<UserFullPermissionDTO> getUserFullPermissions(@PathVariable("userId") Long userId);

    @GetMapping("/api/v1/permission/agent/{clientId}")
    Result<AgentDTO> getAgent(@PathVariable("clientId") String clientId);

    @GetMapping("/api/v1/permission/acl/{sourceClientId}/{targetClientId}")
    Result<AclCheckResultDTO> checkAcl(@PathVariable("sourceClientId") String sourceClientId,
                               @PathVariable("targetClientId") String targetClientId);
}