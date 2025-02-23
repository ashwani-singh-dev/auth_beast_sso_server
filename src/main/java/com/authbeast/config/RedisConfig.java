package com.zentois.authbeast.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configures the Redis-based session management for the SSO server.
 * This class implements the BeanClassLoaderAware interface to provide the
 * class loader to the ObjectMapper used for serializing session data.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Nov-19
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 2592000)
@RedisHash
@EnableRedisRepositories
public class RedisConfig
{
    /**
     * Configures the default Redis serializer for Spring Session using a GenericJackson2JsonRedisSerializer.
     * This serializer is used to serialize session objects to JSON format using the provided ObjectMapper.
     *
     * @param objectMapper The ObjectMapper used for JSON serialization.
     * @return A RedisSerializer for serializing session objects to JSON.
     */
    @Bean
    RedisSerializer<Object> springSessionDefaultRedisSerializer(ObjectMapper objectMapper)
    {
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    /**
     * Returns the default ObjectMapper used for JSON serialization and deserialization.
     * This ObjectMapper is used by the GenericJackson2JsonRedisSerializer to serialize and deserialize session objects.
     * 
     * @return The default ObjectMapper for JSON serialization and deserialization.
     */
    @Bean
    ObjectMapper objectMapper()
    {
        return new ObjectMapper();
    }
}