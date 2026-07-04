package io.github.latcn.a2a.permission.admin.application.cmd;

import io.github.latcn.a2a.permission.admin.application.dto.*;

public interface PermissionAdminService {

    GrantRoleRespDTO grantRole(GrantRoleReqDTO req);

    RevokeRoleRespDTO revokeRole(RevokeRoleReqDTO req);

    GrantPermissionRespDTO grantPermission(GrantPermissionReqDTO req);

    UpdateRowRuleRespDTO updateRowRule(UpdateRowRuleReqDTO req);

    CreateRoleRespDTO createRole(CreateRoleReqDTO req);

    DeleteRoleRespDTO deleteRole(DeleteRoleReqDTO req);

    CreatePermissionRespDTO createPermission(CreatePermissionReqDTO req);

    DeletePermissionRespDTO deletePermission(DeletePermissionReqDTO req);

    RegisterAgentRespDTO registerAgent(RegisterAgentReqDTO req);

    RotateAgentSecretRespDTO rotateAgentSecret(RotateAgentSecretReqDTO req);
}