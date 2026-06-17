package io.github.latcn.a2a.permission.api.enums;

public enum RiskLevel {

    LOW(0),
    MEDIUM(1),
    HIGH(2),
    CRITICAL(3);

    private final int code;

    RiskLevel(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
