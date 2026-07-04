package io.github.latcn.a2a.permission.admin.domain.repository;

import io.github.latcn.a2a.permission.admin.domain.model.A2AAcl;

import java.util.Optional;

public interface A2AAclRepository {

    Optional<A2AAcl> findById(Long id);

    Optional<A2AAcl> findBySourceAndTarget(String sourceClientId, String targetClientId);

    A2AAcl save(A2AAcl acl);

    void delete(Long id);
}