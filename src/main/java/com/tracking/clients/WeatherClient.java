package com.tracking.clients;

import com.tracking.data.Location;
import com.tracking.weather.dto.WeatherResponseDTO;
import io.vavr.control.Try;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * A client responsible for weather details preparation.
 *
 * @author martin
 */
@Service
public class WeatherClient {

    /**
     * The rest template
     */
    @Autowired
    private RestTemplate restTemplate;

    private static final String CURRENT_WEATHER_API = "https://api.openweathermap.org/data/2.5/onecall?lat=%.6f&lon=%.6f&exclude=minutely,hourly&appid=cf1b854910b87295acfe48e14ec69b55";
    private static final String HISTORIC_WEATHER_API = "http://api.openweathermap.org/data/2.5/onecall/timemachine?lat=%d&lon=%d&dt=%d&appid=cf1b854910b87295acfe48e14ec69b55";

    public Optional<WeatherResponseDTO> getWeatherForecast(final Location location) {

        if (location.getLat() == null || location.getLng() == null) {
            return Optional.empty();
        }

        final String url = String.format(CURRENT_WEATHER_API, location.getLat(), location.getLng());
        final Try<WeatherResponseDTO> response = Try.of(() -> this.restTemplate.getForObject(url, WeatherResponseDTO.class));
        
        if (response.isFailure()) {
            System.err.println("Couldn't get weather details!");
            return Optional.empty();
        }
        return Optional.of(response.get());
    }
}
