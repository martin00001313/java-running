package com.tracking.dto;

import com.tracking.enums.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO to represent details of the new registered users.
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewUserDTO {

    private String firstName;

    private String lastName;

    private String dot;

    private String email;

    private UserRoles role;

    private String password;

    private String adminId;
}
