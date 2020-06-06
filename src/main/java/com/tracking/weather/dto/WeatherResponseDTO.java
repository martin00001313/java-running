package com.tracking.weather.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO to represent weather API response details.
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WeatherResponseDTO {

    private float lat;
    
    private float lon;
    
    private String timezone;
    
    private String timezone_offset;
    
    private CurrentWeatherDTO current;
    
    private List<DailyWeatherDTO> daily;
    
    
}
