package com.tracking.unit;

import com.tracking.domain.User;
import com.tracking.dto.NewUserDTO;
import com.tracking.dto.UserDTO;
import com.tracking.enums.UserRoles;
import com.tracking.repositories.UserRepository;
import com.tracking.services.AuthService;
import io.jsonwebtoken.lang.Assert;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author martin
 */
@SpringBootTest
public class AuthServicesValidationTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testUserRegistration() {

        final String email = "m@mail.ru";
        final String dot = "0013";
        final String pass = "my_pass13";

        Optional<User> user = this.userRepository.findOneByEmail(email);
        if (user.isPresent()) {
            this.userRepository.deleteById(user.get().getId());
        }

        Optional<UserDTO> createdUser = this.authService.register(new NewUserDTO("Martin", "Ayvazyan", dot, "invalid.email", UserRoles.ADMIN, pass, null), null);
        Assert.isTrue(!createdUser.isPresent(), "It's shouldn't be possible register with invalid email!");

        createdUser = this.authService.register(new NewUserDTO("Martin", "Ayvazyan", dot, email, UserRoles.ADMIN, pass, null), null);
        Assert.isTrue(createdUser.isPresent(), "The user should be created!");

        createdUser = this.authService.register(new NewUserDTO("M", "A", "", email, UserRoles.REGULAR, "-", createdUser.get().getId()), createdUser.get().getToken());
        Assert.isTrue(!createdUser.isPresent(), "A new user should not be created as the email is not unique!");
    }

    @Test
    void testRegularUserRegistration() {

        final String email = "m@mail.ru";
        final String mgrEmail = "mgr@mail.ru";
        final String regEmail = "rg@mail.ru";
        final String dot = "0013";
        final String pass = "my_pass13";

        Optional<User> user = this.userRepository.findOneByEmail(email);
        if (user.isPresent()) {
            this.userRepository.deleteById(user.get().getId());
        }

        final Optional<UserDTO> createdAdmin = this.authService.register(new NewUserDTO("Martin", "Ayvazyan", dot, email, UserRoles.ADMIN, pass, null), null);
        Assert.isTrue(createdAdmin.isPresent(), "The user should be created!");

        Optional<User> mgruser = this.userRepository.findOneByEmail(mgrEmail);
        if (mgruser.isPresent()) {
            this.userRepository.deleteById(mgruser.get().getId());
        }
        final Optional<UserDTO> createdManager = this.authService.register(new NewUserDTO("M1", "A1", "DOT1", mgrEmail, UserRoles.MANAGER, "my_pass1", createdAdmin.get().getId()), createdAdmin.get().getToken());
        Assert.isTrue(createdManager.isPresent(), "A new manager should be created!");

        Optional<User> regUser = this.userRepository.findOneByEmail(regEmail);
        if (regUser.isPresent()) {
            this.userRepository.deleteById(regUser.get().getId());
        }
        final Optional<UserDTO> createdRegular = this.authService.register(new NewUserDTO("M2", "A2", "DOT1", regEmail, UserRoles.REGULAR, "my_pass2", createdAdmin.get().getId()), createdAdmin.get().getToken());
        Assert.isTrue(createdRegular.isPresent(), "A new regular user should be created!");

        Assert.isTrue(createdRegular.get().getAdminId().equals(createdAdmin.get().getId()));
        Assert.isTrue(createdManager.get().getAdminId().equals(createdAdmin.get().getId()));
        Assert.isTrue(createdAdmin.get().getAdminId().equals(createdAdmin.get().getId()));
    }

    @Test
    void testLogin() {

        final String email = "m2@mail.ru";
        final String dot = "0013";
        final String pass = "my_pass001";

        Optional<User> user = this.userRepository.findOneByEmail(email);
        if (user.isPresent()) {
            this.userRepository.deleteById(user.get().getId());
        }

        Optional<UserDTO> createdUser = this.authService.register(new NewUserDTO("Martin", "Ayvazyan", dot, email, UserRoles.ADMIN, pass, null), null);
        Assert.isTrue(createdUser.isPresent(), "The user should be created successfully!");

        Optional<UserDTO> loginDetails = this.authService.login(email, dot, pass);
        Assert.isTrue(loginDetails.isPresent(), "Login should be successed!");

        loginDetails = this.authService.login(email, dot, pass + "+");
        Assert.isTrue(!loginDetails.isPresent(), "Login should be failed!");
    }
}
