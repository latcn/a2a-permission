package io.github.latcn.a2a.permission.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class AclCheckResult {

    private boolean allowed;
    private String sourceClientId;
    private String targetClientId;
    private Set<String> allowedScopes;
    private String reason;
}
