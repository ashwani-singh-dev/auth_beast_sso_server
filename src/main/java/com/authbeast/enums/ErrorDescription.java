package com.zentois.authbeast.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum containing exception messages used throughout the SSO server implementation.
 * Each enum value represents a specific type of exception that can occur, along with a corresponding error message.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Nov-18
 */
@Getter
@AllArgsConstructor
public enum ErrorDescription
{
    INVALID_REDIRECT_URI("Invalid redirect URI"),

    INVALID_OTP_ENTERED("Invalid OTP entered. Please try again."),

    private String message;
}