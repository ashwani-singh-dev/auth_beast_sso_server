package com.zentois.authbeast.utils;

import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Extracts the any token from the HTTP request authorization header.
 *
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Sep-16
 */
public class TokenExtractorUtil
{
    public static final String AUTHORIZATION = "Authorization";

    /**
     * Extracts the token from the Authorization header of the provided HttpServletRequest.
     *
     *
     * @param  request      the HttpServletRequest object from which to extract the token
     * @param  tokenType    the type of token to extract (e.g. "Bearer")
     * @return          the extracted token, or null if the token is not found
     */
    public static String extractToken(HttpServletRequest request, String tokenType)
    {
        final String authHeader = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(authHeader))
        {
            final String prefix = tokenType + " ";
            if (authHeader.startsWith(prefix))
            {
                return authHeader.substring(prefix.length());
            }
        }
        return null;
    }
}