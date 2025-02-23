package com.zentois.authbeast.controller;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.zentois.framework.email.dto.Context;
import com.zentois.framework.email.enums.EmailType;
import com.zentois.framework.email.service.EmailSenderService;
import com.zentois.authbeast.config.SecurityProperties;
import com.zentois.authbeast.constants.ApiPath;
import com.zentois.authbeast.dto.context.OtpParam;
import com.zentois.authbeast.dto.otp.OtpSsoDto;
import com.zentois.authbeast.dto.otp.ResendOtpDto;
import com.zentois.authbeast.dto.response.ApiResponse;
import com.zentois.authbeast.dto.session_data.SessionUserData;
import com.zentois.authbeast.enums.ErrorDescription;
import com.zentois.authbeast.enums.PathEnum;
import com.zentois.authbeast.service.AccountService;
import com.zentois.authbeast.service.SigninService;
import com.zentois.authbeast.utils.mfa.email.otp.OtpHelper;
import com.zentois.authbeast.utils.model_view.ModelViewBuilderUtil;
import com.zentois.authbeast.utils.session.SessionDataUtil;
import com.zentois.authbeast.utils.uid.UidGeneratorUtil;
import com.zentois.authbeast.utils.url.RedirectValidatorUtil;
import com.zentois.authbeast.utils.url.UrlUtils;

import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * The `SigninController` class is a Spring REST controller that handles
 * authentication-related endpoints for the SSO (Single Sign-On) system.
 * It provides functionality for handling email-based OTP (One-Time Password)
 * login.
 * The controller is responsible for managing user sessions, validating user
 * input, and interacting with various services to facilitate the authentication
 * process.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Nov-15
 */
@RequiredArgsConstructor
@RequestMapping(ApiPath.BASE_PATH)
@RestController
public class SigninController
{
    private final SigninService signinService;

    private final AccountService accountService;

    private final SessionDataUtil sessionDataUtil;

    private final EmailSenderService emailSenderService;

    private final SecurityProperties securityProperties;

    private final RedirectValidatorUtil redirectValidatorUtil;

    private final OtpHelper otpHelper;

    private final UrlUtils urlUtils;

    /**
     * This function handles the email OTP login for multi-factor authentication.
     * It displays a form to the user to enter the OTP received via email.
     *
     * @param email          The email address of the user attempting to login.
     * @param codeChallenge The code challenge for PKCE (Proof Key for Code
     *                       Exchange) flow.
     * @param redirectUri   The URI to redirect the user to after successful login.
     * @param session        The HTTP session for maintaining user state.
     * @return A ModelAndView object containing the OTP login form.
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    @GetMapping(ApiPath.OTP_LOGIN_VIEW)
    public ModelAndView mfaOtpFormView(@RequestParam String ssoToken, @RequestParam(name = "code_challenge") String codeChallenge, @RequestParam(name = "redirect_uri") String redirectUri, @RequestParam(required = false) String error, HttpSession session) throws JsonMappingException, JsonProcessingException
    {
        final ModelAndView modelAndView = ModelViewBuilderUtil.createOtpMfaModelAndView(ssoToken, otpHelper.getEmailBySso(ssoToken), redirectUri, codeChallenge, error, (otpHelper.getOtpExpiryTiming(ssoToken) - System.currentTimeMillis()) / 1000);
        if (error != null)
        {
            modelAndView.addObject("error", error);
        }
        return modelAndView;
    }

    /**
     * This function handles the POST request for email OTP login during
     * multi-factor authentication.
     * It validates the entered OTP, creates a session, and redirects the user to
     * the appropriate destination.
     *
     * @param combinedOtp    The combined OTP entered by the user.
     * @param ssoToken            The email address of the user attempting to login.
     * @param codeChallenge The code challenge for PKCE (Proof Key for Code
     *                       Exchange) flow.
     * @param redirectUri   The URI to redirect the user to after successful login.
     * @param session        The HTTP session for maintaining user state.
     *
     * @return A ResponseEntity object containing the HTTP status and redirect
     *         location.
     * @throws JsonProcessingException
     * @throws JsonMappingException
     * @throws URISyntaxException 
     * @throws MalformedURLException 
     */
    @PostMapping(ApiPath.MFA_OTP_VALIDATE)
    public ResponseEntity<String> handleMfaOtpRequest(@RequestParam String combinedOtp, @RequestParam String email, @RequestParam String ssoToken, @RequestParam(name = "code_challenge") String codeChallenge, @RequestParam(name = "redirect_uri") String redirectUri, HttpSession session) throws JsonMappingException, JsonProcessingException, MalformedURLException, URISyntaxException
    {
        if (!otpHelper.validateOtp(ssoToken, combinedOtp))
        {
            return signinService.handleWrongOtpLogin(ssoToken, redirectUri, codeChallenge);
        }

        return handleLoginWithoutOtp(otpHelper.getEmailBySso(ssoToken), codeChallenge, redirectUri, session, ssoToken, null);
    }

    /**
     * Handles the login process for single sign-on (SSO) using email and password.
     * They are not using MFA.
     * IF you are using MFA verification they you should use handleLoginWithOtp
     * method on /sso/signin post endpoint.
     * 
     * @param email
     * @param code_challenge
     * @param redirect_uri
     * @param session
     * @return ResponseEntity<String>`
     * @throws JsonProcessingException
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    private ResponseEntity<String> handleLoginWithoutOtp(String email, String codeChallenge, String redirectUri, HttpSession session, String ssoToken, String entityCode) throws JsonProcessingException, MalformedURLException, URISyntaxException
    {
        final URL url = urlUtils.convertStringToUrlType(redirectUri);
        final String appName = urlUtils.extractAppName(url);

        final SessionUserData sessionUserData = sessionDataUtil.getOrCreateNewSessionUserData(session);
        final String sessionId = signinService.handleSessionCreation(email, session, sessionUserData);
        sessionUserData.getUsers().get(sessionId).setExpiryStatus(false);

        if (ssoToken == null)
        {
            ssoToken = UidGeneratorUtil.generateSessionId();
            signinService.updateAppSessionInRedis(appName, email, session, codeChallenge, ssoToken, email, null);
        }

        return StringUtils.isBlank(redirectUri)
        ? signinService.handleLocalLogin(session, sessionUserData)
        : signinService.handleRedirectLogin(redirectUri, sessionId, session, sessionUserData, ssoToken, entityCode);
    }
}