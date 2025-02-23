package com.zentois.authbeast.exception;

/**
 * An exception that is thrown when an account has been archived.
 *
 * @author Ashwani Singh
 * @version 1.0
 * @since 2025-Feb-20
 */
public class AccountArchivedException extends RuntimeException
{
    /**
     * Constructs a new AccountArchivedException with the specified detail message.
     *
     * @param message the detail message.
     */
    public AccountArchivedException(String message)
    {
        super(message);
    }
}