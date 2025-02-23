package com.zentois.authbeast.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.zentois.authbeast.enums.Endpoint;
import com.zentois.authbeast.enums.cors.AllowedApplicationUrl;
import com.zentois.authbeast.enums.cors.AllowedHeaders;
import com.zentois.authbeast.enums.cors.AllowedMethods;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for security-related settings.
 *
 * @author Ashwani Singh
 * @version 1.1
 * @since 2024-July-31
 */
@Configuration
@Getter
@PropertySource("classpath:security.properties")
@ConfigurationProperties(prefix = "security")
@Setter
public class SecurityProperties
{
    private long accessTokenExpiration;

    private long refreshTokenExpiration;

    private String senderEmail;

    /**
     * List of allowed origins for CORS.
     * 
     * @return Array String
     */
    public List<String> getAllowedOrigins()
    {
        return Arrays.stream(AllowedApplicationUrl.values())
            .map(AllowedApplicationUrl::getUrl)
            .collect(Collectors.toList());
    }

    /**
     * List of all allwed methods for CORS.
     * 
     * @return Array String
     */
    public List<String> getAllowedMethods()
    {
        return Arrays.stream(AllowedMethods.values())
        .map(AllowedMethods::getMethod)
        .collect(Collectors.toList());
    }

    /**
     * List of all allowed headers for CORS.
     * 
     * @return Array String
     */
    public List<String> getAllowedHeaders()
    {
        return Arrays.stream(AllowedHeaders.values())
        .map(AllowedHeaders::getValue)
        .collect(Collectors.toList());
    }

    /**
     * Get all public endpoints.
     * 
     * @return Array String
     */
    public String[] getPublicEndpoints()
    {
        return Endpoint.stream()
                .filter(e -> !e.isRequiresAuth())
                .map(Endpoint::getPath)
                .toArray(String[]::new);
    }

    /**
     * Get all protected endpoints.
     * 
     * @return Array String
     */
    public String[] getProtectedEndpoints()
    {
        return Stream.of(Endpoint.values())
                .filter(Endpoint::isRequiresAuth)
                .map(Endpoint::getPath)
                .toArray(String[]::new);
    }
}