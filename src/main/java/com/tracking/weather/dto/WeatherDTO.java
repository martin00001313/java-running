package com.tracking.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A structure to represent weather DTO
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WeatherDTO {

    // The field ID
    private Long id;

    // The main weather type
    private String main;

    // A brief description of the wether
    private String description;

    // The icone code of the weather.
    // See https://openweathermap.org/weather-conditions#How-to-get-icon-URL
    private String icon;
}
