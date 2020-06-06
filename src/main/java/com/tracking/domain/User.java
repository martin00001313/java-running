package com.tracking.domain;

import com.tracking.enums.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A structure to represent entity form of the data kept in user database.
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "mp_users")
public class User {

    @Id
    private String id;

    private String firstName;

    private String lastName;

    private String dot;

    private String email;

    private String encryptedPassword;

    private UserRoles role;

    private String adminId;
}
