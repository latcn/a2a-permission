package io.github.latcn.a2a.permission.remote.enums;

import io.github.latcn.archbase.core.exception.IErrorCode;

public enum RemoteErrorEnum implements IErrorCode  {

    FALL_BACK("Remote-Invoke-FallBack", "远程调用失败，降级处理"),

    ;

    private final String code;
    private final String message;

    RemoteErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
