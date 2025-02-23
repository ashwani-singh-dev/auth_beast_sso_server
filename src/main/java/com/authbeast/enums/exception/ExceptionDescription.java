package com.zentois.authbeast.enums.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum containing exception messages used throughout the SSO server implementation.
 * Each enum value represents a specific type of exception that can occur, along with a corresponding error message and exception type.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Nov-18
 */
@Getter
@AllArgsConstructor
public enum ExceptionDescription
{
    EMAIL_SENDING_ERROR("Email sending failed", "email"),
    
    REFRESH_TOKEN_INVALID_ERROR("Invalid refresh token", "authentication"),

    private String message;

    private String type;
}