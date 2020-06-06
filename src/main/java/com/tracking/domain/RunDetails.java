package com.tracking.domain;

import com.tracking.data.Location;
import com.tracking.weather.dto.DailyWeatherDTO;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A structure to provide entity structure for each run details.
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "mp_run")
public class RunDetails {

    @Id
    private String id;

    private String userId;

    private LocalDate date;

    private LocalTime time;

    private double speed;

    private double distance;

    private Location location;

    private DailyWeatherDTO weather;
}
