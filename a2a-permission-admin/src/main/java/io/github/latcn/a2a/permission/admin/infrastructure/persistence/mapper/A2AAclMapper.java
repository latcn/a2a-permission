package io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.A2AAclDO;
import org.apache.ibatis.annotations.Param;

public interface A2AAclMapper extends BaseMapper<A2AAclDO> {

    A2AAclDO selectBySourceAndTarget(@Param("sourceClientId") String sourceClientId, @Param("targetClientId") String targetClientId);
}