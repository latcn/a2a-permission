package io.github.latcn.a2a.permission.admin.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.latcn.a2a.permission.admin.domain.model.AuditLog;
import io.github.latcn.a2a.permission.admin.domain.repository.AuditLogRepository;
import io.github.latcn.a2a.permission.admin.infrastructure.mapper.DomainModelMapper;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.AuditLogDO;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper.AuditLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepository {

    private final AuditLogMapper auditLogMapper;
    private final DomainModelMapper domainModelMapper;

    @Override
    public AuditLog save(AuditLog auditLog) {
        AuditLogDO auditLogDO = domainModelMapper.toDO(auditLog);
        if (auditLogDO.getId() == null) {
            auditLogMapper.insert(auditLogDO);
        } else {
            auditLogMapper.updateById(auditLogDO);
        }
        return domainModelMapper.toDomain(auditLogDO);
    }

    @Override
    public Optional<AuditLog> findByTraceId(String traceId) {
        LambdaQueryWrapper<AuditLogDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuditLogDO::getTraceId, traceId);
        AuditLogDO auditLogDO = auditLogMapper.selectOne(wrapper);
        return Optional.ofNullable(domainModelMapper.toDomain(auditLogDO));
    }

    @Override
    public List<AuditLog> findByOperatorId(Long operatorId) {
        LambdaQueryWrapper<AuditLogDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuditLogDO::getOperatorId, operatorId);
        List<AuditLogDO> auditLogDOList = auditLogMapper.selectList(wrapper);
        return domainModelMapper.toAuditLogList(auditLogDOList);
    }

    @Override
    public List<AuditLog> findByOperationType(String operationType) {
        LambdaQueryWrapper<AuditLogDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuditLogDO::getOperationType, operationType);
        List<AuditLogDO> auditLogDOList = auditLogMapper.selectList(wrapper);
        return domainModelMapper.toAuditLogList(auditLogDOList);
    }
}