package com.getxinfo.support.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class VerifyCodeResponse {

    private final String code;

}
