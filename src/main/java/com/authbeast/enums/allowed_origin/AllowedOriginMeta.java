package com.zentois.authbeast.enums.allowed_origin;

import com.zentois.authbeast.enums.cors.AllowedApplicationUrl;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing the allowed origin metadata for the SSO server.
 * Each enum constant represents a specific application that is allowed to
 * authenticate with the SSO server, along with its associated URL, callback
 * path, application name, whether the origin is allowed, and a token issuer
 * signing key.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Nov-18
 */
@AllArgsConstructor
@Getter
public enum AllowedOriginMeta
{
        CARTXFLIP(AllowedApplicationUrl.CARTXFLIP.getUrl(), "/auth/cartexflip", "Cartxflip", true, "l1Q7zkOL59cRqWBkQ12ZiGVW2DBL"),

        private final String url;
        private final String callback;
        private final String appName;
        private final boolean originAllowed;
        private final String tokenIssuerSign;
}