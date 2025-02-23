package com.zentois.authbeast.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for the refresh token request.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Nov-14
 */
@Getter
@Setter
public class RefreshApplicationTokenDTO
{
    private String refreshToken;

    private String appName;
}