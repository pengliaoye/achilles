package com.getxinfo.support.api;

public enum ErrorCode {

    A0502("请求并发数超出限制"), A0151("手机格式校验失败");

    ErrorCode(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
