package com.tracking.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Location details.
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Location {

    private Double lat;

    private Double lng;
}
