package com.tracking.unit;

import com.tracking.data.Location;
import com.tracking.domain.RunDetails;
import com.tracking.domain.User;
import com.tracking.dto.NewUserDTO;
import com.tracking.dto.UserDTO;
import com.tracking.dto.NewRunDetailsDTO;
import com.tracking.enums.UserRoles;
import com.tracking.repositories.UserRepository;
import com.tracking.services.AuthService;
import com.tracking.services.RunService;
import com.tracking.services.UserService;
import com.tracking.utils.Mapper;
import io.jsonwebtoken.lang.Assert;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

/**
 *
 * @author martin
 */
@SpringBootTest
public class RunServiceValidationTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private RunService runService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void testRunCRUD() {

        final String adminEmail = "admin@mail.ru";
        final String userEmail = "user@mail.ru";
        final String dot = "0013";
        final String pass = "my_pass13";

        Optional<User> user = this.userRepository.findOneByEmail(adminEmail);
        if (user.isPresent()) {
            this.userService.deleteUser(user.get().getId());
        }
        user = this.userRepository.findOneByEmail(userEmail);
        if (user.isPresent()) {
            this.userService.deleteUser(user.get().getId());
        }

        final Optional<UserDTO> createdAdmin = this.authService.register(new NewUserDTO("Martin", "Ayvazyan", dot, adminEmail, UserRoles.ADMIN, pass, null), null);
        Assert.isTrue(createdAdmin.isPresent());
        final Optional<UserDTO> createdRegularUser = this.authService.register(new NewUserDTO("Martin", "Ayvazyan", dot,
                userEmail, UserRoles.REGULAR, pass, createdAdmin.get().getAdminId()), createdAdmin.get().getToken());
        Assert.isTrue(createdRegularUser.isPresent());

        final NewRunDetailsDTO runDetails = new NewRunDetailsDTO();
        runDetails.setDistance(122.);
        runDetails.setLocation(new Location(40.1872, 44.5152));
        runDetails.setTime(LocalTime.now());
        runDetails.setUserId(createdRegularUser.get().getId());
        runDetails.setSpeed(100.);
        for (int i = 0; i < 5; ++i) {
            runDetails.setDate(LocalDate.now().plusDays(i));
            final Optional<RunDetails> savedData = this.runService.create(runDetails, createdAdmin.get().getToken());
            Assert.isTrue(savedData.isPresent(), "Run details should be saved!");
            Assert.isTrue(savedData.get().getUserId().equals(createdRegularUser.get().getId()));
            Assert.isTrue(savedData.get().getWeather() != null && savedData.get().getWeather().getTemp() != null);
        }

        final List<RunDetails> userDetails = this.runService.getAllDetailsByUserId(createdRegularUser.get().getId());
        Assert.isTrue(userDetails.size() == 5);

        final Page<RunDetails> paginatedDetailsFirst = this.runService.getDetailsByUserIdPaginated(createdRegularUser.get().getId(), 4, 0, "");
        Assert.isTrue(paginatedDetailsFirst.isFirst());
        Assert.isTrue(paginatedDetailsFirst.isLast());
        Assert.isTrue(paginatedDetailsFirst.getContent().size() == 4, "For the first portion, the content size should be 4");

        final Page<RunDetails> paginatedDetailsSecond = this.runService.getDetailsByUserIdPaginated(createdRegularUser.get().getId(), 4, 1, "");
        Assert.isTrue(!paginatedDetailsSecond.isFirst());
        Assert.isTrue(paginatedDetailsSecond.isLast());
        Assert.isTrue(paginatedDetailsSecond.getContent().size() == 1, "For the second portion, the content size should be 1");

        final Page<RunDetails> paginatedDetailsThird = this.runService.getDetailsByUserIdPaginated(createdRegularUser.get().getId(), 4, 2, "");
        Assert.isTrue(!paginatedDetailsThird.isFirst());
        Assert.isTrue(paginatedDetailsThird.isLast());
        Assert.isTrue(paginatedDetailsThird.getContent().isEmpty(), "For the third portion, the content should be empty!");

        final RunDetails exp = paginatedDetailsSecond.getContent().get(0);
        final LocalDate date = LocalDate.now().plusDays(4);
        exp.setDate(date);
        final Optional<RunDetails> updated = this.runService.update(Mapper.convertToDTO(exp), createdRegularUser.get().getToken());
        Assert.isTrue(updated.isPresent());
        Assert.isTrue(updated.get().getDate().equals(date));

        this.userService.deleteUser(createdRegularUser.get().getId());

        final List<RunDetails> contentOfDeletedUser = this.runService.getAllDetailsByUserId(createdRegularUser.get().getId());
        Assert.isTrue(contentOfDeletedUser.isEmpty(), "After deletion of the user, approprite run details shoul be removed!");
    }
}
