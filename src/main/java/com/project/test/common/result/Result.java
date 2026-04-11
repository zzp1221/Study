package com.project.test.common.result;

import com.project.test.common.constant.CommonConstants;
import com.project.test.common.exception.ErrorCode;

/**
 * 统一结果相应
 */
public class Result<T> {
    private final Integer code;
    private final String message;
    private final T data;

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    // ========== 成功响应 ==========

    public static <T> Result<T> success() {
        return new Result<>(CommonConstants.StatusCode.SUCCESS, "", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(CommonConstants.StatusCode.SUCCESS, "", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(CommonConstants.StatusCode.SUCCESS, message != null ? message : "", data);
    }

    // ========== 失败响应 ==========

    public static <T> Result<T> error(String message) {
        return new Result<>(CommonConstants.StatusCode.SERVER_ERROR, message, null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> Result<T> error(ErrorCode errorCode, String message) {
        return new Result<>(errorCode.getCode(), message, null);
    }
}
