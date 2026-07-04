package io.github.latcn.a2a.permission.admin.domain.repository;

import io.github.latcn.a2a.permission.admin.domain.model.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository {

    Optional<Department> findById(Long id);

    Department save(Department department);

    void delete(Long id);

    List<Department> findByPathPrefix(String path);
}