package com.project.test.common.exception;

import lombok.Getter;

/**
 * 统一异常处理
 */
@Getter
public class CommonException extends RuntimeException {
    private final Integer code;
    private final String message;

    public CommonException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public CommonException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.message = message;
    }

    public CommonException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public CommonException(String message) {
        super(message);
        this.code = ErrorCode.INTERNAL_ERROR.getCode();
        this.message = message;
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
        this.code = ErrorCode.INTERNAL_ERROR.getCode();
        this.message = message;
    }
}
