package com.tracking.e2e;

import com.tracking.data.Location;
import com.tracking.e2e.dto.NewRunDetailsDTO;
import com.tracking.e2e.dto.NewUserDTO;
import com.tracking.e2e.dto.PageDTO;
import com.tracking.e2e.dto.RunDetailsDTO;
import com.tracking.e2e.dto.UserDTO;
import com.tracking.enums.UserRoles;
import io.jsonwebtoken.lang.Assert;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author martin
 */
@SpringBootTest
public class E2ETest {

    @Autowired
    private AppClient appClient;

    @Autowired
    private ClearAllDBs clearAllDBs;

    private UserDTO admin;

    private UserDTO manager;

    private List<UserDTO> users;

    @Test
    public void flowTest() {
        clearAllDBs.clean();

        Assert.isTrue(this.appClient.testConnection().isPresent());
        // register users
        this.initUsers();

        // create run details
        this.createRunDetailsForUser(manager.getId(), 3, admin.getToken());
        users.forEach(u -> this.createRunDetailsForUser(u.getId(), 6, admin.getToken()));

        this.getPaginatedRunDetails();
        this.getRunDetailsBasedOnQuery();
        // Get all run details of the user
        final List<RunDetailsDTO> runDetails = this.appClient.getRunDetailsByUserId(users.get(0).getId(), admin.getToken());
        Assert.isTrue(runDetails.size() == 6);

        this.updateRunDetails(runDetails.get(0), admin.getToken(), new Location(46.8182, 8.2275));
    }

    private void createRunDetailsForUser(final String userId, final int days, final String token) {

        final NewRunDetailsDTO runDetails = new NewRunDetailsDTO();
        runDetails.setDistance(122.);
        runDetails.setLocation(new Location(40.1872, 44.5152));
        runDetails.setTime(LocalTime.now());
        runDetails.setUserId(userId);
        runDetails.setSpeed(180.);
        for (int i = 0; i < days; ++i) {
            runDetails.setDate(LocalDate.now().plusDays(i));
            Assert.isTrue(this.appClient.createRunData(runDetails, token).isPresent());
        }
    }

    public void updateRunDetails(final RunDetailsDTO runData, final String token, final Location loc) {

        runData.setLocation(loc);
        Assert.isTrue(this.appClient.updateRunData(runData, token).isPresent());
    }

    private void initUsers() {

        Assert.isTrue(this.appClient.regsterAdmin(new NewUserDTO("Martin", "Ayvazyan", "0013", "martn00001313@gmail.com", UserRoles.ADMIN, "123456", null)).isPresent());
        final Optional<UserDTO> adminData = this.appClient.loginUser("martn00001313@gmail.com", "0013", "123456");
        Assert.isTrue(adminData.isPresent());
        this.admin = adminData.get();

        final Optional<UserDTO> managerData = this.appClient.regsterUser(new NewUserDTO("George", "West", "0017", "west@mail.ru", UserRoles.MANAGER,
                "345678", adminData.get().getAdminId()), adminData.get().getToken());
        Assert.isTrue(managerData.isPresent());
        this.manager = managerData.get();

        final List<UserDTO> usersData = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            final Optional<UserDTO> user = this.appClient.regsterUser(new NewUserDTO("User" + i,
                    "West", "DOT-" + i, String.format("mail%d@mail.ru", i), UserRoles.REGULAR, "457832", managerData.get().getAdminId()), managerData.get().getToken());
            Assert.isTrue(user.isPresent());
            usersData.add(user.get());
        }
        this.users = usersData;
    }

    private void getPaginatedRunDetails() {

        // Get paginated data of the user run details
        final Optional<PageDTO> runDetailsPage = this.appClient.getRunDetailsByUserIdPaginated(users.get(0).getId(), admin.getToken(), 4, 0, "'+'date");
        Assert.isTrue(runDetailsPage.isPresent());
        Assert.isTrue(runDetailsPage.get().getContent().size() == 4);
        for (int i = 1; i < 4; ++i) {
            Assert.isTrue(runDetailsPage.get().getContent().get(i - 1).getDate().compareTo(runDetailsPage.get().getContent().get(i).getDate()) <= 0);
        }

        final Optional<PageDTO> runDetailsPage2 = this.appClient.getRunDetailsByUserIdPaginated(users.get(0).getId(), admin.getToken(), 4, 1, "'+'date");
        Assert.isTrue(runDetailsPage2.isPresent());
        Assert.isTrue(runDetailsPage2.get().getContent().size() == 2);
        final Optional<PageDTO> runDetailsPage3 = this.appClient.getRunDetailsByUserIdPaginated(users.get(0).getId(), admin.getToken(), 4, 2, "'+'date");
        Assert.isTrue(runDetailsPage3.isPresent());
        Assert.isTrue(runDetailsPage3.get().getContent().isEmpty());
    }

    public void getRunDetailsBasedOnQuery() {

        final String query = "{$and: [{date:{ $gt: '2020-05-29'}}, {$or: [{speed: {$eq: 180.0}}, {distance: {$eq: 130.0}}]}]}";
        final Optional<PageDTO> runDetailsPage = this.appClient.getRunDetailsByQueryStringPaginated(query, admin.getToken(), 4, 0, "'+'date");
        Assert.isTrue(runDetailsPage.isPresent());

        final Optional<PageDTO> runDetailsUserPage = this.appClient.getRunDetailsByQueryStringPaginated(query, users.get(0).getToken(), 4, 0, "'+'date");
        Assert.isTrue(runDetailsUserPage.isPresent());
        Assert.isTrue(runDetailsUserPage.get().getContent().stream().allMatch(i -> i.getUserId().equals(users.get(0).getId())));

        final Optional<PageDTO> runDetailsMgrPage = this.appClient.getRunDetailsByQueryStringPaginated(query, manager.getToken(), 4, 0, "'+'date");
        Assert.isTrue(runDetailsMgrPage.isPresent());
        Assert.isTrue(runDetailsMgrPage.get().getContent().isEmpty());
    }
}
