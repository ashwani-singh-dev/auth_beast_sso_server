package com.zentois.authbeast.dto.session_data;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the session data for a user in the SSO system.
 * This class is used to store and manage user account information within the session.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Nov-04
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionUserData
{    
    private Map<String, UserAccount> users = new HashMap<String, UserAccount>();
}