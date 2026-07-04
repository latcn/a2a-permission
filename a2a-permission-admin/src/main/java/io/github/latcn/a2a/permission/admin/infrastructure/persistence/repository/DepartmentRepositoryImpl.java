package io.github.latcn.a2a.permission.admin.infrastructure.persistence.repository;

import io.github.latcn.a2a.permission.admin.domain.model.Department;
import io.github.latcn.a2a.permission.admin.domain.repository.DepartmentRepository;
import io.github.latcn.a2a.permission.admin.infrastructure.mapper.DomainModelMapper;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.DepartmentDO;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper.DepartmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements DepartmentRepository {

    private final DepartmentMapper departmentMapper;
    private final DomainModelMapper domainModelMapper;

    @Override
    public Optional<Department> findById(Long id) {
        DepartmentDO departmentDO = departmentMapper.selectById(id);
        return Optional.ofNullable(domainModelMapper.toDomain(departmentDO));
    }

    @Override
    public Department save(Department department) {
        DepartmentDO departmentDO = domainModelMapper.toDO(department);
        if (departmentDO.getId() == null) {
            departmentMapper.insert(departmentDO);
        } else {
            departmentMapper.updateById(departmentDO);
        }
        return domainModelMapper.toDomain(departmentDO);
    }

    @Override
    public void delete(Long id) {
        departmentMapper.deleteById(id);
    }

    @Override
    public List<Department> findByPathPrefix(String path) {
        List<DepartmentDO> departmentDOList = departmentMapper.selectByPathPrefix(path);
        return domainModelMapper.toDepartmentList(departmentDOList);
    }
}