package io.github.latcn.a2a.permission.api.enums;

public enum Effect {
    ALLOW(1),
    DENY(0);

    private final int code;

    Effect(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
