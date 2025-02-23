package com.zentois.authbeast.enums;

import java.util.stream.Stream;

import com.zentois.authbeast.constants.ApiPath;
import com.zentois.authbeast.enums.controller.ControllerRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum containing all SSO endpoint configurations
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Aug-16
 */
@RequiredArgsConstructor
@Getter
public enum Endpoint
{    
    // Forgot password endpoint
    FORGOT_ENDPOINT("/auth/v1"+ApiPath.PASSWORD_RESET_FORM_VIEW, false, "Forgot password endpoint", ControllerRegistry.SSO_TOKEN_CONTROLLER.getControllerName()),
    
    SIGN_OUT("/auth/v1"+ApiPath.SPECIFIC_APPLICATION_SIGNOUT, false, "Sign out endpoint", ControllerRegistry.SIGNOUT_CONTROLLER.getControllerName()), 
    ALL_ACCOUNT_SIGN_OUT("/auth/v1"+ApiPath.SINGLE_SIGNOUT, false, "Sign out endpoint for all account", ControllerRegistry.SIGNOUT_CONTROLLER.getControllerName()), 

   
    // Sign in endpoints
    // Organization specific login endpoint
    ORGANIZATION_LOGIN("/auth/v1"+ApiPath.ORGANIZATION_SIGNIN, false, "Organization login endpoint", ControllerRegistry.SIGNIN_CONTROLLER.getControllerName()),
  
    private final String path;
    private final boolean requiresAuth;
    private final String description;
    private final String controllerType;

    /**
     * Returns a stream of all Endpoint enum values
     *
     * @return Stream<Endpoint> A stream of all endpoints
     */
    public static Stream<Endpoint> stream()
    {
        return Stream.of(Endpoint.values());
    }
}