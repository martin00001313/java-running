package com.tracking.dto;

import com.tracking.data.Location;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * A DTO to represent run details to create appropriate entity in the database.
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class NewRunDetailsDTO {

    private String userId;

    private LocalDate date;

    private LocalTime time;

    private Double speed;

    private Double distance;

    private Location location;
}
