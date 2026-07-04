package io.github.latcn.a2a.permission.admin.infrastructure.persistence.repository;

import io.github.latcn.a2a.permission.admin.domain.model.A2AAcl;
import io.github.latcn.a2a.permission.admin.domain.repository.A2AAclRepository;
import io.github.latcn.a2a.permission.admin.infrastructure.mapper.DomainModelMapper;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.A2AAclDO;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper.A2AAclMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class A2AAclRepositoryImpl implements A2AAclRepository {

    private final A2AAclMapper a2AAclMapper;
    private final DomainModelMapper domainModelMapper;

    @Override
    public Optional<A2AAcl> findById(Long id) {
        A2AAclDO a2AAclDO = a2AAclMapper.selectById(id);
        return Optional.ofNullable(domainModelMapper.toDomain(a2AAclDO));
    }

    @Override
    public Optional<A2AAcl> findBySourceAndTarget(String sourceClientId, String targetClientId) {
        A2AAclDO a2AAclDO = a2AAclMapper.selectBySourceAndTarget(sourceClientId, targetClientId);
        return Optional.ofNullable(domainModelMapper.toDomain(a2AAclDO));
    }

    @Override
    public A2AAcl save(A2AAcl acl) {
        A2AAclDO a2AAclDO = domainModelMapper.toDO(acl);
        if (a2AAclDO.getId() == null) {
            a2AAclMapper.insert(a2AAclDO);
        } else {
            a2AAclMapper.updateById(a2AAclDO);
        }
        return domainModelMapper.toDomain(a2AAclDO);
    }

    @Override
    public void delete(Long id) {
        a2AAclMapper.deleteById(id);
    }
}