package com.tracking.dto;

import com.tracking.weather.dto.DailyWeatherDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * A DTO to represent run details
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class RunDetailsDTO extends NewRunDetailsDTO {

    private String id;

    private DailyWeatherDTO weather;
}
