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
import com.zentois.authbeast.enums.ErrorDescription;
import com.zentois.authbeast.enums.PathEnum;
import com.zentois.authbeast.service.ForgotPasswordService;
import com.zentois.authbeast.utils.UrlBuilderUtil;
import com.zentois.authbeast.utils.mfa.email.otp.OtpHelper;
import com.zentois.authbeast.utils.model_view.ModelViewBuilderUtil;
import com.zentois.authbeast.utils.url.UrlUtils;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


/**
 * The ForgotPasswordController class is responsible for handling the forgot password functionality of the nceph user.
 * It provides endpoints for rendering the forgot password view and sending an OTP to the user's email for password reset.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Dec-04
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiPath.BASE_PATH)
public class ForgotPasswordController
{
    private final OtpHelper otpHelper;

    private final ForgotPasswordService forgotPasswordService;

    private final EmailSenderService emailSenderService;

    private final SecurityProperties securityProperties;

    private final UrlUtils urlUtils;

    /**
     * Renders the password reset form view page.
     * This endpoint provides a form for users to initiate the password reset process
     * by entering their email address.
     *
     * @param code_challenge An optional code challenge for PKCE flow, used for enhanced security.
     * @param redirect_uri An optional URI to redirect the user after the password reset process.
     * @return A ModelAndView object containing the password reset form view.
     */
    @GetMapping(ApiPath.PASSWORD_RESET_FORM_VIEW)
    public ModelAndView handlePasswordResetRequestFormView(@RequestParam String code_challenge, @RequestParam String redirect_uri)
    {
        return ModelViewBuilderUtil.createPasswordResetFormView(redirect_uri, code_challenge);
    }

    /**
     * Sends an OTP to the user's email for password reset.
     * This endpoint is invoked after the user submits their email address in the forgot password form.
     * It generates a random ID, maps it to the user's email that called it ssoToken(credential identifier), and sends an OTP to their email.
     * The user is then redirected to the OTP verification page with the generated ssoToken.
     *
     * @param email The user's email address to send the OTP to.
     * @param code_challenge An optional code challenge for PKCE flow, used for enhanced security.
     * @param redirect_uri An optional URI to redirect the user after the password reset process.
     * @return A ResponseEntity object containing the HTTP status and redirect location.
     * @throws UnsupportedEncodingException If the encoding of the redirect URI is not supported.
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     * @throws URISyntaxException 
     * @throws MalformedURLException 
     */
    @PostMapping(ApiPath.FORGET_PASSWORD_OTP_REQUEST)
    public ResponseEntity<String> handleForgetPasswordOtpRequest(@RequestParam String email, @RequestParam String code_challenge, @RequestParam String redirect_uri, HttpSession session) throws UnsupportedEncodingException, JsonMappingException, JsonProcessingException, MalformedURLException, URISyntaxException
    {
        final URL redirectUri = urlUtils.convertStringToUrlType(redirect_uri);
        final String appName = urlUtils.extractAppName(redirectUri);
        final OtpSsoDto otpSsoDto = otpHelper.generateOtpWithSso(appName, email, session, code_challenge);

        final Context context = Context.builder()
        .emailType(EmailType.PASSWORD_RESET_EMAIL)
        .emailSender(securityProperties.getSenderEmail())
        .emailReceiver(email)
        .additionalParams(OtpParam.builder().otp(otpSsoDto.getOtp()).expiryMinutes(otpHelper.getOtpExpiryTiming(email)).build())
        .build();
        emailSenderService.sendEmail(context);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(UrlBuilderUtil.buildUrl(PathEnum.OTP_LOGIN_URL.getPath(), otpSsoDto.getSsoToken(), code_challenge, redirect_uri))).build();
    }

    /**
     * This function handles the GET request for the OTP-based password reset login view.
     * It displays a form to the user to enter the OTP received via email.
     *
     * @param ssoToken The random ID generated for the user's OTP.
     * @param code_challenge The code challenge for PKCE flow, used for enhanced security.
     * @param redirect_uri The URI to redirect the user to after successful password reset.
     * @param error An optional error message to display if any.
     * @return A ModelAndView object containing the OTP login form view.
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     */
    @GetMapping(ApiPath.FORGET_PASSWORD_OTP_FORM_VIEW)
    public ModelAndView handleForgotPasswordOtpFormView(@RequestParam String ssoToken, @RequestParam String code_challenge, @RequestParam String redirect_uri, @RequestParam(required = false) String error) throws JsonMappingException, JsonProcessingException
    {
        return ModelViewBuilderUtil.createPasswordForgotModelAndView(ssoToken, redirect_uri, code_challenge, error, (otpHelper.getOtpExpiryTiming(ssoToken) - System.currentTimeMillis()) / 1000);
    }

    /**
     * This function handles the POST request for OTP-based password reset here we validate entered OTP.
     * It validates the entered OTP, and if valid, redirects the user to the password update form view.
     * If the OTP is invalid, it redirects the user to the OTP login form view with an error message.
     * 
     * @param combinedOtp The combined OTP entered by the user.
     * @param ssoToken The random ID generated for the user's OTP.
     * @param code_challenge The code challenge for PKCE flow, used for enhanced security.
     * @param redirect_uri The URI to redirect the user to after successful password reset.
     * @return A ResponseEntity object containing the HTTP status and redirect location.
     * @throws UnsupportedEncodingException If the encoding of the redirect URI is not supported.
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     */
    @PostMapping(ApiPath.FORGOT_PASSWORD_OTP_VALIDATE)
    public ResponseEntity<String> handleOtpValidationRequest(@RequestParam String combinedOtp, @RequestParam String ssoToken, @RequestParam String code_challenge, @RequestParam String redirect_uri) throws UnsupportedEncodingException, JsonMappingException, JsonProcessingException
    {
        return otpHelper.validateOtp(ssoToken, combinedOtp)
        ? ResponseEntity.status(HttpStatus.FOUND)
        .location(
        URI.create(UrlBuilderUtil.buildUrl(PathEnum.OTP_VALIDATE_REDIRECT_URL.getPath(), ssoToken,
        URLEncoder.encode(code_challenge != null ? code_challenge : "", StandardCharsets.UTF_8.toString()),
        URLEncoder.encode(redirect_uri != null ? redirect_uri : "", StandardCharsets.UTF_8.toString()))))
        .build()
        : forgotPasswordService.handleWrongOtp(ssoToken, 
        URLEncoder.encode(redirect_uri != null ? redirect_uri : "", StandardCharsets.UTF_8.toString()), 
        URLEncoder.encode(code_challenge != null ? code_challenge : "", StandardCharsets.UTF_8.toString()));
    }

    /**
     * This function handles the password reset view after the user has entered the correct OTP.
     * It renders the new password form view page and passes the required parameters to it.
     * 
     * @param ssoToken The client identifier associated with the user.
     * @param code_challenge The code challenge for PKCE (Proof Key for Code Exchange) flow.
     * @param redirect_uri The URI to redirect the user to after successful password reset.
     * 
     * @return A ModelAndView object containing the new password form view page.
     */
    @GetMapping(ApiPath.CHANGE_PASSWORD_VIEW)
    public ModelAndView handlePasswordChangeView(@RequestParam String ssoToken, @RequestParam String code_challenge, @RequestParam String redirect_uri)
    {
        return ModelViewBuilderUtil.createPasswordView(ssoToken, redirect_uri, code_challenge);
    }

    /**
     * Handles the password update for the user who has initiated the forgot password flow.
     * 
     * @param ssoToken The client identifier associated with the user.
     * @param password The new password to be set for the user.
     * @param confirmPassword The confirmation of the new password.
     * @param code_challenge The code challenge for PKCE flow, used for enhanced security.
     * @param redirect_uri The URI to redirect the user to after successful password reset.
     * 
     * @return A ResponseEntity object containing the HTTP status and redirect location.
     * @throws UnsupportedEncodingException If the encoding of the redirect URI is not supported.
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     */
    @PostMapping(ApiPath.PASSWORD_CHANGE_REQUEST)
    public ResponseEntity<String> handlePasswordChangeRequest(@RequestParam String ssoToken, @RequestParam String password, @RequestParam String confirmPassword, @RequestParam String code_challenge, @RequestParam String redirect_uri) throws UnsupportedEncodingException, JsonMappingException, JsonProcessingException
    {
        if (otpHelper.isSsoTokenExist(ssoToken))
        {
            forgotPasswordService.forgotPassword(password, confirmPassword, otpHelper.getEmailBySso(ssoToken));
            return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(UrlBuilderUtil.buildUrl(PathEnum.LOGIN_FORM_REDIRECT_URL.getPath(), null, 
            URLEncoder.encode(redirect_uri != null ? redirect_uri : "", StandardCharsets.UTF_8.toString()),
            URLEncoder.encode(code_challenge != null ? code_challenge : "", StandardCharsets.UTF_8.toString())
            )))
            .build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDescription.INVALID_REQUEST.getMessage());
    }

    /**
     * Resends an OTP email for password reset.
     * 
     * @param ssoToken The client identifier associated with the user.
     * @param code_challenge The code challenge for PKCE (Proof Key for Code Exchange) flow.
     * @param redirect_uri The URI to redirect the user to after successfully resending the OTP.
     * @return A ResponseEntity object containing the HTTP status and redirect location.
     * @throws UnsupportedEncodingException If the encoding of the redirect URI is not supported.
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     * @throws URISyntaxException 
     * @throws MalformedURLException 
     */
    @PostMapping(ApiPath.FORGOT_PASSWORD_OTP_RESEND)
    public ResponseEntity<?> resendOtpRequest(@RequestParam String email, @RequestParam String code_challenge, @RequestParam String redirect_uri, HttpSession session) throws UnsupportedEncodingException, JsonMappingException, JsonProcessingException, MalformedURLException, URISyntaxException
    {
        if (email == null)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid SSO token."));
        }

        final URL redirectUri = urlUtils.convertStringToUrlType(redirect_uri);
        final String appName = urlUtils.extractAppName(redirectUri);
        final OtpSsoDto otpSsoDto = otpHelper.generateOtpWithSso(appName, email, session, code_challenge);
        final long newExpiryTime = otpHelper.getOtpExpiryTiming(otpSsoDto.getSsoToken());

        final Context context = Context.builder()
        .emailType(EmailType.PASSWORD_RESET_EMAIL)
        .emailReceiver(email)
        .additionalParams(OtpParam.builder().otp(otpSsoDto.getOtp()).expiryMinutes(otpHelper.getOtpExpiryTiming(email)).build())
        .emailSender(securityProperties.getSenderEmail())
        .build();
        emailSenderService.sendEmail(context);

        final ResendOtpDto response = ResendOtpDto.builder().expiryTime(((newExpiryTime - System.currentTimeMillis()) / 1000)).ssoToken(otpSsoDto.getSsoToken()).build();
        return ResponseEntity.ok(response);
    }
}