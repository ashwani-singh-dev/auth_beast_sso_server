package com.zentois.authbeast.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.zentois.authbeast.config.provider.CustomAuthenticationProvider;
import com.zentois.authbeast.model.AuditorAwareImpl;
import com.zentois.authbeast.security.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * This class configures Spring Security for the NCEPH User Registration application.
 * It integrates with Redis session management to prevent duplicate session creation.
 *
 * @author Ashwani Singh
 * @version 1.1
 * @since 2024-July-10
 */
// @EnableMethodSecurity(securedEnabled = true) // In this implementation we are not having any use case yet....
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig
{
    private final CorsConfigurationSource corsConfigurationSource;

    private final SecurityProperties securityProperties;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final BeanFactory beanFactory;

    @Bean
	BCryptPasswordEncoder bCryptPasswordEncoder()
	{
		return new BCryptPasswordEncoder();
	}

    /**
	 * Returns an instance of the AuditorAware interface, which is used to provide the current auditor's username.
	 *
	 * @return an instance of the AuditorAware interface
	 */
	@Bean
	AuditorAware<String> auditorAware()
	{
		return new AuditorAwareImpl();
	}

    /**
     * Configures the security filter chain for the application.
     * 
     * <p>This method sets up various security configurations including custom authentication provider,
     * CSRF protection, CORS configuration, session management, and security headers. Additionally, it
     * specifies authorization rules for public and protected endpoints and adds a JWT authentication 
     * filter to the security filter chain.</p>
     *
     * <p>CSRF protection is disabled, and session management is limited to a single session per user. 
     * Frame options and XSS protection are configured to enhance security. The method also allows
     * customization of Content Security Policy (CSP) headers.</p>
     *
     * @param http the {@link HttpSecurity} to modify with security configurations
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs while configuring the security filter chain
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http
            .authenticationProvider(beanFactory.getBean(CustomAuthenticationProvider.class))
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .sessionManagement(session -> {
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1);
            })
            .headers(headers -> headers
            // Prevents the application from being embedded in iframes/frames
            // This protects against clickjacking attacks
            .frameOptions(frameOptions -> frameOptions.deny())
        
            // XSS protection is disabled since modern browsers have built-in XSS filtering
            // Modern applications typically rely on CSP and proper output encoding instead
            .xssProtection(xss -> xss.disable())
        
            // Content Security Policy (CSP) header configuration
            // Allow resources from 'self' and specific external domains
            // .contentSecurityPolicy(csp -> csp
            //     .policyDirectives("default-src 'self'; " +
            //                       "script-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com; " +
            //                       "style-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com https://fonts.googleapis.com; " +
            //                       "font-src 'self' https://cdnjs.cloudflare.com https://fonts.gstatic.com; " +
            //                       "img-src 'self' data:; " +
            //                       "connect-src 'self'")
            // )
        )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(securityProperties.getPublicEndpoints()).permitAll()
                .requestMatchers(securityProperties.getProtectedEndpoints()).authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}