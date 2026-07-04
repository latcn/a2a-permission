package io.github.latcn.a2a.permission.admin.domain.repository;

import io.github.latcn.a2a.permission.admin.domain.model.Agent;

import java.util.Optional;

public interface AgentRepository {

    Optional<Agent> findById(Long id);

    Optional<Agent> findByClientId(String clientId);

    Agent save(Agent agent);

    void delete(Long id);
}