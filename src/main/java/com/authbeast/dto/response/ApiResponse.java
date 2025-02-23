package com.zentois.authbeast.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an API response DTO.
 *
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Aug-09
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse
{
    private boolean success;

    private String message;
}