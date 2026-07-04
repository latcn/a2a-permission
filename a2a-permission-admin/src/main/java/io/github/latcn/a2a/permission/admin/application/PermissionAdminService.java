package io.github.latcn.a2a.permission.admin.application;

import java.util.Set;

/**
 * 管理端crud权限服务
 */
public interface PermissionAdminService {

    /**
     * 授予角色
     * @param operatorId
     * @param roleId
     * @param userIds
     * @return
     */
    boolean grantRole(Long operatorId, Long roleId, Set<Long> userIds);


    /**
     * 授予权限
     * @param operatorId
     * @param roleId
     * @param permissionIds
     * @return
     */
    boolean grantPermission(Long operatorId, Long roleId, Set<Long> permissionIds);

    /**
     * 撤销角色
     * @param operatorId
     * @param roleId
     * @param userIds
     * @return
     */
    boolean revokeRole(Long operatorId, Long roleId, Set<Long> userIds);


    /**
     * 更新行级规则
     * @param operatorId
     * @param roleId
     * @param permissionId
     * @param newRowRule
     * @return
     */
    boolean updateRowRule(Long operatorId, Long roleId, Long permissionId, String newRowRule);
}
