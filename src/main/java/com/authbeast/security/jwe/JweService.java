package com.zentois.authbeast.security.jwe;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.zentois.framework.security.jwe.JweUtil;
import com.zentois.authbeast.config.SecurityProperties;
import com.zentois.authbeast.dto.StandardJwtClaims;
import com.zentois.authbeast.enums.token.TokenType;
import com.zentois.authbeast.model.JwtPayloadData;
import com.zentois.authbeast.model.nceph_entity.Account;
import com.zentois.authbeast.repository.AccountRepository;
import com.zentois.authbeast.repository.EntityRolePrivilegeMapRepository;
import com.zentois.authbeast.repository.ProductPrivilegeMapRepository;
import com.zentois.authbeast.repository.ProductRepository;
import com.zentois.authbeast.security.rsa.RsaService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * The JweManager class is responsible for managing the encryption and decryption of JSON Web Encryption (JWE) tokens.
 * It handles the loading and usage of RSA public and private keys for the encryption and decryption operations.
 * 
 * Token generation process : 
 * generateToken -> doGenerateToken -> encryptToken

 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Dec-09
 */
@Component
@RequiredArgsConstructor
public class JweService
{
    private final AccountRepository accountRepository;

    private final ProductRepository productRepository;

    private final SecurityProperties securityProperties;

    private final EntityRolePrivilegeMapRepository entityRolePrivilegeMapRepository;

    private final ProductPrivilegeMapRepository productPrivilegeMapRepository;

    private final JweUtil jweUtil;

    private final RsaService rsaUtil;
    
    private RSAPublicKey publicKey;

    private RSAPrivateKey privateKey;

    /**
     * Initializes the JWEManager by loading the RSA key pair from the file specified
     * by the "jwt.keypair.file" property.
     * 
     * This method is annotated with Spring's @PostConstruct annotation, which means
     * it will be called after the bean has been constructed.
     * @throws InvalidKeySpecException 
     * @throws NoSuchAlgorithmException 
     */
    @PostConstruct
    public void init() throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        this.publicKey = (RSAPublicKey) rsaUtil.loadKeyPairFromFile().getPublic();
        this.privateKey = (RSAPrivateKey) rsaUtil.loadKeyPairFromFile().getPrivate();
    }

    /**
     * Retrieves the claims from a JWE token by validating and decrypting it.
     *
     * <p>This method uses the private key to decrypt the provided token and
     * returns the claims contained within the token as a {@link JWTClaimsSet} object.
     * 
     * @param token The JWE token to be validated and decrypted.
     * @return The claims contained within the token as a {@link JWTClaimsSet}.
     * @throws JOSEException If an error occurs during the decryption process.
     * @throws ParseException If an error occurs while parsing the token.
     */
    public JWTClaimsSet getClaimsFromToken(String token) throws JOSEException, ParseException
    {
        return jweUtil.validateAndDecryptToken(token, privateKey);
    }

    /**
     * Retrieves the session ID from a JWE token by validating and decrypting it.
     * 
     * <p>This method uses the private key to decrypt the provided token and
     * returns the session ID contained within the token as a string.
     * 
     * @param token The JWE token to be validated and decrypted.
     * @return The session ID contained within the token as a string.
     * @throws JOSEException If an error occurs during the decryption process.
     * @throws ParseException If an error occurs while parsing the token.
     */
    public String getSessionId(String token) throws JOSEException, ParseException
    {
        return jweUtil.extractClaimsIfExpired(token, privateKey).getClaim("sessionID").toString();
    }

    /**
     * Builds the claims for an access token, including the user's entity ID, username, email, user ID and session ID.
     * Additionally, adds the list of privileges associated with the given appName, if the user has the privilege.
     * 
     * @param account The account for which to build the claims.
     * @param payloadData The payload data containing the user's email and session ID.
     * @param appName The name of the app to retrieve the privileges for.
     * @return A map of claims for the access token.
     */
    private StandardJwtClaims buildAccessTokenClaims(Account account, JwtPayloadData payloadData, String appName)
    {
        final List<String> privileges = productPrivilegeMapRepository.getPrivilegeBasedOnProduct(appName);
    
        final List<String> privilege = privileges.contains(productRepository.findByName(appName).getPrivilege().getName()) ? privileges : List.of();
        final List<String> accountPrivilegeList = entityRolePrivilegeMapRepository.getPrivilegeNamesByEntityRoleMapId(account.getEntityRoleId(), false);
        
        return StandardJwtClaims.builder()
            .entityId(account.getEntity().getId())
            .username(account.getUsername())
            .email(payloadData.getEmail())
            .userId(account.getId())
            .sessionId(payloadData.getSessionID())
            .tokenType(TokenType.ACCESS)
            .privileges(privilege.stream().filter(accountPrivilegeList::contains).collect(Collectors.toList()))
            .build();
    }

    /**
     * Generates an access token with the provided claims and expiration time.
     * 
     * @param payloadData The payload data containing the user's email and session ID.
     * @param issuer The issuer of the token.
     * @param appName The name of the app to add the privileges for.
     * @return The generated access token.
     * @throws JOSEException If there is an error encrypting the token.
     */
    public String generateToken(JwtPayloadData payloadData, String issuer, String appName) throws JOSEException
    {
        final StandardJwtClaims claims = buildAccessTokenClaims(accountRepository.findByEmail(payloadData.getEmail()).get(), payloadData, appName);
        return jweUtil.doGenerate(claims.toMap(), issuer, publicKey, securityProperties.getAccessTokenExpiration());
    }

    /**
     * Generates a refresh token with the provided claims and expiration time.
     * 
     * @param payloadData
     * @param issuer
     * @return String
     * @throws JOSEException If there is an error encrypting the token.
     */
    public String generateRefreshToken(JwtPayloadData payloadData, String issuer) throws JOSEException
    {
        final Map<String, Object> claims = new HashMap<>();
        claims.put("email", payloadData.getEmail());
        claims.put("uID", payloadData.getUid());
        claims.put("sessionID", payloadData.getSessionID());
        claims.put("bSID", payloadData.getBSID());
        claims.put("tokenType", TokenType.REFRESH);
        return jweUtil.doGenerate(claims, issuer, publicKey, securityProperties.getRefreshTokenExpiration());
    }

    /**
     * Checks if the given token is expired.
     * 
     * @param token The JSON Web Token to check.
     * @return {@code true} if the token is expired; {@code false} otherwise.
     * @throws JOSEException 
     */  
    public boolean isTokenExpired(String token) throws JOSEException
    {
        return jweUtil.isExpired(token, privateKey);
    }
}