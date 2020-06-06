package com.tracking.schedulers;

import com.tracking.services.RunReportService;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * A structure responsible for run report generation.
 *
 * @author martin
 */
@Component
public class RunReportsScheduler {

    @Autowired
    private RunReportService runReportService;
    /**
     * Generate run report for each week.
     */
    @Scheduled(cron = "0 0 1 * * MON")
    public void generateRunReport() {
        System.out.println("Scheduler startes execution: " + ZonedDateTime.now());
        this.runReportService.generate();
        System.out.println("Scheduler ends the execution: " + ZonedDateTime.now());
    }
}
