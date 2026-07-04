package io.github.latcn.a2a.permission.admin.infrastructure.persistence.repository;

import io.github.latcn.a2a.permission.admin.domain.model.Agent;
import io.github.latcn.a2a.permission.admin.domain.repository.AgentRepository;
import io.github.latcn.a2a.permission.admin.infrastructure.mapper.DomainModelMapper;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.AgentDO;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper.AgentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AgentRepositoryImpl implements AgentRepository {

    private final AgentMapper agentMapper;
    private final DomainModelMapper domainModelMapper;

    @Override
    public Optional<Agent> findById(Long id) {
        AgentDO agentDO = agentMapper.selectById(id);
        return Optional.ofNullable(domainModelMapper.toDomain(agentDO));
    }

    @Override
    public Optional<Agent> findByClientId(String clientId) {
        AgentDO agentDO = agentMapper.selectByClientId(clientId);
        return Optional.ofNullable(domainModelMapper.toDomain(agentDO));
    }

    @Override
    public Agent save(Agent agent) {
        AgentDO agentDO = domainModelMapper.toDO(agent);
        if (agentDO.getId() == null) {
            agentMapper.insert(agentDO);
        } else {
            agentMapper.updateById(agentDO);
        }
        return domainModelMapper.toDomain(agentDO);
    }

    @Override
    public void delete(Long id) {
        agentMapper.deleteById(id);
    }
}