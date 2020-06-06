package com.tracking.repositories;

import com.tracking.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Database to keep user details.
 *
 * @author martin
 */
public interface UserRepository extends MongoRepository<User, String> {

    public Optional<User> findOneByEmail(final String email);

    public Optional<User> findOneByDotAndEmailAndEncryptedPassword(final String dot, final String email, final String encryptedPassword);

    public List<User> findByAdminId(final String adminId);

    public void deleteByIdIn(final List<String> userId);
}
