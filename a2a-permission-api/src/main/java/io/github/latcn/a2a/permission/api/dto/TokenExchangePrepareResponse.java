package io.github.latcn.a2a.permission.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenExchangePrepareResponse {
    private Long userId;
    private String username;
    private String combinedVersion;
    private Set<String> permissions;
    private Map<String, String> rowRules;
    private List<RoleInfo> roles;
    private AgentDTO agent;
    private AclCheckResultDTO aclResult;
}
