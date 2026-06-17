package io.github.latcn.a2a.permission.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.latcn.a2a.permission.admin.entity.Permission;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {

    @Select("SELECT permission_code FROM t_permission")
    List<String> selectAllPermissionCodes();
}