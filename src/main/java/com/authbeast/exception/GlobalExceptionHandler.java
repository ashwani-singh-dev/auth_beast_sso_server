package com.zentois.authbeast.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.zentois.authbeast.dto.response.ApiResponse;
import com.zentois.authbeast.enums.ErrorDescription;
import com.nimbusds.jose.JOSEException;

/**
 * Global exception handler for the SSO server application. This class provides
 * centralized exception handling for various types of exceptions that may occur
 * during the execution of the application.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Nov-15
 */
@ControllerAdvice
public class GlobalExceptionHandler
{
    /**
     * Handles an AccessDeniedException by returning a 403 Forbidden status
     * response with the exception message.
     * 
     * @param ex the exception that was thrown
     * @return a ResponseEntity containing the error response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex)
    {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ApiResponse(false, ex.getMessage()));
    }
}