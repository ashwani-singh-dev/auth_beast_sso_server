package com.zentois.authbeast.utils.mfa.email.otp;

import java.security.SecureRandom;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.zentois.authbeast.dto.otp.OtpData;
import com.zentois.authbeast.dto.otp.OtpSsoDto;
import com.zentois.authbeast.dto.token.SsoTokenData;
import com.zentois.authbeast.service.SigninService;
import com.zentois.authbeast.utils.uid.UidGeneratorUtil;
import com.zentois.util.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * Utility class for generating and validating one-time passwords (OTPs) for multi-factor authentication.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Nov-11
 */
@RequiredArgsConstructor
@Component
public class OtpHelper
{
    private static final int OTP_LENGTH = 6;
    
    private static final long OTP_VALID_DURATION = 60 * 1000; // 1 minutes
    
    private final RedisTemplate<String, String> redisTemplate;

    private final SigninService signinService;
    
    public OtpSsoDto generateOtpWithSso(String appName, String email, HttpSession session, String code_challenge) throws JsonProcessingException
    {
        final String otp = generateRandomOtp();
        final long expiryTime = System.currentTimeMillis() + OTP_VALID_DURATION;
        final OtpData otpData = new OtpData(otp, expiryTime);

        final String ssoToken = UidGeneratorUtil.generateSessionId();
        signinService.updateAppSessionInRedis(appName, email, session, code_challenge, ssoToken, email, otpData);

        return OtpSsoDto.builder().ssoToken(ssoToken).otp(otp).build();
    }
    
    /**
     * Generates a random one-time password (OTP) of length 6.
     * The generated OTP is a combination of 6 random digits.
     *
     * @return The generated OTP as a string.
     */
    private String generateRandomOtp()
    {
        final SecureRandom random = new SecureRandom();
        final StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++)
        {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    /**
     * Retrieves the expiry time of the one-time password (OTP) associated with the given key.
     * The expiry time is stored in Redis when the OTP is generated.
     *
     * @param key The unique identifier for which the OTP expiry time is being retrieved.
     * @return The expiry time of the OTP in milliseconds since epoch.
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     */
    public long getOtpExpiryTiming(String key) throws JsonMappingException, JsonProcessingException
    {
        final String otpDataString = redisTemplate.opsForValue().get(key);
        if (otpDataString == null)
        {
            return 0;
        }
        final SsoTokenData ssoTokenData = ObjectMapper.GENERIC_MAPPER.readValue(otpDataString, SsoTokenData.class);
        return ssoTokenData != null ? ssoTokenData.getOtpData().getExpiryTime() : 0;
    }
    
    /**
     * Validates the given one-time password (OTP) against the one stored in Redis
     * associated with the given key.
     * 
     * @param key The unique identifier for which the OTP is being validated.
     * @param otpToValidate The OTP to be validated.
     * @return {@code true} if the OTP is valid and not expired, {@code false} otherwise.
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     */
    public boolean validateOtp(String key, String otpToValidate) throws JsonMappingException, JsonProcessingException
    {
        final String otpDataString = redisTemplate.opsForValue().get(key);
        if (otpDataString == null)
        {
            return false;
        }
        final SsoTokenData ssoTokenData = ObjectMapper.GENERIC_MAPPER.readValue(otpDataString, SsoTokenData.class);
        final boolean isOtpValid = ssoTokenData.getOtpData().getOtp().equals(otpToValidate);
        final boolean isOtpExpired = System.currentTimeMillis() > ssoTokenData.getOtpData().getExpiryTime();

        if (isOtpValid && !isOtpExpired)
        {
            ssoTokenData.setOtpData(null);
            return true;
        }

        return false;
    }

    /**
     * Retrieves the email associated with the given SSO token from the Redis cache.
     * 
     * @param ssoToken The SSO token to retrieve the email for.
     * @return The email associated with the given SSO token, or null if no such token exists in the cache.
     * @throws JsonMappingException If an error occurs while mapping the cached data to a Java object.
     * @throws JsonProcessingException If an error occurs while processing the cached data.
     */
    public String getEmailBySso(String ssoToken) throws JsonMappingException, JsonProcessingException
    {
        final String otpDataString = redisTemplate.opsForValue().get(ssoToken);
        if(otpDataString == null)
        {
            return null;
        }
        return otpDataString == null ? null : ObjectMapper.GENERIC_MAPPER.readValue(otpDataString, SsoTokenData.class).getSessionData().getEmail();
    }

    /**
     * Checks if an SSO token exists in the Redis cache.
     *
     * @param ssoToken The SSO token to check for existence.
     * @return {@code true} if the SSO token exists in the cache, {@code false} otherwise.
     */
    public Boolean isSsoTokenExist(String ssoToken)
    {
        return redisTemplate.hasKey(ssoToken);
    }
}