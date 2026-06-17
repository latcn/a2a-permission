package io.github.latcn.a2a.permission.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.latcn.a2a.permission.admin.entity.Department;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepartmentMapper extends BaseMapper<Department> {

    List<Department> selectByPathPrefix(@Param("pathPrefix") String pathPrefix);

    Department selectForUpdate(@Param("id") Long id);

    List<Long> selectSubDeptIdsByPath(@Param("path") String path);

    void batchCascadeUpdatePath(@Param("childIds") List<Long> childIds,
                                @Param("oldPrefix") String oldPrefix,
                                @Param("newPrefix") String newPrefix);

    void updateParentAndPath(@Param("id") Long id, @Param("parentId") Long parentId, @Param("path") String path);

    String selectPath(@Param("id") Long id);
}