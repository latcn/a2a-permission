package io.github.latcn.a2a.permission.api.dto;


import lombok.Data;
import java.util.Set;

@Data
public class TokenExchangePrepareRequest {
    private Long userId;
    private String clientId;
    private String targetAgent;
    private Set<String> requestedScopes;
}
