package com.tracking.utils;

import com.tracking.data.TokenProperties;
import com.tracking.domain.User;
import com.tracking.enums.ActionTypes;
import com.tracking.enums.UserRoles;
import com.tracking.services.UserService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

/**
 * Action validation
 *
 * @author martin
 */
@Component
public class ActionValidation {

    @Autowired
    private UserService userService;

    public boolean validateAction(final String token, final String userId, final ActionTypes action) {
        final Optional<TokenProperties> data = TokenGenerator.fetchAllCredentialsFromToken(token);

        if (!data.isPresent()) {
            return false;
        }

        if (data.get().getRoleCode() == UserRoles.REGULAR) {
            return data.get().getUserId().equals(userId);
        }

        final Optional<User> user = this.userService.getById(userId);
        if (!user.isPresent() || !user.get().getAdminId().equals(data.get().getAdminId())) {
            return false;
        }

        if (data.get().getRoleCode() == UserRoles.MANAGER) {
            return action == ActionTypes.CRUD_USER;
        }

        return true;
    }

    /**
     * Function to generate criteria based on token data(i.e.user's permissions)
     *
     * @param token
     * @param actionTypes
     * @return generated criteria
     */
    public Criteria generateCriteria(final String token, final ActionTypes actionTypes) {

        final Optional<TokenProperties> tokenData = TokenGenerator.fetchAllCredentialsFromToken(token);

        // If token is invalid or the role of the user if Manager, don't allow query
        if (!tokenData.isPresent() || (tokenData.get().getRoleCode() == UserRoles.MANAGER && actionTypes != ActionTypes.CRUD_USER)) {
            return Criteria.where("id").is(null);
        }

        // In case of regular user, allow query for user's data only
        if (tokenData.get().getRoleCode() == UserRoles.REGULAR) {
            return Criteria.where("userId").is(tokenData.get().getUserId());
        }

        // If it's teh admin, allow query on all entities which user's admin id the the same as ID of token's user
        final List<String> users = this.userService.getByAdminId(tokenData.get().getAdminId()).stream().map(u -> u.getId()).collect(Collectors.toList());
        return Criteria.where("userId").in(users);
    }
}
