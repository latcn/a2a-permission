package io.github.latcn.a2a.permission.admin.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.latcn.a2a.permission.admin.domain.model.UserRole;
import io.github.latcn.a2a.permission.admin.domain.repository.UserRoleRepository;
import io.github.latcn.a2a.permission.admin.infrastructure.mapper.DomainModelMapper;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.UserRoleDO;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRoleRepositoryImpl implements UserRoleRepository {

    private final UserRoleMapper userRoleMapper;
    private final DomainModelMapper domainModelMapper;

    @Override
    public UserRole save(UserRole userRole) {
        UserRoleDO userRoleDO = domainModelMapper.toDO(userRole);
        if (userRoleDO.getId() == null) {
            userRoleMapper.insert(userRoleDO);
        } else {
            userRoleMapper.updateById(userRoleDO);
        }
        return domainModelMapper.toDomain(userRoleDO);
    }

    @Override
    public void deleteByUserIdAndRoleId(Long userId, Long roleId) {
        LambdaQueryWrapper<UserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoleDO::getUserId, userId).eq(UserRoleDO::getRoleId, roleId);
        userRoleMapper.delete(wrapper);
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        LambdaQueryWrapper<UserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoleDO::getRoleId, roleId);
        userRoleMapper.delete(wrapper);
    }

    @Override
    public List<UserRole> findByUserId(Long userId) {
        List<UserRoleDO> userRoleDOList = userRoleMapper.selectByUserId(userId);
        return domainModelMapper.toUserRoleList(userRoleDOList);
    }

    @Override
    public List<UserRole> findByRoleId(Long roleId) {
        LambdaQueryWrapper<UserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoleDO::getRoleId, roleId);
        List<UserRoleDO> userRoleDOList = userRoleMapper.selectList(wrapper);
        return domainModelMapper.toUserRoleList(userRoleDOList);
    }
}