package com.zentois.authbeast.config.provider;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.zentois.framework.email.dto.Context;
import com.zentois.framework.email.enums.EmailType;
import com.zentois.framework.email.service.EmailSenderService;
import com.zentois.authbeast.config.CustomAuthentication;
import com.zentois.authbeast.config.SecurityProperties;
import com.zentois.authbeast.dto.JwtAccountDTO;
import com.zentois.authbeast.dto.JwtEntityDTO;
import com.zentois.authbeast.enums.ErrorDescription;
import com.zentois.authbeast.exception.AccountArchivedException;
import com.zentois.authbeast.exception.AccountLockedException;
import com.zentois.authbeast.model.nceph_entity.Account;
import com.zentois.authbeast.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

/**
 * CustomAuthenticationProvider is an implementation of the AuthenticationProvider interface.
 * It provides a mechanism for authenticating users using their credentials.
 * This provider retrieves user account details from the AccountRepository and verifies
 * the provided credentials. It also handles failed login attempts and locks the account
 * if the maximum number of failed attempts is reached.
 * 
 * Use this provider ensures that only authenticated users can access secured resources.
 * 
 * Note: Ensure that the AccountRepository is properly configured for this provider to function.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Nov-04
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider
{
    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final AccountRepository accountRepository;

    private final EmailSenderService emailSenderService;

    private final BeanFactory beanFactory;

    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Authenticates the given user using the provided credentials.
     * 
     * @param authentication the Authentication object containing the user's credentials
     * @return the authenticated user object
     * @throws AuthenticationException if the user is not found, the account is locked, or the credentials are invalid
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        final Account account = accountRepository.getByEmail(authentication.getName());

        if (account == null)
        {
            throw new UsernameNotFoundException(ErrorDescription.USER_NOT_FOUND.getMessage());
        }

        if (!account.isEnabled())
        {
            throw new AccountLockedException(ErrorDescription.ACCOUNT_LOCKED_MESSAGE.getMessage());
        }
        
        if (account.isArchive())
        {
            throw new AccountArchivedException(ErrorDescription.ACCOUNT_ARCHIVED_MESSAGE.getMessage());
        }

        if (!passwordEncoder.matches(authentication.getCredentials().toString(), account.getPassword()))
        {
            handleFailedLogin(account);
            throw new BadCredentialsException(ErrorDescription.INVALID_CREDENTIAL.getMessage());
        }

        resetFailedAttempts(account);

        final JwtAccountDTO jwtAccountDTO = buildJwtAccountDTO(account);

        final CustomAuthentication customAuth = new CustomAuthentication(jwtAccountDTO);
        customAuth.setAuthenticated(true);
        return customAuth;
    }

    /**
     * Determines if the given {@code authentication} object is supported by this
     * {@link AuthenticationProvider}.
     *
     * @param authentication the authentication object to check
     * @return true if the authentication is supported, false otherwise
     */
    @Override
    public boolean supports(Class<?> authentication)
    {
        return Authentication.class.isAssignableFrom(authentication);
    }

    /**
     * Handles failed login attempts by incrementing the failed attempts count for the account.
     *
     * This method checks if the failed attempts have reached the maximum allowed attempts.
     * If so, it locks the account to prevent further login attempts. The updated account
     * information is then saved to the account repository.
     *
     * @param account The account for which the login attempt failed.
     */
    private void handleFailedLogin(Account account)
    {
        int failedAttempts = account.getFailedAttempts() + 1;
        account.setFailedAttempts(failedAttempts);
        if (failedAttempts >= MAX_FAILED_ATTEMPTS)
        {
            lockAccount(account);
        }
        accountRepository.save(account);
    }

    /**
     * Resets the failed login attempts count for the specified account.
     *
     * This method sets the failedAttempts field to 0 and persists the updated account
     * information to the accountRepository. It is typically called after a successful
     * authentication to ensure that the failed attempts counter is cleared.
     *
     * @param account The account for which the failed attempts count should be reset.
     */
    private void resetFailedAttempts(Account account)
    {
        account.setFailedAttempts(0);
        accountRepository.save(account);
    }

    /**
     * Locks the account and sends an email notification to the user.
     *
     * This method disables the account, and sends an email to the user's email address
     * informing them that their account has been locked due to excessive failed login
     * attempts.
     *
     * @param account the account to be locked
     */
    private void lockAccount(Account account)
    {
        account.setEnabled(false);
        final Context context = Context.builder()
                .emailType(EmailType.ACCOUNT_LOCK_INFORMATION_EMAIL)
                .emailReceiver(account.getEmail())
                .emailSender(beanFactory.getBean(SecurityProperties.class).getSenderEmail())
                .build();
        emailSenderService.sendEmail(context);
    }

    /**
     * Constructs a JwtAccountDTO object from the provided Account.
     * This method initializes the JwtEntityDTO with the entity ID
     * and sets the email, username, ID, and entity for the JwtAccountDTO.
     *
     * @param account The account from which to build the JwtAccountDTO.
     * @return The constructed JwtAccountDTO object containing the account's ID,
     *         email, username, and entity information.
     */
    private JwtAccountDTO buildJwtAccountDTO(Account account)
    {
        final JwtEntityDTO entityDTO = new JwtEntityDTO();
        entityDTO.setId(account.getEntity().getId());

        final JwtAccountDTO jwtAccountDTO = new JwtAccountDTO();
        jwtAccountDTO.setEmail(account.getEmail());
        jwtAccountDTO.setUsername(account.getUsername());
        jwtAccountDTO.setId(account.getId());
        jwtAccountDTO.setEntity(entityDTO);

        return jwtAccountDTO;
    }
}