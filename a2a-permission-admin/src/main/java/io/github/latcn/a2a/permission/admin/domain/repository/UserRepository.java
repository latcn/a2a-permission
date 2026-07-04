package io.github.latcn.a2a.permission.admin.domain.repository;

import io.github.latcn.a2a.permission.admin.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    User save(User user);

    void delete(Long id);

    int incrementVersionIfMatch(Long userId, Long oldVersion);

    List<User> findByIds(List<Long> ids);
}