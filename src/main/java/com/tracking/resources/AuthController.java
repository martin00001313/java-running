package com.tracking.resources;

import com.tracking.dto.NewUserDTO;
import com.tracking.dto.ResponseMessageDTO;
import com.tracking.dto.UserDTO;
import com.tracking.services.AuthService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for authentication, token validation, user
 * registration.
 *
 * @author martin
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * An API to register admin.
     *
     * @param user
     * @return Details of the new registered user
     */
    @PostMapping(path = "/register/admin")
    public ResponseEntity<?> registerUser(
            @RequestBody(required = true) NewUserDTO user) {

        final Optional<UserDTO> registeredUser = this.authService.register(user, null);

        if (!registeredUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessageDTO("The user data is invalid!"));
        }
        return ResponseEntity.ok(registeredUser.get());
    }

    /**
     * An API to register a new user.
     *
     * @param user
     * @param token
     * @return Details of the new registered user
     */
    @PostMapping(path = "/register/user")
    public ResponseEntity<?> registerUser(
            @RequestBody(required = true) NewUserDTO user,
            @RequestParam(required = true) String token) {

        final Optional<UserDTO> registeredUser = this.authService.register(user, token);

        if (!registeredUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessageDTO("The user data is invalid!"));
        }
        return ResponseEntity.ok(registeredUser.get());
    }

    /**
     * API to login the user based on the provided details.
     *
     * @param email
     * @param dot
     * @param password
     * @return
     */
    @GetMapping(path = "/login")
    public ResponseEntity<?> login(
            @RequestParam(required = true) final String email,
            @RequestParam(required = true) final String dot,
            @RequestParam(required = true) final String password
    ) {
        if (email == null || dot == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessageDTO("The login details are invalid!"));
        }
        final Optional<UserDTO> userDTO = this.authService.login(email, dot, password);

        if (!userDTO.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("The login details are not correct!"));
        }
        return ResponseEntity.ok(userDTO.get());
    }
}
