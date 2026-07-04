package io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.PermissionDO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PermissionMapper extends BaseMapper<PermissionDO> {

    @Select("SELECT permission_code FROM t_permission")
    List<String> selectAllPermissionCodes();
}