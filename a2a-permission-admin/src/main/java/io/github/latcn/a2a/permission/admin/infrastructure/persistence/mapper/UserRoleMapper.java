package io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.UserRoleDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserRoleMapper extends BaseMapper<UserRoleDO> {

    List<UserRoleDO> selectByUserId(@Param("userId") Long userId);
}