package com.tracking.repositories;

import com.tracking.domain.RunReport;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository to keep weekly report of user report
 *
 * @author martin
 */
public interface RunReportRepository extends MongoRepository<RunReport, String> {

    public List<RunReport> findByUserId(final String userId);

    public void deleteByUserIdIn(final List<String> userId);
}
