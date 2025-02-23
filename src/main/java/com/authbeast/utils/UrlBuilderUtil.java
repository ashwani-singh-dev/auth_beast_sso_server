package com.zentois.authbeast.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.zentois.authbeast.constants.ApiPath;
import com.zentois.authbeast.enums.ErrorDescription;
import com.zentois.authbeast.enums.PathEnum;

/**
 * Utility class for building response URLs.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Nov-11
 */
public class UrlBuilderUtil
{    
    /**
     * Builds a login URL with an error message.
     *
     * @param redirectUri The redirect URI to include in the URL.
     * @param codeChallenge The code challenge to include in the URL.
     * @param failedMessage The error message to include in the URL.
     * @param endpoint The endpoint to include in the URL.
     * @return The constructed login URL.
     */
    public static String buildLoginUrlWithError(String redirectUri, String codeChallenge, String failedMessage, String endpoint)
    {
        final StringBuilder loginUrl = new StringBuilder(ApiPath.BASE_PATH + endpoint);
        loginUrl.append(PathEnum.ERROR_PARAM.getPath()).append(URLEncoder.encode(failedMessage, StandardCharsets.UTF_8));
        
        if (redirectUri != null && !redirectUri.isEmpty())
        {
            loginUrl.append(PathEnum.REDIRECT_URI_PARAM.getPath()).append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8));
            
            if (codeChallenge != null && !codeChallenge.isEmpty())
            {
                loginUrl.append(PathEnum.CODE_CHALLENGE_PARAM.getPath()).append(URLEncoder.encode(codeChallenge, StandardCharsets.UTF_8));
            }
        }
        
        return loginUrl.toString();
    }

    /**
     * Builds a login URL with an error message indicating an invalid OTP.
     *
     * @param ssoToken The client ID to include in the URL.
     * @param redirectUri The redirect URI to include in the URL.
     * @param codeChallenge The code challenge to include in the URL.
     * @param endpoint The endpoint to include in the URL.
     * @return The constructed login URL with the error message.
     */
    public static String buildWrongOtpError(String ssoToken, String redirectUri, String codeChallenge, String endpoint)
    {
        final StringBuilder loginUrl = new StringBuilder(ApiPath.BASE_PATH + endpoint);
        loginUrl.append(PathEnum.ERROR_PARAM.getPath()).append(URLEncoder.encode(ErrorDescription.INVALID_OTP_ENTERED.getMessage(), StandardCharsets.UTF_8));
        loginUrl.append(PathEnum.SSO_TOKEN_REDIRECT_URL.getPath()).append(URLEncoder.encode(ssoToken, StandardCharsets.UTF_8));
        
        if (redirectUri != null && !redirectUri.isEmpty())
        {
            loginUrl.append(PathEnum.REDIRECT_URI_PARAM.getPath()).append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8));
            
            if (codeChallenge != null && !codeChallenge.isEmpty())
            {
                loginUrl.append(PathEnum.CODE_CHALLENGE_PARAM.getPath()).append(URLEncoder.encode(codeChallenge, StandardCharsets.UTF_8));
            }
        }
        
        return loginUrl.toString();
    }

    /**
     * Builds a URL with query parameters for the given path, code challenge, and redirect URI.
     * 
     * @param path The path to build the URL for.
     * @param ssoToken The client ID to include in the URL.
     * @param code_challenge The code challenge to include in the URL.
     * @param redirect_uri The redirect URI to include in the URL.
     * @return The constructed URL.
     * @throws UnsupportedEncodingException If the URL encoding fails.
     */
    public static String buildUrl(String path, String ssoToken, String code_challenge, String redirect_uri) throws UnsupportedEncodingException
    {
        return String.format(PathEnum.SSO_TOKEN_REQUEST_URL.getPath(),
            path,
            ssoToken != null ? URLEncoder.encode(ssoToken, StandardCharsets.UTF_8.toString()) : "",
            code_challenge != null ? code_challenge : "",
            redirect_uri != null ? redirect_uri : "");
    }
}