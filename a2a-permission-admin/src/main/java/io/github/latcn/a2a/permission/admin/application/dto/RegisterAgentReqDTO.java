package io.github.latcn.a2a.permission.admin.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAgentReqDTO {

    private Long operatorId;

    private String clientId;

    private String clientSecretHash;

    private String agentName;

    private String frameworkType;

    private String agentCardUrl;

    private String publicKey;

    private String capabilities;
}