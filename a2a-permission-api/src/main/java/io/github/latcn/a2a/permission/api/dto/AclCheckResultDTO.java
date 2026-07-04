package io.github.latcn.a2a.permission.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AclCheckResultDTO {

    private boolean allowed;
    private String sourceClientId;
    private String targetClientId;
    private Set<String> allowedScopes;
    private String reason;
}
