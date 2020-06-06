package com.tracking.services;

import com.tracking.clients.WeatherClient;
import com.tracking.data.Location;
import com.tracking.weather.dto.DailyWeatherDTO;
import com.tracking.weather.dto.WeatherResponseDTO;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service responsible for weather data preparation
 *
 * @author martin
 */
@Service
public class WeatherService {

    @Autowired
    private WeatherClient weatherCleint;

    /**
     * Function to prepare daily weather data base don the provided date and
     * location details.
     *
     * @param date Should be in range [current date, current date + 7 days]
     * @param loc
     * @return The daily weather
     */
    public Optional<DailyWeatherDTO> getWeather(final LocalDate date, final Location loc) {

        final LocalDate currDate = LocalDate.now();
        final Duration duration = Duration.between(currDate.atStartOfDay(), date.atStartOfDay());
        if ((duration.isNegative() && duration.negated().toDays() != 0)
                || (duration.negated().isNegative() && duration.toDays() > 7)) {
            System.err.println("The date should be in [curr date, curr date + 7 days] interval!");
            return Optional.empty();
        }

        final int dayIdx = duration.isNegative() ? 0 : (int) duration.toDays();
        final Optional<WeatherResponseDTO> response = this.weatherCleint.getWeatherForecast(loc);
        if (!response.isPresent() || response.get().getDaily().size() <= dayIdx) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.get().getDaily().get(dayIdx));
    }
}
