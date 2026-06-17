package io.github.latcn.a2a.permission.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.latcn.a2a.permission.admin.entity.A2AAcl;
import org.apache.ibatis.annotations.Param;

public interface A2AAclMapper extends BaseMapper<A2AAcl> {

    A2AAcl selectBySourceAndTarget(@Param("sourceClientId") String sourceClientId,
                                   @Param("targetClientId") String targetClientId);
}