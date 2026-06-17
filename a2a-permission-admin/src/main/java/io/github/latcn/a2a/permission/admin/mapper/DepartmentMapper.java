package io.github.latcn.a2a.permission.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.latcn.a2a.permission.admin.entity.Department;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepartmentMapper extends BaseMapper<Department> {

    List<Department> selectByPathPrefix(@Param("pathPrefix") String pathPrefix);
}