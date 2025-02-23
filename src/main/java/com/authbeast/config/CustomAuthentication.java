package com.zentois.authbeast.config;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.zentois.authbeast.dto.JwtAccountDTO;

/**
 * CustomAuthentication class represents a custom authentication implementation for the NCEPH User Registration application.
 * It implements the Spring Security Authentication interface and provides methods to handle user authentication and authorization.
 *
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Sep-10
 */
public class CustomAuthentication implements Authentication
{
    private final JwtAccountDTO account;

    private boolean authenticated;

    private Object details;

    /**
     * Constructs a new instance of CustomAuthentication with the given account and sets the authentication status to false.
     *
     * @param account the account associated with this authentication
     */
    public CustomAuthentication(JwtAccountDTO account)
    {
        this.account = account;
        this.authenticated = false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return account.getAuthorities();
    }

    @Override
    public String getCredentials()
    {
        return account.getPassword();
    }

    @Override
    public Object getDetails()
    {
        return details;
    }

    @Override
    public JwtAccountDTO getPrincipal()
    {
        return account;
    }

    @Override
    public boolean isAuthenticated()
    {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException
    {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName()
    {
        return account.getEmail();
    }

    /**
     * Returns the account object associated with this authentication.
     *
     * @return The account object.
     */
    public JwtAccountDTO getAccount()
    {
        return account;
    }

    /**
     * Sets the details object associated with this authentication.
     *
     * @param  object  the details object to be set
     */
    public void setDetails(Object object)
    {
        this.details = object;
    }    
}