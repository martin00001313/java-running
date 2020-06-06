package com.tracking.services;

import com.tracking.domain.User;
import com.tracking.dto.UserDTO;
import com.tracking.enums.ActionTypes;
import com.tracking.enums.UserRoles;
import com.tracking.repositories.UserRepository;
import com.tracking.utils.ActionValidation;
import com.tracking.utils.PageRequestBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User related DAO
 *
 * @author martin
 */
@Service
public class UserService {

    @Autowired
    private ActionValidation actionValidation;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RunService runService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get user data based on the provided ID
     *
     * @param id
     * @return The prepared data
     */
    public Optional<User> getById(final String id) {
        return this.userRepository.findById(id);
    }

    /**
     * Get users by admin ID
     *
     * @param amdinId
     * @return Users having the provided admin ID
     */
    public List<User> getByAdminId(final String amdinId) {
        return this.userRepository.findByAdminId(amdinId);
    }

    public Page<User> getDetailsPaginatedByQString(final String queryJson, final Integer pageSize,
            final Integer pageNumber, final String sortingCriteria, final String token) {

        final PageRequest pageRequest = PageRequestBuilder.getPageRequest(pageSize, pageNumber, sortingCriteria);
        return this.getReportsPaginatedQString(pageRequest, queryJson, token);
    }

    /**
     * Return paginated version of reports.
     *
     * @param pageRequest The page request details
     * @param queryJson The JSON query
     * @return The appropriate page of the received reports
     */
    private Page<User> getReportsPaginatedQString(final PageRequest pageRequest, final String queryJson, final String token) {

        final BasicQuery query = new BasicQuery(queryJson);
        query.addCriteria(this.actionValidation.generateCriteria(token, ActionTypes.CRUD_USER));

        return new PageImpl<>(
                mongoTemplate.find(query.with(pageRequest), User.class),
                pageRequest,
                mongoTemplate.count(query, User.class));
    }

    /**
     * Update user base info.
     *
     * @param user
     * @return Updated state of the user data
     */
    @Transactional
    public Optional<User> update(final UserDTO user) {

        if (user == null || user.getId() == null || user.getEmail() == null || user.getFirstName() == null
                || user.getLastName() == null || user.getRole() == null) {
            return Optional.empty();
        }

        final Optional<User> userData = this.userRepository.findById(user.getId());
        if (!userData.isPresent() || !userData.get().getAdminId().equals(user.getAdminId())
                || userData.get().getRole() != user.getRole()) {
            return Optional.empty();
        }

        userData.get().setFirstName(user.getFirstName());
        userData.get().setLastName(user.getLastName());
        userData.get().setDot(user.getDot());
        userData.get().setEmail(user.getEmail());

        return Optional.ofNullable(this.userRepository.save(userData.get()));
    }

    /**
     * Delete registered user details.
     *
     * @param userId
     * @return The state of the operation
     */
    @Transactional
    public boolean deleteUser(final String userId) {

        final Optional<User> user = this.userRepository.findById(userId);
        if (!user.isPresent()) {
            return false;
        }

        if (user.get().getRole() == UserRoles.ADMIN) {
            final List<String> usersToRemove = this.userRepository.findByAdminId(userId).stream().map(u -> u.getId()).collect(Collectors.toList());
            this.userRepository.deleteByIdIn(usersToRemove);
            this.runService.deleteByUserId(usersToRemove);
        } else {
            this.userRepository.deleteById(userId);
            this.runService.deleteByUserId(Collections.singletonList(userId));
        }

        return true;
    }
}
