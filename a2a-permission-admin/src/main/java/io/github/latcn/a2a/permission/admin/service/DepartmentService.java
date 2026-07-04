package io.github.latcn.a2a.permission.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.latcn.a2a.permission.admin.domain.entity.Department;
import io.github.latcn.a2a.permission.admin.infra.mapper.DepartmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentMapper departmentMapper;
    private final RedissonClient redissonClient;

    private static final String DEPT_PATH_LOCK_PREFIX = "dept:path:lock:";
    private static final int BATCH_SIZE = 1000;

    @Transactional
    public Department createDepartment(Department department) {
        if (department.getParentId() != null) {
            departmentMapper.selectForUpdate(department.getParentId());
        }
        departmentMapper.insert(department);

        String parentPath = department.getParentId() == null ? "" : departmentMapper.selectPath(department.getParentId());
        String currentPath = parentPath + "/" + department.getId();
        departmentMapper.updateParentAndPath(department.getId(), department.getParentId(), currentPath);

        return departmentMapper.selectById(department.getId());
    }

    @Transactional
    public void moveDepartment(Long deptId, Long newParentId) {
        String lockKey = DEPT_PATH_LOCK_PREFIX + deptId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                throw new ConcurrentModificationException("部门正在被其他操作修改");
            }

            String oldPath = departmentMapper.selectPath(deptId);
            String newParentPath = newParentId == null ? "" : departmentMapper.selectPath(newParentId);
            String newPath = newParentPath + "/" + deptId;

            departmentMapper.updateParentAndPath(deptId, newParentId, newPath);

            String oldPrefix = oldPath + "/";
            String newPrefix = newPath + "/";
            int offset = 0;
            while (true) {
                List<Long> childIds = departmentMapper.selectSubDeptIdsByPath(oldPrefix);
                if (childIds.isEmpty()) break;
                for (int i = 0; i < childIds.size(); i += BATCH_SIZE) {
                    List<Long> batch = childIds.subList(i, Math.min(i + BATCH_SIZE, childIds.size()));
                    departmentMapper.batchCascadeUpdatePath(batch, oldPrefix, newPrefix);
                }
                offset += BATCH_SIZE;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取分布式锁被中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public Department create(Department department) {
        Department parent = null;
        String path = "/";

        if (department.getParentId() != null) {
            parent = departmentMapper.selectById(department.getParentId());
            if (parent != null) {
                path = parent.getPath();
            }
        }

        Department saved = new Department();
        saved.setDeptName(department.getDeptName());
        saved.setParentId(department.getParentId());
        saved.setPath(path);
        saved.setCreatedAt(LocalDateTime.now());
        saved.setUpdatedAt(LocalDateTime.now());

        departmentMapper.insert(saved);

        if (parent != null) {
            saved.setPath(parent.getPath() + saved.getId() + "/");
        } else {
            saved.setPath("/" + saved.getId() + "/");
        }
        departmentMapper.updateById(saved);

        return saved;
    }

    @Transactional
    public Department update(Long id, Department department) {
        RLock lock = redissonClient.getLock(DEPT_PATH_LOCK_PREFIX + id);
        try {
            lock.lock(10, TimeUnit.SECONDS);

            Department existing = departmentMapper.selectById(id);
            if (existing == null) {
                throw new IllegalArgumentException("Department not found: " + id);
            }

            existing.setDeptName(department.getDeptName());
            existing.setUpdatedAt(LocalDateTime.now());

            departmentMapper.updateById(existing);

            return existing;

        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public Department getById(Long id) {
        return departmentMapper.selectById(id);
    }

    public List<Department> getAll() {
        return departmentMapper.selectList(new LambdaQueryWrapper<>());
    }

    public List<Department> getChildren(Long parentId) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getParentId, parentId);
        return departmentMapper.selectList(wrapper);
    }

    public List<Department> getByPathPrefix(String pathPrefix) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Department::getPath, pathPrefix);
        return departmentMapper.selectList(wrapper);
    }

    public List<Long> getDescendantIds(Long departmentId) {
        Department dept = departmentMapper.selectById(departmentId);
        if (dept == null) {
            return List.of();
        }

        List<Department> descendants = getByPathPrefix(dept.getPath());
        List<Long> ids = new ArrayList<>();
        for (Department d : descendants) {
            ids.add(d.getId());
        }
        return ids;
    }

    @Transactional
    public void delete(Long id) {
        RLock lock = redissonClient.getLock(DEPT_PATH_LOCK_PREFIX + id);
        try {
            lock.lock(10, TimeUnit.SECONDS);

            List<Department> children = getChildren(id);
            if (!children.isEmpty()) {
                throw new IllegalStateException("Cannot delete department with children");
            }

            departmentMapper.deleteById(id);

        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}