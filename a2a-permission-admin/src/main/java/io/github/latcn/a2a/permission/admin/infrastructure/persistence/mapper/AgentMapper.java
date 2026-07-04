package io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.AgentDO;
import org.apache.ibatis.annotations.Param;

public interface AgentMapper extends BaseMapper<AgentDO> {

    AgentDO selectByClientId(@Param("clientId") String clientId);
}