package io.github.latcn.a2a.permission.api.dto;

import lombok.Builder;
import lombok.Data;

import javax.management.relation.RoleInfo;
import java.util.*;

@Data
@Builder
public class TokenExchangePrepareResponse {
    private Long userId;
    private String username;
    private String combinedVersion;
    private Set<String> permissions;
    private Map<String, String> rowRules;
    private List<RoleInfo> roles;
    private AgentDTO agent;
    private AclCheckResult aclResult;
}
