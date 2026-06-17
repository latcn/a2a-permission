package io.github.latcn.a2a.permission.common.cache;

public final class CacheConstants {

    private CacheConstants() {
    }

    public static final String USER_PERMISSION_CACHE_PREFIX = "permission:user:";
    public static final String ROLE_VERSION_CACHE_PREFIX = "permission:role:version:";
    public static final String USER_VERSION_CACHE_PREFIX = "permission:user:version:";
    public static final String AGENT_CACHE_PREFIX = "permission:agent:";
    public static final String ACL_CACHE_PREFIX = "permission:acl:";

    public static final int LOCAL_CACHE_MAX_SIZE = 10000;
    public static final int LOCAL_CACHE_EXPIRE_MINUTES = 10;
    public static final int REDIS_CACHE_EXPIRE_HOURS = 24;

    public static final String DELIMITER = ":";

    public static String buildUserPermissionKey(Long userId) {
        return USER_PERMISSION_CACHE_PREFIX + userId;
    }

    public static String buildRoleVersionKey(Long roleId) {
        return ROLE_VERSION_CACHE_PREFIX + roleId;
    }

    public static String buildUserVersionKey(Long userId) {
        return USER_VERSION_CACHE_PREFIX + userId;
    }

    public static String buildAgentKey(String clientId) {
        return AGENT_CACHE_PREFIX + clientId;
    }

    public static String buildAclKey(String sourceClientId, String targetClientId) {
        return ACL_CACHE_PREFIX + sourceClientId + DELIMITER + targetClientId;
    }
}