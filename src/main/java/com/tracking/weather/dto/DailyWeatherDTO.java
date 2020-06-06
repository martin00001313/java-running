package com.tracking.weather.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A structure to represent daily weather details.
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DailyWeatherDTO {

    private long dt;

    private long sunrise;

    private long sunset;

    private TemperatureDTO temp;

    private List<WeatherDTO> weather;

    private int clouds;

    private float rain;

}
