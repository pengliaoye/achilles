package com.getxinfo.support.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class AccessTokenResponse {

    private final String access_token;

}
