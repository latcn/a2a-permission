package io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.RolePermissionDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RolePermissionMapper extends BaseMapper<RolePermissionDO> {

    List<RolePermissionDO> selectByRoleIds(@Param("roleIds") List<Long> roleIds);

    RolePermissionDO selectByRoleAndPermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}