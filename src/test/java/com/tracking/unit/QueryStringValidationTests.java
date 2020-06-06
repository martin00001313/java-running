package com.tracking.unit;

import com.tracking.data.Location;
import com.tracking.domain.RunDetails;
import com.tracking.domain.User;
import com.tracking.dto.NewRunDetailsDTO;
import com.tracking.dto.NewUserDTO;
import com.tracking.dto.UserDTO;
import com.tracking.enums.UserRoles;
import com.tracking.repositories.UserRepository;
import com.tracking.services.AuthService;
import com.tracking.services.RunService;
import com.tracking.services.UserService;
import io.jsonwebtoken.lang.Assert;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
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
public class QueryStringValidationTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private RunService runService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void testQueryString() {

        final String adminEmail = "admin@mail.ru";
        final List<String> userEmails = Arrays.asList("user1@mail.ru", "user2@mail.ru");
        final String dot = "0013";
        final String pass = "my_pass13";

        String queryString1 = "{speed: {$gt: 100}}";
        String queryString2 = "{$and: [{speed: {$gt: 100}}, {$or: [{distance: {$lt: 124}}, {distance: {$gt: 126}}]}]}";
        Optional<User> user = this.userRepository.findOneByEmail(adminEmail);
        if (user.isPresent()) {
            this.userService.deleteUser(user.get().getId());
        }

        final Optional<UserDTO> createdAdmin = this.authService.register(new NewUserDTO("Martin", "Ayvazyan", dot, adminEmail, UserRoles.ADMIN, pass, null), null);
        Assert.isTrue(createdAdmin.isPresent());

        userEmails.forEach(e -> {
            Optional<User> userTmp = this.userRepository.findOneByEmail(e);
            if (userTmp.isPresent()) {
                this.userService.deleteUser(userTmp.get().getId());
            }
            final Optional<UserDTO> createdRegularUser = this.authService.register(new NewUserDTO("Martin", "Ayvazyan", dot,
                    e, UserRoles.REGULAR, pass, createdAdmin.get().getAdminId()), createdAdmin.get().getToken());
            Assert.isTrue(createdRegularUser.isPresent());

            final NewRunDetailsDTO runDetails = new NewRunDetailsDTO();
            runDetails.setLocation(new Location(40.1872, 44.5152));
            runDetails.setTime(LocalTime.now());
            runDetails.setUserId(createdRegularUser.get().getId());
            for (int i = 0; i < 5; ++i) {
                runDetails.setSpeed(100. + i);
                runDetails.setDistance(122. + i);
                runDetails.setDate(LocalDate.now().plusDays(i));
                final Optional<RunDetails> savedData = this.runService.create(runDetails, createdAdmin.get().getToken());
                Assert.isTrue(savedData.isPresent(), "Run details should be saved!");
                Assert.isTrue(savedData.get().getUserId().equals(createdRegularUser.get().getId()));
                Assert.isTrue(savedData.get().getWeather() != null && savedData.get().getWeather().getTemp() != null);
            }

            final Page<RunDetails> details1 = this.runService.getDetailsByQueryJsonPaginated(queryString1, Integer.MAX_VALUE, 0, "", createdRegularUser.get().getToken());
            Assert.isTrue(details1.getContent().size() == 4);

            final Page<RunDetails> details2 = this.runService.getDetailsByQueryJsonPaginated(queryString2, Integer.MAX_VALUE, 0, "", createdRegularUser.get().getToken());
            Assert.isTrue(details2.getContent().size() == 1);
        });

        final Page<RunDetails> details1 = this.runService.getDetailsByQueryJsonPaginated(queryString1, Integer.MAX_VALUE, 0, "", createdAdmin.get().getToken());
        Assert.isTrue(details1.getContent().size() == 8);

        final Page<RunDetails> details2 = this.runService.getDetailsByQueryJsonPaginated(queryString2, Integer.MAX_VALUE, 0, "", createdAdmin.get().getToken());
        Assert.isTrue(details2.getContent().size() == 2);
    }
}
