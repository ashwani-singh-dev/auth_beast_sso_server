package com.zentois.authbeast.utils.cache;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.zentois.authbeast.dto.session_data.UserAccount;
import com.zentois.authbeast.dto.token.SsoTokenData;
import com.zentois.authbeast.enums.allowed_origin.AllowedOriginMeta;
import com.zentois.authbeast.model.SessionDataDTO;
import com.zentois.util.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * The `SsoCacheManager` class is a Spring-managed component that provides caching functionality for SSO (Single Sign-On) token data.
 * It uses Redis to store and retrieve SSO token data (ssoToken is helping in generatin access token), and provides methods to manage the cache.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Oct-04
 */
@RequiredArgsConstructor
@Component
public class SsoCacheManager
{    
    private final RedisTemplate<String, String> redisTemplate;
    
    /**
     * Retrieves the SSO token data from the cache based on the provided SSO token.
     *
     * @param ssoToken the SSO token to retrieve the token data for
     * @return the SSO token data associated with the provided token, or null if not found
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     */
    public SsoTokenData getTokenData(String ssoToken) throws JsonMappingException, JsonProcessingException
    {
        return ObjectMapper.GENERIC_MAPPER.readValue(redisTemplate.opsForValue().get(ssoToken), SsoTokenData.class);
    }
    
    /**
     * Removes the SSO token data from the cache.
     *
     * @param ssoToken the SSO token to remove from the cache
     */
    public void removeToken(String ssoToken)
    {
        redisTemplate.delete(ssoToken);
    }
    
    /**
     * Checks if the SSO token is present in the cache.
     *
     * @param ssoToken the SSO token to check for in the cache
     * @return true if the SSO token is present in the cache, false otherwise
     */
    public boolean hasToken(String ssoToken)
    {
        return redisTemplate.hasKey(ssoToken);
    }
    
    /**
     * Clears the entire Redis cache.
     *
     * This method flushes the entire database in Redis, effectively removing all keys and their associated data.
     * Use with caution as this operation cannot be undone and will delete all cached data.
     */
    public void clearCache()
    {
        final RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        if (connectionFactory != null)
        {
            final RedisConnection connection = connectionFactory.getConnection();
            connection.serverCommands().flushAll();
        }
    }

    /**
     * Caches the given user account data and SSO token as intermediate steps of the SSO flow.
     * 
     * This method is used to store user account data and SSO token in Redis as intermediate steps of the SSO
     * flow. The data is cached against the SSO token which is used to retrieve the cached data later.
     * 
     * @param appName        The name of the application to cache the user account data for.
     * @param sessionId      The session ID of the user to cache the user account data for.
     * @param userAccount    The user account data to cache.
     * @param ssoToken       The SSO token to cache the user account data against.
     * @param code_challenge The code challenge to cache.
     * @param browserSessionId The browser session ID to cache.
     * @throws JsonProcessingException 
     */
    public void fillSsoTokenCache(String appName, String sessionId, UserAccount userAccount, String ssoToken, String code_challenge, String browserSessionId) throws JsonProcessingException
    {
        cacheIntermediateToken(appName, sessionId, userAccount, ssoToken, code_challenge, browserSessionId);
    }

    private void cacheIntermediateToken(String appName, String sessionId, UserAccount userAccount, String ssoToken, String codeChallenge, String browserSessionId) throws JsonProcessingException
    {
        final SsoTokenData tokenData = SsoTokenData.builder()
                .appName(AllowedOriginMeta.valueOf(appName).getAppName())
                .origin(AllowedOriginMeta.valueOf(appName).getUrl())
                .sessionData(SessionDataDTO.builder()
                        .email(userAccount.getEmail())
                        .build())
                .codeChallenge(codeChallenge)
                .browserSessionId(browserSessionId)
                .build();
        redisTemplate.opsForValue().set(ssoToken, ObjectMapper.GENERIC_MAPPER.writeValueAsString(tokenData));
    }
}