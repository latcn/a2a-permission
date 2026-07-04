package io.github.latcn.a2a.permission.admin.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.latcn.a2a.permission.admin.domain.model.Role;
import io.github.latcn.a2a.permission.admin.domain.repository.RoleRepository;
import io.github.latcn.a2a.permission.admin.infrastructure.mapper.DomainModelMapper;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.RoleDO;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleMapper roleMapper;
    private final DomainModelMapper domainModelMapper;

    @Override
    public Optional<Role> findById(Long id) {
        RoleDO roleDO = roleMapper.selectById(id);
        return Optional.ofNullable(domainModelMapper.toDomain(roleDO));
    }

    @Override
    public Optional<Role> findByName(String name) {
        LambdaQueryWrapper<RoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleDO::getRoleName, name);
        RoleDO roleDO = roleMapper.selectOne(wrapper);
        return Optional.ofNullable(domainModelMapper.toDomain(roleDO));
    }

    @Override
    public Role save(Role role) {
        RoleDO roleDO = domainModelMapper.toDO(role);
        if (roleDO.getId() == null) {
            roleMapper.insert(roleDO);
        } else {
            roleMapper.updateById(roleDO);
        }
        return domainModelMapper.toDomain(roleDO);
    }

    @Override
    public void delete(Long id) {
        roleMapper.deleteById(id);
    }

    @Override
    public int incrementVersionIfMatch(Long roleId, Long oldVersion) {
        return roleMapper.incrementVersionIfMatch(roleId, oldVersion);
    }

    @Override
    public List<Role> findByIds(List<Long> ids) {
        List<RoleDO> roleDOList = roleMapper.selectBatchIds(ids);
        return domainModelMapper.toRoleList(roleDOList);
    }
}