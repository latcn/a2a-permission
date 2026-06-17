package io.github.latcn.a2a.permission.api.enums;

public enum AgentStatus {

    NORMAL(1),
    SUSPENDED(2),
    CANCELLED(3);

    private final int code;

    AgentStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
