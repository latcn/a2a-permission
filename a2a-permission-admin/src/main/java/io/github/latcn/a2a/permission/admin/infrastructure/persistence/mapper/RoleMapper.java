package io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.RoleDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface RoleMapper extends BaseMapper<RoleDO> {

    @Update("UPDATE t_role SET role_version = role_version + 1, updated_at = CURRENT_TIMESTAMP(6) " +
            "WHERE id = #{roleId} AND role_version = #{oldVersion}")
    int incrementVersionIfMatch(@Param("roleId") Long roleId, @Param("oldVersion") Long oldVersion);
}