package io.github.latcn.a2a.permission.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.latcn.a2a.permission.admin.entity.Agent;
import org.apache.ibatis.annotations.Param;

public interface AgentMapper extends BaseMapper<Agent> {

    Agent selectByClientId(@Param("clientId") String clientId);
}