package io.github.latcn.a2a.permission.admin.engine;

import io.github.latcn.a2a.permission.admin.domain.entity.Department;
import io.github.latcn.a2a.permission.admin.domain.entity.User;
import io.github.latcn.a2a.permission.admin.infra.mapper.DepartmentMapper;
import io.github.latcn.a2a.permission.admin.infra.mapper.UserMapper;
import io.github.latcn.a2a.permission.api.dto.UserFullPermissionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserContextEnricher {

    private final DepartmentMapper departmentMapper;
    private final UserMapper userMapper;

    public UserFullPermissionDTO enrich(UserFullPermissionDTO dto, User user) {
        enrichDepartmentPath(dto, user);
        return dto;
    }

    private void enrichDepartmentPath(UserFullPermissionDTO dto, User user) {
        if (user == null || dto == null) {
            return;
        }

        List<String> departmentPath = getDepartmentPath(user.getId());
        if (!departmentPath.isEmpty()) {
            dto.setDepartmentPath(departmentPath);
        }
    }

    private List<String> getDepartmentPath(Long userId) {
        return new ArrayList<>();
    }

    public List<Long> getDescendantDepartmentIds(Long departmentId) {
        Department dept = departmentMapper.selectById(departmentId);
        if (dept == null || dept.getPath() == null) {
            return List.of(departmentId);
        }

        List<Department> descendants = departmentMapper.selectByPathPrefix(dept.getPath());
        List<Long> ids = new ArrayList<>();
        for (Department d : descendants) {
            ids.add(d.getId());
        }
        return ids;
    }

    public List<Long> getUserDepartmentIds(Long userId) {
        return new ArrayList<>();
    }
}