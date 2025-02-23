package com.zentois.authbeast.dto.response.callback;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Note : this class is not used in current implementation due to the lack of OAuth implementation.
 * (We are not save oauth user information in our database after registration or login from oauth)
 * 
 * Represents data returned from an OAuth callback.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Nov-14
 */
@Getter
@Setter
@Builder
public class OAuthCallbackData
{
    private String email;
    
    private String name;
    
    private String basePath;
    
    private String codeChallenge;
}