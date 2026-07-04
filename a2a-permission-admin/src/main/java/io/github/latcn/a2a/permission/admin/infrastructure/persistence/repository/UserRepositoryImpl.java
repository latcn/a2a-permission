package io.github.latcn.a2a.permission.admin.infrastructure.persistence.repository;

import io.github.latcn.a2a.permission.admin.domain.model.User;
import io.github.latcn.a2a.permission.admin.domain.repository.UserRepository;
import io.github.latcn.a2a.permission.admin.infrastructure.mapper.DomainModelMapper;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.UserDO;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;
    private final DomainModelMapper domainModelMapper;

    @Override
    public Optional<User> findById(Long id) {
        UserDO userDO = userMapper.selectById(id);
        return Optional.ofNullable(domainModelMapper.toDomain(userDO));
    }

    @Override
    public User save(User user) {
        UserDO userDO = domainModelMapper.toDO(user);
        if (userDO.getId() == null) {
            userMapper.insert(userDO);
        } else {
            userMapper.updateById(userDO);
        }
        return domainModelMapper.toDomain(userDO);
    }

    @Override
    public void delete(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public int incrementVersionIfMatch(Long userId, Long oldVersion) {
        return userMapper.incrementVersionIfMatch(userId, oldVersion);
    }

    @Override
    public List<User> findByIds(List<Long> ids) {
        List<UserDO> userDOList = userMapper.selectBatchIds(ids);
        return domainModelMapper.toUserList(userDOList);
    }
}