package com.zentois.authbeast.controller;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.zentois.authbeast.constants.ApiPath;
import com.zentois.authbeast.service.SignoutService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * This Controller class responsible for handling sign-out functionality for the SSO session management system.
 * 
 * @author Ashwani Singh
 * @version  1.0
 * @since 2024-Oct-25
 */
@RestController
@RequestMapping(ApiPath.BASE_PATH)
@RequiredArgsConstructor
public class SignoutController
{
    private final SignoutService signoutService;

    private static final String DEFAULT_REDIRECT = "/";

    /**
     * Handle the httpsession for signout from specific application
     * 
     * @param redirect_uri
     * @param sessionId
     * @param request
     * @return void
     * @throws JsonMappingException
     * @throws JsonProcessingException
     * @throws MalformedURLException
     * @throws URISyntaxException 
     */
    @GetMapping(ApiPath.SPECIFIC_APPLICATION_SIGNOUT)
    public ResponseEntity<Void> handlePartialLogout(@RequestParam(required = false) String redirect_uri, @RequestParam(required = false) String sessionId, HttpServletRequest request) throws JsonMappingException, JsonProcessingException, MalformedURLException, URISyntaxException
    {
        final HttpSession session = request.getSession(false);
        
        if (session != null)
        {
            signoutService.partialLogoutFromRedis(session, sessionId, redirect_uri);
        }

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create(redirect_uri != null ? redirect_uri : DEFAULT_REDIRECT))
            .build();
    }

    /**
     * NOTE: This method is not used in current implementation....
     * 
     * Handle the httpsession for signout from all application and also remove session
     * 
     * @param redirect_uri
     * @param sessionId
     * @param request
     * @return void
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @GetMapping(ApiPath.SINGLE_SIGNOUT)
    public ResponseEntity<Void> handleAllUserLogout(@RequestParam(required = false) String redirect_uri, @RequestParam(required = false) String sessionId, HttpServletRequest request) throws JsonMappingException, JsonProcessingException
    {
        final HttpSession session = request.getSession(false);
        
        if (session != null)
        {
            signoutService.handleSessionLogout(session, sessionId);
        }

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create(redirect_uri != null ? redirect_uri : DEFAULT_REDIRECT))
            .build();
    }
}