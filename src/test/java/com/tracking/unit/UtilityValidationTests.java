package com.tracking.unit;

import com.tracking.data.Location;
import com.tracking.data.TokenProperties;
import com.tracking.domain.RunDetails;
import com.tracking.dto.RunDetailsDTO;
import com.tracking.enums.UserRoles;
import com.tracking.utils.DataValidator;
import com.tracking.utils.EncryptionUtil;
import com.tracking.utils.Mapper;
import com.tracking.utils.TokenGenerator;
import com.tracking.weather.dto.DailyWeatherDTO;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
class UtilityValidationTests {

    @Test
    void testTokenGenerator() {
        final TokenProperties tokenProps = new TokenProperties();
        tokenProps.setRoleCode(UserRoles.ADMIN);
        tokenProps.setUserId("0013");

        final String token = TokenGenerator.generateToken(tokenProps);
        Assert.isTrue(TokenGenerator.validateAuthToken(token), "Token validation failed!");

        final Optional<TokenProperties> decrypted = TokenGenerator.fetchAllCredentialsFromToken(token);
        Assert.isTrue(decrypted.isPresent(), "Fetching the properties failed!");

        Assert.isTrue(tokenProps.equals(decrypted.get()), "Properties should be the same!");
    }

    @Test
    void testEmailValidation() {
        String email = "m@m.com";
        Assert.isTrue(DataValidator.isValidEmail(email), "Email form is valid!");

        email = "martin.ayvazyan@gmail.com";
        Assert.isTrue(DataValidator.isValidEmail(email), "Email form is valid!");

        email = "12@gmail.com";
        Assert.isTrue(DataValidator.isValidEmail(email), "Email form is valid!");

        email = "12@gmail.sx.com";
        Assert.isTrue(DataValidator.isValidEmail(email), "Email form is valid!");

        email = "0013";
        Assert.isTrue(!DataValidator.isValidEmail(email), "Email form is invalid!");

        email = "m@12";
        Assert.isTrue(!DataValidator.isValidEmail(email), "Email form is invalid!");

        email = "12@12";
        Assert.isTrue(!DataValidator.isValidEmail(email), "Email form is invalid!");

        email = "";
        Assert.isTrue(!DataValidator.isValidEmail(email), "Email form is invalid!");
    }

    @Test
    void testPassEncryption() {

        final String pass1 = "MY_PASS_123_32";
        final Optional<String> encrypt1 = EncryptionUtil.hashKey(pass1);

        Assert.isTrue(encrypt1.isPresent(), "The encrypted data should be present!");
        Assert.isTrue(encrypt1.get().equals(EncryptionUtil.hashKey(pass1).get()), "On the same input, the encryption should be teh same!");
    }

    @Test
    void testMappers() {

        final RunDetailsDTO dto = new RunDetailsDTO();
        dto.setId("MA13");
        dto.setDate(LocalDate.now());
        dto.setDistance(13.);
        dto.setLocation(new Location(44.5, 43.5));
        dto.setTime(LocalTime.now());
        dto.setUserId("User-ID");
        dto.setWeather(new DailyWeatherDTO());

        final RunDetails data = Mapper.convertToEntity(dto);
        final RunDetailsDTO dto2 = Mapper.convertToDTO(data);
        Assert.isTrue(dto2.equals(dto), "Mapping of DTOs to enities");
    }
}
