package com.tracking.data;

import com.tracking.enums.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * A structure to represent core properties of token.
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class TokenProperties {

    private String userId;
    private UserRoles roleCode;
    private String adminId;
}
