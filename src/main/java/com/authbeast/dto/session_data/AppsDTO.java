package com.zentois.authbeast.dto.session_data;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO representing information about user applications.
 * This class contains the user's email and a list of application information strings.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2025-Jan-24
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppsDTO
{
    private String email;

    @Builder.Default
    private List<String> appsInfo = new ArrayList<String>();
}