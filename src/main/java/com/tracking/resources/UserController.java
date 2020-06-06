package com.tracking.resources;

import com.tracking.domain.User;
import com.tracking.dto.ResponseMessageDTO;
import com.tracking.dto.UserDTO;
import com.tracking.enums.ActionTypes;
import com.tracking.services.AuthService;
import com.tracking.services.UserService;
import com.tracking.utils.ActionValidation;
import com.tracking.utils.Mapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible to get/update/delete users
 *
 * @author martin
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private ActionValidation actionValidation;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @GetMapping(path = "/by-id")
    public ResponseEntity<?> getUserByID(
            @RequestParam(required = true) final String userId,
            @RequestParam(required = true) final String token) {

        if (!this.authService.validateAuthToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Token is invalid!"));
        }

        if (!this.actionValidation.validateAction(token, userId, ActionTypes.CRUD_USER)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("The opertion is not allowed!"));
        }

        final Optional<User> user = this.userService.getById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessageDTO("User doesn't exist!"));
        }
        return ResponseEntity.ok(Mapper.convertToDTO(user.get()));
    }

    @GetMapping(path = "/by-admin-id")
    public ResponseEntity<?> getUserByAdminId(
            @RequestParam(required = true) final String adminId,
            @RequestParam(required = true) final String token) {

        if (!this.authService.validateAuthToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Token is invalid!"));
        }

        if (!this.actionValidation.validateAction(token, adminId, ActionTypes.CRUD_USER)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("The opertion is not allowed!"));
        }

        final List<UserDTO> users = this.userService.getByAdminId(adminId).stream().map(u -> Mapper.convertToDTO(u)).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping(path = "/by-json-query/paginated")
    public ResponseEntity<?> getPaginatedByQuery(
            final @RequestParam(required = true) String queryJson,
            final @RequestParam(required = true) String token,
            final @RequestParam(required = true) int pageSize,
            final @RequestParam(required = true) int pageNumber,
            final @RequestParam(defaultValue = "") String sorting
    ) {
        if (!this.authService.validateAuthToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Token is invalid!"));
        }
        final Page<User> users = this.userService.getDetailsPaginatedByQString(queryJson, pageSize, pageNumber, sorting, token);
        return ResponseEntity.ok(Mapper.convertToUsersPageDTO(users));
    }

    @PutMapping
    public ResponseEntity<?> updateUser(
            @RequestBody(required = true) final UserDTO user,
            @RequestParam(required = true) final String token) {

        if (!this.authService.validateAuthToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Token is invalid!"));
        }

        if (!this.actionValidation.validateAction(token, user.getId(), ActionTypes.CRUD_USER)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("The opertion is not allowed!"));
        }

        final Optional<User> updatedData = this.userService.update(user);
        if (!updatedData.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessageDTO("The user data is not correct!"));
        }
        return ResponseEntity.ok(Mapper.convertToDTO(updatedData.get()));
    }

    @DeleteMapping(path = "/by-id")
    public ResponseEntity<?> deleteByID(
            @RequestParam(required = true) final String userId,
            @RequestParam(required = true) final String token) {

        if (!this.authService.validateAuthToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Token is invalid!"));
        }

        if (!this.actionValidation.validateAction(token, userId, ActionTypes.CRUD_USER)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("The opertion is not allowed!"));
        }

        if (!this.userService.deleteUser(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessageDTO("The user doesn't exist!"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessageDTO("The user has been deleted successfully!"));
    }
}
