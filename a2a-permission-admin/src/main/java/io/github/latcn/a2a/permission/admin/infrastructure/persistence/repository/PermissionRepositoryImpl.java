package io.github.latcn.a2a.permission.admin.infrastructure.persistence.repository;

import io.github.latcn.a2a.permission.admin.domain.model.Permission;
import io.github.latcn.a2a.permission.admin.domain.repository.PermissionRepository;
import io.github.latcn.a2a.permission.admin.infrastructure.mapper.DomainModelMapper;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.PermissionDO;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PermissionRepositoryImpl implements PermissionRepository {

    private final PermissionMapper permissionMapper;
    private final DomainModelMapper domainModelMapper;

    @Override
    public Optional<Permission> findById(Long id) {
        PermissionDO permissionDO = permissionMapper.selectById(id);
        return Optional.ofNullable(domainModelMapper.toDomain(permissionDO));
    }

    @Override
    public Permission save(Permission permission) {
        PermissionDO permissionDO = domainModelMapper.toDO(permission);
        if (permissionDO.getId() == null) {
            permissionMapper.insert(permissionDO);
        } else {
            permissionMapper.updateById(permissionDO);
        }
        return domainModelMapper.toDomain(permissionDO);
    }

    @Override
    public void delete(Long id) {
        permissionMapper.deleteById(id);
    }

    @Override
    public List<Permission> findByIds(List<Long> ids) {
        List<PermissionDO> permissionDOList = permissionMapper.selectBatchIds(ids);
        return domainModelMapper.toPermissionList(permissionDOList);
    }

    @Override
    public List<String> findAllPermissionCodes() {
        return permissionMapper.selectAllPermissionCodes();
    }
}