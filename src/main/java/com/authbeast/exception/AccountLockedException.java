package com.zentois.authbeast.exception;

/**
 * Represents an exception that is thrown when a user's account has been locked.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Sep-09
 */
public class AccountLockedException extends RuntimeException
{
    /**
     * Constructs a new {@link AccountLockedException} with the specified error message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     */
    public AccountLockedException(String message)
    {
        super(message);
    }
}