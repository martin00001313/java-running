package com.tracking.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to represent report data
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RunReportDTO {

    private String id;

    private String userId;

    private double avgSpeed;

    private double avgDistance;

    private LocalDate endDate;
}
