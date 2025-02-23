package com.zentois.authbeast.utils.hash;

import java.security.MessageDigest;
import java.util.Base64;

import com.zentois.authbeast.enums.ErrorDescription;

/**
 * Utility class for hashing.
 *
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Sep-25
 */
public class HashingUtil
{
    /**
     * Hashes the given code verifier using the given algorithm and returns the
     * base64 encoded hash.
     *
     * @param codeVerifier the code verifier to be hashed
     * @param algo         the algorithm to be used for hashing
     * @return the base64 encoded hash
     */
    public static String hashCodeVerifier(String codeVerifier, String algo)
    {
        try
        {
            // Creates a cryptographic hash function instance (one-way hash function)
            // The resulting digest object can then be used to generate hash values through its methods like digest(), 
            //which is what this utility class uses to hash the code verifier for secure OAuth/PKCE flows.
            final MessageDigest digest = MessageDigest.getInstance(algo);
            final byte[] hash = digest.digest(codeVerifier.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        }
        catch (Exception e)
        {
            throw new RuntimeException(ErrorDescription.ERROR_CODE_VERIFIER.getMessage(), e);
        }
    }
}