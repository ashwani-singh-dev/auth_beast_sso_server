package com.zentois.authbeast.config.manager;

import java.nio.file.ProviderNotFoundException;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.zentois.authbeast.config.CustomAuthentication;
import com.zentois.authbeast.enums.ErrorDescription;

import lombok.RequiredArgsConstructor;

/**
 * Implements the AuthenticationManager interface to provide custom authentication functionality.
 * This class is responsible for authenticating user credentials using a list of AuthenticationProvider instances.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Nov-15
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager
{
    private final List<AuthenticationProvider> authenticationProviders;

    @Override
    public CustomAuthentication authenticate(Authentication authentication) throws AuthenticationException
	{
        AuthenticationException lastException = null;

        for (AuthenticationProvider provider : authenticationProviders)
        {
            if (provider.supports(authentication.getClass()))
            {
                try
                {
                    final Authentication result = provider.authenticate(authentication);
                    if (result != null && result.isAuthenticated())
                    {
                        return (CustomAuthentication) result;
                    }
                }
                catch (AuthenticationException e)
                {
                    lastException = e;
                }
            }
        }

        if (lastException != null)
        {
            throw lastException;
        }

        throw new ProviderNotFoundException(ErrorDescription.PROVIDER_NOT_FOUND + authentication.getClass().getName());
    }
}