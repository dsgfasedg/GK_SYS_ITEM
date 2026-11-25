package com.gk.zhixue.common.exception;

public class BusinessException extends RuntimeException {
    private final int code;
    private final String message;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public static BusinessException parameterError(String msg) {
        return new BusinessException(1001, msg);
    }

    public static BusinessException notFound(String msg) {
        return new BusinessException(2001, msg);
    }

    public static BusinessException forbidden(String msg) {
        return new BusinessException(4001, msg);
    }

    public int getCode() { return code; }

    @Override
    public String getMessage() { return message; }
}
