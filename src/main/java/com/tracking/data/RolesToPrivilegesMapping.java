package com.tracking.data;

import com.tracking.enums.UserRoles;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

/**
 * Roles to privileges mapping
 *
 * @author martin
 */
public class RolesToPrivilegesMapping {

    public final static EnumMap<UserRoles, List<UserRoles>> ROLES_TO_PRIVILEGES;

    static {
        ROLES_TO_PRIVILEGES = new EnumMap<>(UserRoles.class);
        ROLES_TO_PRIVILEGES.put(UserRoles.ADMIN, Arrays.asList(UserRoles.ADMIN, UserRoles.MANAGER, UserRoles.REGULAR));
        ROLES_TO_PRIVILEGES.put(UserRoles.MANAGER, Arrays.asList(UserRoles.MANAGER, UserRoles.REGULAR));
        ROLES_TO_PRIVILEGES.put(UserRoles.REGULAR, Collections.singletonList(UserRoles.REGULAR));
    }
}
