package io.github.latcn.a2a.permission.api.dto;

import io.github.latcn.a2a.permission.api.enums.AgentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentDTO {

    private Long id;
    private String clientId;
    private String agentName;
    private String frameworkType;
    private String agentCardUrl;
    private String publicKey;
    private Set<String> capabilities;
    private AgentStatus status;
}
