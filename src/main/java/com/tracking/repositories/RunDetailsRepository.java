package com.tracking.repositories;

import com.tracking.domain.RunDetails;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * A repository to keep run details.
 *
 * @author martin
 */
public interface RunDetailsRepository extends MongoRepository<RunDetails, String> {

    public List<RunDetails> findByUserId(final String userId);

    public void deleteByUserIdIn(final List<String> userId);
}
