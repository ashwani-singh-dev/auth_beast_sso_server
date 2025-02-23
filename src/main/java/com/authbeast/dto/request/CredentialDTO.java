package com.zentois.authbeast.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Credential information, such as email, password, and account type.
 * This DTO is used for request payloads in the SSO session management functionality.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Nov-29
 */
@Getter
@Setter
@Builder
public class CredentialDTO
{
    private String email;

    private String password;

    private Integer accountType;
}