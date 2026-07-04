package io.github.latcn.a2a.permission.admin.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.latcn.a2a.permission.admin.domain.entity.RolePermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    List<RolePermission> selectByRoleIds(@Param("roleIds") List<Long> roleIds);

    RolePermission selectByRoleAndPermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}