package com.tracking.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO to represent temperature details
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TemperatureDTO {

    private float day;

    private float min;

    private float max;

    private float night;

    private float eve;

    private float morn;
}
