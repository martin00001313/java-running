package com.tracking.resources;

import com.tracking.domain.RunReport;
import com.tracking.dto.ResponseMessageDTO;
import com.tracking.enums.ActionTypes;
import com.tracking.services.AuthService;
import com.tracking.services.RunReportService;
import com.tracking.utils.ActionValidation;
import com.tracking.utils.Mapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * APIs to do operations on generated reports
 *
 * @author martin
 */
@RestController
@RequestMapping("/api/v1/tests")
public class RunReportController {

    @Autowired
    private ActionValidation actionValidation;

    @Autowired
    private AuthService authService;

    @Autowired
    private RunReportService runReportService;

    @GetMapping(path = "/by-user-id/all")
    public ResponseEntity<?> getAll(
            final @RequestParam(required = true) String userId,
            final @RequestParam(required = true) String token) {

        if (!this.authService.validateAuthToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Token is invalid!"));
        }

        if (!actionValidation.validateAction(token, userId, ActionTypes.READ_REPORT)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("The operation is not allowed for the provided user!"));
        }

        final List<RunReport> reports = this.runReportService.getAllReportsByUserId(userId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping(path = "/by-user-id/paginated")
    public ResponseEntity<?> getPaginated(
            final @RequestParam(required = true) String userId,
            final @RequestParam(required = true) String token,
            final @RequestParam(required = true) int pageSize,
            final @RequestParam(required = true) int pageNumber,
            final @RequestParam(defaultValue = "") String sorting
    ) {

        if (!this.authService.validateAuthToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Token is invalid!"));
        }

        if (!actionValidation.validateAction(token, userId, ActionTypes.READ_REPORT)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("The operation is not allowed for the provided user!"));
        }

        final Page<RunReport> reports = this.runReportService.getDetailsPaginatedByUserId(userId, pageSize, pageNumber, sorting);
        return ResponseEntity.ok(Mapper.convertToReportDTO(reports));
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

        final Page<RunReport> reports = this.runReportService.getDetailsPaginatedByQString(queryJson, pageSize, pageNumber, sorting, token);
        return ResponseEntity.ok(Mapper.convertToReportDTO(reports));
    }
}
