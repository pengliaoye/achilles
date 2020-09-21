package com.getxinfo.support.api;

import lombok.Data;

@Data
public class ErrorResponse {

    private String error_code;
    private String error_message;
    private String stack_trace;
    private String user_tip;

}
