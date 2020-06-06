package com.tracking.domain;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The from of report entity
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "mp_run_report")
public class RunReport {

    @Id
    private String id;

    private String userId;

    private double avgSpeed;

    private double avgDistance;

    private LocalDate endDate;
}
