package com.tracking.services;

import com.tracking.data.TokenProperties;
import com.tracking.domain.User;
import com.tracking.dto.NewUserDTO;
import com.tracking.dto.UserDTO;
import com.tracking.enums.UserRoles;
import com.tracking.repositories.UserRepository;
import com.tracking.utils.DataValidator;
import com.tracking.utils.EncryptionUtil;
import com.tracking.utils.TokenGenerator;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author martin
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Login functionality
     *
     * @param email
     * @param dot
     * @param password
     * @return Properties of the user data.
     */
    @Transactional
    public Optional<UserDTO> login(final String email, final String dot, final String password) {

        final Optional<String> encryptedPass = EncryptionUtil.hashKey(password);
        if (!encryptedPass.isPresent()) {
            System.err.println("Encryption failed!");
            return Optional.empty();
        }

        final Optional<User> user = this.userRepository.findOneByDotAndEmailAndEncryptedPassword(dot, email, encryptedPass.get());
        if (!user.isPresent()) {
            System.err.println("Data has not been founded");
            return Optional.empty();
        }

        final TokenProperties tokenProperties = new TokenProperties(user.get().getId(), user.get().getRole(), user.get().getAdminId());
        final String token = TokenGenerator.generateToken(tokenProperties);

        final UserDTO userDTO = new UserDTO(tokenProperties.getUserId(), user.get().getFirstName(), user.get().getLastName(),
                user.get().getDot(), user.get().getEmail(), user.get().getRole(), token, user.get().getAdminId());

        return Optional.of(userDTO);
    }

    /**
     * Register a new user.
     *
     * @param user
     * @param token
     * @return The registered user details.
     */
    @Transactional
    public Optional<UserDTO> register(final NewUserDTO user, final String token) {

        if (!this.vaidateRegisteredData(user, token)) {
            return Optional.empty();
        }

        final Optional<String> encryptedPass = EncryptionUtil.hashKey(user.getPassword());
        if (!encryptedPass.isPresent()) {
            return Optional.empty();
        }

        User newUser = new User(null, user.getFirstName(), user.getLastName(),
                user.getDot(), user.getEmail(), encryptedPass.get(), user.getRole(), user.getAdminId());
        newUser = this.userRepository.save(newUser);

        // Update adminId field for registration of ADMIN
        if (newUser.getRole() == UserRoles.ADMIN) {
            newUser.setAdminId(newUser.getId());
            newUser = this.userRepository.save(newUser);
        }

        final TokenProperties tokenProperties = new TokenProperties(newUser.getId(), newUser.getRole(), newUser.getAdminId());

        final String genToken = TokenGenerator.generateToken(tokenProperties);

        final UserDTO userDTO = new UserDTO(tokenProperties.getUserId(), newUser.getFirstName(), newUser.getLastName(),
                newUser.getDot(), newUser.getEmail(), newUser.getRole(), genToken, newUser.getAdminId());

        return Optional.of(userDTO);
    }

    /**
     * Validate provided token.
     *
     * @param token
     * @return The state of the validation
     */
    public boolean validateAuthToken(final String token) {
        return TokenGenerator.validateAuthToken(token);
    }

    public boolean vaidateRegisteredData(final NewUserDTO user, final String token) {

        if (user.getDot() == null || !DataValidator.isValidEmail(user.getEmail())
                || this.userRepository.findOneByEmail(user.getEmail()).isPresent()
                || user.getFirstName() == null || user.getLastName() == null || user.getPassword() == null
                || user.getPassword().length() < 6 || user.getRole() == null) {
            return false;
        }

        if (user.getRole() == UserRoles.ADMIN && token != null) {
            return false;
        } else if (user.getRole() == UserRoles.ADMIN) {
            user.setAdminId(null);
            return true;
        }

        final Optional<TokenProperties> tokenData = TokenGenerator.fetchAllCredentialsFromToken(token);
        if (!tokenData.isPresent()) {
            return false;
        } else if (tokenData.get().getRoleCode() == UserRoles.REGULAR) {
            return false;
        }

        return user.getAdminId().equals(tokenData.get().getAdminId());
    }
}
