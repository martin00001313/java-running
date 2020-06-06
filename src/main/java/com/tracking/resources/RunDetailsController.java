package com.tracking.resources;

import com.tracking.domain.RunDetails;
import com.tracking.dto.NewRunDetailsDTO;
import com.tracking.dto.ResponseMessageDTO;
import com.tracking.dto.RunDetailsDTO;
import com.tracking.enums.ActionTypes;
import com.tracking.services.AuthService;
import com.tracking.services.RunService;
import com.tracking.utils.ActionValidation;
import com.tracking.utils.Mapper;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller responsible for operations done on jugging data
 *
 * @author martin
 */
@RestController
@RequestMapping("/api/v1/run/details")
public class RunDetailsController {

    @Autowired
    private ActionValidation actionValidation;

    @Autowired
    private AuthService authService;

    @Autowired
    private RunService runService;

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(
            final @RequestParam(required = true) String token,
            final @RequestBody(required = true) NewRunDetailsDTO runDetails
    ) {
        if (!this.authService.validateAuthToken(token)) {
            System.err.println("The token is invalid!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Toke is invalid!"));
        }

        if (!actionValidation.validateAction(token, runDetails.getUserId(), ActionTypes.CRUD_RUN)) {
            System.err.println("The operation is not allowed!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Action is unavailable for the action!"));
        }

        final Optional<RunDetails> newData = this.runService.create(runDetails, token);
        if (!newData.isPresent()) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessageDTO("The operation has been failed!"));
        }
        return ResponseEntity.ok(Mapper.convertToDTO(newData.get()));
    }

    @PutMapping(path = "/update")
    public ResponseEntity<?> update(
            final @RequestParam(required = true) String token,
            final @RequestBody(required = true) RunDetailsDTO runDetails
    ) {
        if (!this.authService.validateAuthToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Toke is invalid!"));
        }

        if (!actionValidation.validateAction(token, runDetails.getUserId(), ActionTypes.CRUD_RUN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Action is unavailable for the action!"));
        }

        final Optional<RunDetails> updatedData = this.runService.update(runDetails, token);
        if (!updatedData.isPresent()) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessageDTO("The operation has been failed!"));
        }
        return ResponseEntity.ok(updatedData);
    }

    @GetMapping(path = "/by-user-id/all")
    public ResponseEntity<?> getAllByUserId(
            final @RequestParam(required = true) String token,
            final @RequestParam(required = true) String userId
    ) {
        if (!this.authService.validateAuthToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Toke is invalid!"));
        }

        if (!actionValidation.validateAction(token, userId, ActionTypes.CRUD_RUN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Action is unavailable for the action!"));
        }

        final List<RunDetails> data = this.runService.getAllDetailsByUserId(userId);

        return ResponseEntity.ok(data);
    }

    @GetMapping(path = "/by-user-id/paginated")
    public ResponseEntity<?> getByUserIdPaginated(
            final @RequestParam(required = true) String token,
            final @RequestParam(required = true) String userId,
            final @RequestParam(required = true) int pageSize,
            final @RequestParam(required = true) int pageNumber,
            final @RequestParam(defaultValue = "") String sorting
    ) {
        if (!this.authService.validateAuthToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Toke is invalid!"));
        }

        if (!actionValidation.validateAction(token, userId, ActionTypes.CRUD_RUN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Action is unavailable for the action!"));
        }

        final Page<RunDetails> data = this.runService.getDetailsByUserIdPaginated(userId, pageSize, pageNumber, sorting);

        return ResponseEntity.ok(Mapper.convertToDTO(data));
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

        final Page<RunDetails> reports = this.runService.getDetailsByQueryJsonPaginated(queryJson, pageSize, pageNumber, sorting, token);
        return ResponseEntity.ok(Mapper.convertToDTO(reports));
    }
}
