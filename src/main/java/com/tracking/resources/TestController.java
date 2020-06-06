package com.tracking.resources;

import com.tracking.clients.WeatherClient;
import com.tracking.data.Location;
import com.tracking.data.TokenProperties;
import com.tracking.domain.RunDetails;
import com.tracking.domain.User;
import com.tracking.dto.NewRunDetailsDTO;
import com.tracking.dto.NewUserDTO;
import com.tracking.dto.UserDTO;
import com.tracking.enums.UserRoles;
import com.tracking.repositories.UserRepository;
import com.tracking.services.AuthService;
import com.tracking.services.RunService;
import com.tracking.services.WeatherService;
import com.tracking.utils.TokenGenerator;
import com.tracking.weather.dto.DailyWeatherDTO;
import com.tracking.weather.dto.WeatherResponseDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * APIs to generate/update/get run details
 *
 * @author martin
 */
@RestController
@RequestMapping("/api/v1/tests")
public class TestController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RunService runService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherClient weatherClient;

    @Autowired
    private WeatherService weatherService;

    @GetMapping(path = "/test1")
    public ResponseEntity<?> test1() {
        final Location location = new Location(40.1872, 44.5152);
        final Optional<WeatherResponseDTO> dto = this.weatherClient.getWeatherForecast(location);
        if (!dto.isPresent()) {
            return ResponseEntity.ok("MA13-error");
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping(path = "/test2")
    public ResponseEntity<?> test2() {
        final NewUserDTO user = new NewUserDTO("Martin", "Ayvazyan", "0013", "martin.ayvazyan.dev@gmail.com", UserRoles.ADMIN, "001313", null);
        final Optional<UserDTO> registeredUser = this.authService.register(user, null);
        if (!registeredUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body("Error-MA13");
        }
        return ResponseEntity.ok(registeredUser.get());
    }

    @GetMapping(path = "/test3")
    public ResponseEntity<?> test3() {

        final LocalDate date = LocalDate.now().plusDays(2);
        final Location location = new Location(40.1872, 44.5152);
        final Optional<DailyWeatherDTO> daily = this.weatherService.getWeather(date, location);
        if (!daily.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body("Error-MA15");
        }
        return ResponseEntity.ok(daily.get());
    }

    @GetMapping(path = "/test4")
    public ResponseEntity<?> test4() {

        final List<User> users = this.userRepository.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.ok("The users list is empty!");
        }
        final User user = users.get(0);
        final NewRunDetailsDTO dto = new NewRunDetailsDTO();
        dto.setDate(LocalDate.now());
        dto.setLocation(new Location(40.1872, 44.5152));
        dto.setDistance(1000.);
        dto.setUserId(users.get(0).getId());

        final String token = TokenGenerator.generateToken(new TokenProperties(user.getId(), user.getRole(), user.getAdminId()));
        final Optional<RunDetails> data = this.runService.create(dto, token);
        if (!data.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body("Error-MA15");
        }
        return ResponseEntity.ok(data.get());
    }
}
