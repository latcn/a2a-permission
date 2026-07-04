package io.github.latcn.a2a.permission.admin.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.latcn.a2a.permission.admin.domain.model.RolePermission;
import io.github.latcn.a2a.permission.admin.domain.repository.RolePermissionRepository;
import io.github.latcn.a2a.permission.admin.infrastructure.mapper.DomainModelMapper;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.RolePermissionDO;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper.RolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RolePermissionRepositoryImpl implements RolePermissionRepository {

    private final RolePermissionMapper rolePermissionMapper;
    private final DomainModelMapper domainModelMapper;

    @Override
    public RolePermission save(RolePermission rolePermission) {
        RolePermissionDO rolePermissionDO = domainModelMapper.toDO(rolePermission);
        if (rolePermissionDO.getId() == null) {
            rolePermissionMapper.insert(rolePermissionDO);
        } else {
            rolePermissionMapper.updateById(rolePermissionDO);
        }
        return domainModelMapper.toDomain(rolePermissionDO);
    }

    @Override
    public void deleteById(Long id) {
        rolePermissionMapper.deleteById(id);
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        LambdaQueryWrapper<RolePermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermissionDO::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);
    }

    @Override
    public void deleteByPermissionId(Long permissionId) {
        LambdaQueryWrapper<RolePermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermissionDO::getPermissionId, permissionId);
        rolePermissionMapper.delete(wrapper);
    }

    @Override
    public void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId) {
        LambdaQueryWrapper<RolePermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermissionDO::getRoleId, roleId).eq(RolePermissionDO::getPermissionId, permissionId);
        rolePermissionMapper.delete(wrapper);
    }

    @Override
    public Optional<RolePermission> findByRoleIdAndPermissionId(Long roleId, Long permissionId) {
        RolePermissionDO rolePermissionDO = rolePermissionMapper.selectByRoleAndPermission(roleId, permissionId);
        return Optional.ofNullable(domainModelMapper.toDomain(rolePermissionDO));
    }

    @Override
    public List<RolePermission> findByRoleIds(List<Long> roleIds) {
        List<RolePermissionDO> rolePermissionDOList = rolePermissionMapper.selectByRoleIds(roleIds);
        return domainModelMapper.toRolePermissionList(rolePermissionDOList);
    }

    @Override
    public List<RolePermission> findByRoleId(Long roleId) {
        LambdaQueryWrapper<RolePermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermissionDO::getRoleId, roleId);
        List<RolePermissionDO> rolePermissionDOList = rolePermissionMapper.selectList(wrapper);
        return domainModelMapper.toRolePermissionList(rolePermissionDOList);
    }
}