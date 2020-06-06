package com.tracking.weather.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO to represent current date's weather details.
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CurrentWeatherDTO {

    private long dt;

    private long sunrise;

    private long sunset;

    private double temp;

    private double feels_like;

    private long pressure;

    private long humidity;

    private float dew_point;

    private float uvi;

    private long clouds;

    private long visibility;

    private float wind_speed;

    private long wind_deg;

    private List<WeatherDTO> weather;

    private Map<String, Double> rain;
}
