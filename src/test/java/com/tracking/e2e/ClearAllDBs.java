package com.tracking.e2e;

import com.tracking.repositories.RunDetailsRepository;
import com.tracking.repositories.RunReportRepository;
import com.tracking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author martin
 */
@Component
public class ClearAllDBs {

    @Autowired
    private RunDetailsRepository runDetailsRepository;

    @Autowired
    private RunReportRepository runReportRepository;

    @Autowired
    private UserRepository userRepository;

    public void clean() {
        this.runDetailsRepository.deleteAll();
        this.runReportRepository.deleteAll();
        this.userRepository.deleteAll();
    }
}
