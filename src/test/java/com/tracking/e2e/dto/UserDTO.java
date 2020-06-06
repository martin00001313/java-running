package com.tracking.e2e.dto;

import com.tracking.enums.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * A DTO to represent user details including authentication token.
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class UserDTO {

    private String id;

    private String firstName;

    private String lastName;

    private String dot;

    private String email;

    private UserRoles role;

    private String token;

    private String adminId;
}
