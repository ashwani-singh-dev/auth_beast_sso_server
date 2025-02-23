package com.zentois.authbeast.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

/**
 * This class configures Cross-Origin Resource Sharing (CORS) settings for the Spring Boot application.
 * It creates a {@link CorsConfigurationSource} bean to handle CORS requests.
 *
 * <p>The CORS configuration is based on the properties defined in the {@link SecurityProperties} class.
 * The allowed origins, methods, and headers are set accordingly.
 *
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-July-31
 */
@Configuration
@RequiredArgsConstructor
public class CorsConfig
{
    private final SecurityProperties securityProperties;

    /**
     * This method configures and returns a CorsConfigurationSource bean for handling Cross-Origin Resource Sharing (CORS) 
     * requests in the Spring Boot application.
     *
     * @return a CorsConfigurationSource bean for handling CORS requests
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource()
    {
        // Create a new CorsConfiguration instance
        final CorsConfiguration config = new CorsConfiguration();

        // Allow credentials in the CORS response. Determines whether user credentials are supported
        config.setAllowCredentials(true);

        // Set the list of allowed origins
        config.setAllowedOrigins(securityProperties.getAllowedOrigins());

        // Set the list of allowed HTTP methods
        config.setAllowedMethods(securityProperties.getAllowedMethods());

        // Set the list of allowed headers
        config.setAllowedHeaders(securityProperties.getAllowedHeaders());

        // Create a new UrlBasedCorsConfigurationSource instance
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Register the CorsConfiguration for the specified URL pattern
        source.registerCorsConfiguration("/**", config);

        // Return the CorsConfigurationSource bean
        return source;
    }
}