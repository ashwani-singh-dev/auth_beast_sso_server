package com.zentois.authbeast.controller;

import java.text.ParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zentois.authbeast.constants.ApiPath;
import com.zentois.authbeast.dto.request.RefreshApplicationTokenDTO;
import com.zentois.authbeast.dto.token.TokenResponseDTO;
import com.zentois.authbeast.service.RefreshTokenService;
import com.nimbusds.jose.JOSEException;

import lombok.RequiredArgsConstructor;

/**
 * Controller class responsible for handling refresh token related operations.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Nov-14
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiPath.BASE_PATH)
public class RefreshTokenController
{
    private final RefreshTokenService refreshTokenService;

    /**
     * This endpoint is used to refersh the access token and get new refresh and access token.
     * 
     * @param authorization
     * @throws JsonProcessingException
     * @return TokenResponseDTO
     * @throws ParseException
     * @throws JOSEException
     */
    @PostMapping(ApiPath.REFRESH_TOKEN_REQUEST)
    public ResponseEntity<TokenResponseDTO> handleRefreshToken(@RequestBody RefreshApplicationTokenDTO refreshTokenDTO) throws JsonProcessingException, JOSEException, ParseException
    {
        return ResponseEntity.ok(refreshTokenService.refreshToken(refreshTokenDTO));
    }
}