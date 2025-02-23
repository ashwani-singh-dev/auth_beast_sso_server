package com.zentois.authbeast.utils.cache;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zentois.authbeast.dto.session_data.SessionUserData;
import com.zentois.authbeast.dto.session_data.TokenDataList;
import com.zentois.authbeast.dto.session_data.UserAccount;
import com.zentois.authbeast.enums.ErrorDescription;
import com.zentois.authbeast.enums.redis.HashKeyValue;
import com.zentois.util.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * Utility class for managing user session data, specifically for handling user applications session.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Nov-24
 */
@RequiredArgsConstructor
@Component
public class UserAppManager
{
    /**
     * Updates the user's session data with the provided origin by adding a new entry for the origin if it does not already exist.
     * This method is used to keep track of the user's active sessions across different applications.
     * 
     * @param sessionId The session ID for the user attempting to login.
     * @param appName    The appName of the application that the user is attempting to access.
     * @param sessionUserData The session data for the user.
     */
    public void updateUserApps(String sessionId, String appName, SessionUserData sessionUserData)
    {
        final UserAccount userAccount = sessionUserData.getUsers().get(sessionId);
        if (userAccount != null)
        {
            if (userAccount.getApps().get(appName) == null)
            {
                userAccount.getApps().put(appName, new TokenDataList());
            }
        }
    }

    /**
     * Updates the Redis cache with the user's logged in applications.
     * 
     * This method retrieves the user's app info from Redis, deserializes it into a list of strings, and checks if the given origin is already
     * in the list. If not, the origin is added to the list, and the list is then serialized and stored back in Redis. The Redis key is set to
     * expire after one day.
     * 
     * @param session The HttpSession instance for the user.
     * @param sessionId The session ID for the user attempting to login.
     * @param origin The origin of the application that the user is attempting to access.
     * @param redisTemplate The RedisTemplate instance for interacting with Redis.
     * 
     * @throws RuntimeException If an error occurs while processing the session data.
     */
    public void updateRedisCache(HttpSession session, String sessionId, String origin, RedisTemplate<String, String> redisTemplate)
    {
        try
        {
            final String userAppInfo = (String) redisTemplate.opsForHash().get(session.getId() + ":" + sessionId, HashKeyValue.USER_APP_INFO.getKey());
            
            final List<String> userLoggedInApps = getUserLoggedInApps(userAppInfo);
            
            if (!userLoggedInApps.contains(origin))
            {
                userLoggedInApps.add(origin);
            }
            
            final String updatedUserAppInfo = ObjectMapper.GENERIC_MAPPER.writeValueAsString(userLoggedInApps);
            redisTemplate.opsForHash().put(session.getId() + ":" + sessionId, HashKeyValue.USER_APP_INFO.getKey(), updatedUserAppInfo);
            redisTemplate.expire(session.getId() + ":" + sessionId, Duration.ofDays(1));
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(ErrorDescription.REDIS_CACHE_FAILED.getMessage(), e);
        }
    }

    /**
     * Deserialize the given user app info string into a list of strings representing the origins of the applications the user is logged in to.
     * If the given user app info string is null, return an empty list.
     * 
     * @param userAppInfo The user app info string to deserialize.
     * @param objectMapper The ObjectMapper instance for deserializing the user app info string.
     * 
     * @return A list of strings representing the origins of the applications the user is logged in to.
     * 
     * @throws JsonProcessingException If an error occurs while deserializing the user app info string.
     */
    public List<String> getUserLoggedInApps(String userAppInfo) throws JsonProcessingException
    {
        if (userAppInfo == null)
        {
            return new ArrayList<>();
        }
        return ObjectMapper.GENERIC_MAPPER.readValue(userAppInfo, new TypeReference<List<String>>() {});
    }
}