package com.zentois.authbeast.model;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.zentois.authbeast.model.nceph_entity.Account;

/**
 * Custom implementation of AuditorAware interface to provide the current auditor's username.
 *
 * @author Ashwani
 * @version 1.1
 * @since 2024-July-16
 */
public class AuditorAwareImpl implements AuditorAware<String>
{
    /**
     * Returns the current auditor's username.
     *
     * @return An {@link Optional} containing the current auditor's username.
     *         If the authentication object is null or the principal is not an instance of {@link UserDetails},
     *         it returns "system".
     */
    @Override
    public Optional<String> getCurrentAuditor()
    {    
        final var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Account))
        {
            return Optional.of("system");
        }   

        final Account account = (Account) authentication.getPrincipal();
        return Optional.ofNullable(account.getId());
    }
}
