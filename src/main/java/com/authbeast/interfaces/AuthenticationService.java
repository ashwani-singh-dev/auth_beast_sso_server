package com.zentois.authbeast.interfaces;

import com.zentois.authbeast.dto.request.CredentialDTO;

import jakarta.servlet.http.HttpSession;

/**
 * Interface for authentication service.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Oct-05
 */
public interface AuthenticationService
{
    /**
     * Authenticates a user by checking the email and password.
     * here we take the session for authentication same organization user can login iin sso auth server
     *
     * @param request  The credential data for authentication.
     * @param session  The HttpSession object to store the authenticated user data.
     * @return         True if the user is authenticated, false otherwise.
     */
    Boolean authenticateUser(CredentialDTO request, HttpSession session);
}