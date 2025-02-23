package com.zentois.authbeast.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Defines the API paths used in the application.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2025-Feb-18
 */
@Getter
@AllArgsConstructor
public class ApiPath
{
    /**
     * Base path for the api
     */
    public static final String BASE_PATH = "/auth/v1";
    
    // AccountController endpoint
    /**
     * Endpoint for account switching modle view
     */
    public static final String ACCOUNT_CHOOSER = "/account-chooser";

    // ForgotPasswordController endpoints
    /**
     * Endpoint for password reset form view 
     */
    public static final String PASSWORD_RESET_FORM_VIEW = "/pkk";

    /**
     * Endpoint for password reset otp request
     */
    public static final String FORGET_PASSWORD_OTP_REQUEST = "/pkkb";

    // SsoTokenController endpoints
    /**
     * Endpoint for sso token verification 
     */
    public static final String VERIFICATION_SSOTOKEN = "/verification/ssotoken";
}