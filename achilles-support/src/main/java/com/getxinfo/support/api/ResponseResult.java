package com.getxinfo.support.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseResult<T> {

    private final String code;
    private final String message;
    private final Long timestamp;
    private T data;

    public static <T> ResponseResult<T> ok(T data) {
        return ResponseResult.<T>builder()
                .code("00000")
                .timestamp(System.currentTimeMillis())
                .data(data)
                .build();

    }

    public static <T> ResponseResult<T> error(ErrorCode code) {
        return ResponseResult.<T>builder()
                .code(code.name())
                .message(code.getValue())
                .timestamp(System.currentTimeMillis())
                .build();

    }

    public static <T> ResponseResult<T> error(ErrorCode code, String message) {
        return ResponseResult.<T>builder()
                .code(code.name())
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();

    }

    public static <T> ResponseResult<T> ret(ErrorCode code, String message, T data) {
        return ResponseResult.<T>builder()
                .code(code.name())
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();

    }

    public ResponseResult<T> result(T data) {
        this.data = data;
        return this;
    }

}
