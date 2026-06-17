package io.github.latcn.a2a.permission.api.enums;

public enum UserStatus {

    NORMAL(1),
    LOCKED(2),
    DISABLED(3),
    DELETED(0);

    private final int code;

    UserStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
