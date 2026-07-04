# A2A Permission 权限基础服务

AI Agent 平台权限基础服务，提供功能权限管理、数据权限管理（行级规则）、角色与权限绑定等核心能力。

**技术栈**：JDK 21 + Spring Boot 3.5.3 + MyBatis-Plus 3.5.9 + Redis 7.x + MySQL 8.0 + Spring Cloud OpenFeign + RocketMQ 5.x

---

## 1. 架构设计

### 1.1 模块划分

项目采用 Maven 多模块架构，分为四个核心模块：

```
a2a-permission/
├── a2a-permission-api/          # 【契约层】纯接口 JAR，无实现依赖
├── a2a-permission-common/        # 【公共层】缓存基础设施，无 MySQL/Feign/MQ 依赖
├── a2a-permission-remote/        # 【远程层】Feign 远程调用 + MQ 订阅（无本地 DB）
└── a2a-permission-admin/         # 【控制面】完整本地实现（Engine + Mapper + Security）
```

| 模块 | 职责 | 依赖关系 |
|------|------|----------|
| `a2a-permission-api` | DTO + 枚举 + SPI 接口定义 | 无 |
| `a2a-permission-common` | 多级缓存（LocalCache + Redis）| api + Redis + Caffeine |
| `a2a-permission-remote` | Feign 远程调用 + MQ 订阅 + 降级熔断 | api + common + Feign + MQ |
| `a2a-permission-admin` | 权限计算引擎 + MyBatis Mapper + 审计 | api + common + MySQL + RocketMQ |

### 1.2 核心设计原则

| 原则 | 说明 |
|------|------|
| **物理隔离** | `remote`（纯远程）和 `admin`（本地实现）分属不同模块，编译期杜绝误用 |
| **读写分离** | 控制面（Admin）负责写入和计算，数据面（Remote）负责读取和缓存 |
| **缓存优先** | 所有读取操作优先走多级缓存（Caffeine → Redis）|
| **最终一致性** | 权限变更通过 RocketMQ 广播，数据面缓存异步刷新 |
| **双版本驱动** | 用户权限版本 + 角色权限版本 MD5 聚合共同决定 Token 有效性 |
| **安全左移** | 行级规则模板通过 AST 语法树校验，运行时强制参数绑定 |

### 1.3 部署架构

```
┌─────────────────────────────────────────────────────────────────┐
│                     上层调用方（认证服务/Agent节点）             │
│  依赖：api + common + remote（不引入 admin）                    │
└─────────────────────────────────────────────────────────────────┘
                              │ Feign 调用
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              控制面 - a2a-permission-admin                      │
│  职责：数据写入、权限计算、Redis 更新、MQ 发送                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Redis + MySQL + RocketMQ                      │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. 模块依赖关系

```
                    a2a-permission-api
                           ▲
                           │ 依赖
    ┌──────────────────────┴───────────────────────┐
    │         a2a-permission-common               │
    │  缓存基础设施（LocalCache + Redis）          │
    └───────────────────────┬─────────────────────┘
                            │
          ┌─────────────────┴─────────────────┐
          ▼                                   ▼
┌─────────────────────┐           ┌─────────────────────┐
│ a2a-permission-    │           │ a2a-permission-     │
│ remote（远程调用层） │           │ admin（控制面）     │
│ · Feign Client      │           │ · Controller        │
│ · FallbackFactory   │           │ · Engine            │
│ · MQ Subscriber     │           │ · Mapper            │
│ · Resilience4j 熔断  │           │ · Audit             │
└─────────────────────┘           └─────────────────────┘
```

---

## 3. 核心链路

### 3.1 权限查询链路（数据面）

```
getUserFullPermissions(userId)
    │
    ▼
CachedPermissionQueryService.getUserFullPermissions()
    │
    ├──► LocalCacheManager.get()  ── Caffeine AsyncLoadingCache
    │         │
    │         └──► 命中 ──► 返回缓存
    │
    ├──► RedisCacheManager.get()  ── Redis 分布式缓存
    │         │
    │         └──► 命中 ──► 反填 LocalCache
    │
    └──► RemotePermissionQueryService.getUserFullPermissions()
              │
              └──► Feign 调用 admin 服务
```

### 3.2 权限变更链路（控制面）

```
grantRole / revokeRole / grantPermission / ...
    │
    ▼
PermissionAdminService
    │
    ├──► Mapper 操作（MySQL）
    │
    ├──► 版本号递增（乐观锁）
    │
    ├──► AuditLogService.recordAudit()
    │         │
    │         └──► AuditLogProducer.send()
    │                   │
    │                   └──► RocketMQ（审计日志队列）
    │
    └──► PermissionChangeProducer.send()
              │
              └──► RocketMQ（权限变更Topic）
```

### 3.3 缓存刷新链路

```
PermissionChangeSubscriber.onMessage()
    │
    ├──► ChangeType.USER ──► 清理用户 Redis 缓存 + 更新 UserVersionCache
    │
    └──► ChangeType.ROLE ──► 更新 RoleVersionCache（本地缓存下次访问时失效）
```

---

## 4. 数据模型

### 4.1 核心实体

| 实体 | 说明 | 关键字段 |
|------|------|----------|
| `User` | 用户 | id, username, permVersion（个人权限版本）|
| `Role` | 角色 | id, roleName, priority（优先级）, roleVersion |
| `Permission` | 权限 | id, permissionCode, actionCode, mandatoryRowRuleTemplate |
| `RolePermission` | 角色-权限关联 | roleId, permissionId, effect(ALLOW/DENY), rowRuleTemplate |
| `UserRole` | 用户-角色关联 | userId, roleId |
| `A2AAcl` | Agent 间访问控制 | sourceClientId, targetClientId, allowedScopes |

### 4.2 数据流转

```
用户请求
    │
    ▼
TokenExchangePrepare ──► 计算 combinedVersion（MD5）
                              │
                              ├── userPermVersion
                              └── roleVersions（TreeMap 排序后 MD5）
                                        │
                                        ▼
                              返回 Token + 权限数据
```

---

## 5. 权限计算引擎

### 5.1 核心组件

| 组件 | 职责 |
|------|------|
| `PermissionCalculator` | 编排权限计算流程 |
| `WildcardExpander` | 通配符权限展开 |
| `RowRuleMerger` | 多角色行级规则合并（ALLOW/DENY + 优先级）|
| `ExpressionOptimizer` | 行级规则表达式优化 |
| `MandatoryRuleInjector` | 强制规则注入 |
| `CombinedVersionCalculator` | 双版本 MD5 聚合 |
| `UserContextEnricher` | 用户上下文补充 |

### 5.2 行级规则合并策略

```
多个角色的行级规则合并：
1. 按角色优先级降序排序
2. 同 resourceType:table 的规则按 effect 分组
3. 合并公式： (allow1 OR allow2) AND NOT (deny1 OR deny2)
```

---

## 6. 缓存机制

### 6.1 多级缓存架构

```
┌─────────────────────────────────────────────────────────┐
│                    Caffeine LocalCache                  │
│            AsyncLoadingCache + RemovalListener          │
│                  进程内缓存，快速访问                     │
└─────────────────────────────────────────────────────────┘
                          │
                          │ 未命中
                          ▼
┌─────────────────────────────────────────────────────────┐
│                       Redis Cache                       │
│              分布式缓存，跨节点共享                        │
└─────────────────────────────────────────────────────────┘
                          │
                          │ 未命中
                          ▼
┌─────────────────────────────────────────────────────────┐
│                    MySQL Database                        │
│                    持久化存储                            │
└─────────────────────────────────────────────────────────┘
```

### 6.2 缓存失效机制

| 变更类型 | LocalCache | RedisCache | VersionCache |
|----------|------------|------------|--------------|
| 用户权限变更 | Tag 标记失效 | 删除 Key | 更新 UserVersion |
| 角色权限变更 | Tag 标记失效 | 下次版本检查时失效 | 更新 RoleVersion |

---

## 7. 安全机制

### 7.1 行级规则安全校验（RowRuleValidator）

| 校验项 | 说明 |
|--------|------|
| 危险函数检测 | 禁止 SLEEP、BENCHMARK、LOAD_FILE 等 |
| 系统变量检测 | 禁止 @@ 变量引用 |
| SQL 注释检测 | 禁止 --、/*、*/ 等注释 |
| 关键字检测 | 禁止 EXEC、INFORMATION_SCHEMA 等 |
| 语法校验 | 使用 JSQLParser AST 解析 |
| 子查询检测 | 禁止嵌套子查询 |

### 7.2 参数绑定（RowRulePreparedBinder）

- 使用 MyBatis-Plus `apply` 方法进行参数绑定
- 防止 SQL 注入攻击

---

## 8. 审计机制

### 8.1 审计日志

- 记录所有权限管理操作（角色授权、权限授予等）
- 使用强类型 `AuditDiff` 抽象类记录增量变更
- 通过 RocketMQ 外部队列保证不丢失

### 8.2 版本历史

- `t_permission_version_history`：权限版本变化历史
- `t_role_permission_version_history`：角色权限版本变化历史

---

## 9. 项目亮点

### 9.1 架构设计亮点

| 亮点 | 说明 |
|------|------|
| **四模块物理隔离** | remote 不含 MySQL，admin 不引入 remote，编译期安全 |
| **读写分离** | 控制面写入，数据面读取，职责清晰 |
| **SPI 接口设计** | api 模块定义纯 Java 接口，支持多种实现 |
| **Feign FallbackFactory** | 远程调用降级，Resilience4j 熔断保护 |

### 9.2 技术实现亮点

| 亮点 | 说明 |
|------|------|
| **多级缓存** | Caffeine（进程内）+ Redis（分布式）+ 版本号校验 |
| **异步缓存加载** | AsyncLoadingCache 防止缓存击穿 |
| **行级规则引擎** | ALLOW/DENY 策略 + 角色优先级 + 表达式优化 |
| **AST 语法树校验** | JSQLParser 解析行级规则，防止 SQL 注入 |
| **双版本号机制** | 用户版本 + 角色版本 MD5 聚合，快速失效检测 |
| **审计日志增量变更** | Diff 机制减少存储，结构化 JSON 保证一致性 |

---

## 10. 待优化项

| 优化项 | 优先级 | 说明 |
|--------|--------|------|
| **监控指标完善** | 高 | 引入 Micrometer + Prometheus，监控缓存命中率、QPS、延迟等 |
| **缓存预热机制** | 中 | 服务启动时预加载热点数据，避免冷启动性能抖动 |
| **分布式锁优化** | 中 | 版本更新使用 Redisson 分布式锁替代乐观锁 |
| **单元测试覆盖** | 中 | 补充 Engine 层和 Service 层单元测试 |
| **配置中心集成** | 低 | 接入 Apollo/Nacos，支持动态配置 |
| **多租户支持** | 低 | 考虑租户隔离需求 |

---

## 11. 模块快速参考

### 11.1 API 模块（a2a-permission-api）

```java
// 核心接口
public interface PermissionQueryService {
    TokenExchangePrepareResponse prepareTokenExchange(TokenExchangePrepareRequest request);
    UserFullPermissionDTO getUserFullPermissions(Long userId);
    AgentDTO getAgent(String clientId);
    AclCheckResult checkAcl(String sourceClientId, String targetClientId);
}
```

### 11.2 Admin 模块（a2a-permission-admin）

```java
// 核心服务
public class PermissionAdminService {
    boolean grantRole(Long operatorId, Long roleId, Set<Long> userIds);
    boolean revokeRole(Long operatorId, Long roleId, Set<Long> userIds);
    boolean grantPermission(Long operatorId, Long roleId, Set<Long> permissionIds);
    boolean createRole(Long operatorId, String roleName, String description);
    boolean createPermission(Long operatorId, String permissionCode, String actionCode);
}
```

### 11.3 Remote 模块（a2a-permission-remote）

```java
// Feign 接口
@FeignClient(name = "a2a-permission-service", fallbackFactory = PermissionQueryFallbackFactory.class)
public interface RemotePermissionQueryService {
    @GetMapping("/api/v1/permission/user/{userId}/full-permissions")
    UserFullPermissionDTO getUserFullPermissions(@PathVariable Long userId);
}
```

---

## 12. 配置说明

### 12.1 核心配置项

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `spring.datasource.url` | MySQL 连接地址 | - |
| `spring.data.redis.*` | Redis 配置 | - |
| `rocketmq.name-server` | RocketMQ 地址 | - |
| `permission.cache.local.enabled` | 启用本地缓存 | true |
| `permission.cache.redis.enabled` | 启用 Redis 缓存 | true |
| `permission.security.whitelist.tables` | 行级规则白名单表 | - |

### 12.2 模式配置

| 模式 | 适用场景 | 说明 |
|------|----------|------|
| `mode=local` | Admin 控制面 | 包含完整实现，连接 MySQL |
| `mode=remote` | 数据面调用方 | 纯远程调用，不连接 MySQL |
