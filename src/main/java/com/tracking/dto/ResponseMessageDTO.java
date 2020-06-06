package com.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO to represent message data as an API response.
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseMessageDTO {

    private String message;
}
