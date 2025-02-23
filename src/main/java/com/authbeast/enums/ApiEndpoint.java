package com.zentois.authbeast.enums;

import com.zentois.authbeast.enums.controller.ControllerRegistry;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiEndpoint
{
    PATH_FORGOT_PASSWORD_VIEW("/auth/v1/pkk", "/pkk", "GET", ControllerRegistry.FORGOT_PASSWORD_CONTROLLER.getControllerName(), "Show the forgot password form view page that take email"),

    PATH_SEND_OTP("/auth/v1/pkkb", "/pkkb", "POST", ControllerRegistry.FORGOT_PASSWORD_CONTROLLER.getControllerName(), "Send OTP to user's email for password reset"),

    private String path;

    private String apiSubPath;

    private String method;

    private String controllerName;

    private String description;
}