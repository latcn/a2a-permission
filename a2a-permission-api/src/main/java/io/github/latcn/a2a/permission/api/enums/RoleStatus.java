package io.github.latcn.a2a.permission.api.enums;

public enum RoleStatus {

    ACTIVE(1),
    INACTIVE(0);

    private final int code;

    RoleStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}