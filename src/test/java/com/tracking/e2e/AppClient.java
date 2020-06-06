package com.tracking.e2e;

import com.tracking.e2e.dto.NewRunDetailsDTO;
import com.tracking.e2e.dto.NewUserDTO;
import com.tracking.e2e.dto.PageDTO;
import com.tracking.e2e.dto.RunDetailsDTO;
import com.tracking.e2e.dto.UserDTO;
import com.tracking.weather.dto.DailyWeatherDTO;
import io.vavr.control.Try;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author martin
 */
@Component
public class AppClient {

    private final String BASE_URL = "http://127.0.0.1:8080/api/v1";

    @Autowired
    private RestTemplate restTemplate;

    public Optional<DailyWeatherDTO> testConnection() {

        final Try<ResponseEntity<DailyWeatherDTO>> response = Try.of(() -> this.restTemplate.exchange(BASE_URL + "/tests/test3",
                HttpMethod.GET, HttpEntity.EMPTY, DailyWeatherDTO.class));
        if (response.isFailure()) {
            System.err.println("The operation has been failed: " + response.getCause().getLocalizedMessage());
            return Optional.empty();
        }
        System.out.println("MA13: " + response.get().getBody().getWeather().get(0).getDescription());
        return Optional.of(response.get().getBody());
    }

    public Optional<UserDTO> regsterAdmin(final NewUserDTO newUser) {

        final Try<ResponseEntity<UserDTO>> response = Try.of(() -> this.restTemplate.exchange(BASE_URL + "/auth/register/admin",
                HttpMethod.POST, new HttpEntity<>(newUser), UserDTO.class));
        if (response.isFailure()) {
            System.err.println("Registration failed: " + response.getCause().getLocalizedMessage());
            return Optional.empty();
        }
        return Optional.of(response.get().getBody());
    }

    public Optional<UserDTO> regsterUser(final NewUserDTO newUser, final String token) {

        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL + "/auth/register/user").queryParam("token", token);

        final Try<ResponseEntity<UserDTO>> response = Try.of(() -> this.restTemplate.exchange(builder.build().toUri(),
                HttpMethod.POST, new HttpEntity<>(newUser), UserDTO.class));

        if (response.isFailure()) {
            System.err.println("Registration failed: " + response.getCause().getLocalizedMessage());
            return Optional.empty();
        }
        return Optional.of(response.get().getBody());
    }

    public Optional<UserDTO> loginUser(final String email, final String dot, final String pass) {

        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL + "/auth/login")
                .queryParam("email", email)
                .queryParam("dot", dot)
                .queryParam("password", pass);
        final Try<ResponseEntity<UserDTO>> response = Try.of(() -> this.restTemplate.exchange(builder.build().toUri(),
                HttpMethod.GET, HttpEntity.EMPTY, UserDTO.class));

        if (response.isFailure()) {
            System.err.println("Login failed: " + response.getCause().getLocalizedMessage());
            return Optional.empty();
        }
        return Optional.of(response.get().getBody());
    }

    public Optional<RunDetailsDTO> createRunData(final NewRunDetailsDTO runDetails, final String token) {

        System.out.println(runDetails.toString());
        final UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL + "/run/details/create").queryParam("token", token);

        final Try<ResponseEntity<RunDetailsDTO>> response = Try.of(() -> this.restTemplate.exchange(builder.build().toUri(),
                HttpMethod.POST, new HttpEntity<>(runDetails), RunDetailsDTO.class));
        if (response.isFailure()) {
            System.err.println("Run details generation failed: " + response.getCause().getLocalizedMessage());
            return Optional.empty();
        }
        return Optional.of(response.get().getBody());
    }

    public Optional<RunDetailsDTO> updateRunData(final RunDetailsDTO runDetails, final String token) {

        System.out.println(runDetails.toString());

        final UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL + "/run/details/update").queryParam("token", token);

        final Try<ResponseEntity<RunDetailsDTO>> response = Try.of(() -> this.restTemplate.exchange(builder.build().toUri(),
                HttpMethod.PUT, new HttpEntity<>(runDetails), RunDetailsDTO.class));
        if (response.isFailure()) {
            System.err.println("Run details update failed: " + response.getCause().getLocalizedMessage());
            return Optional.empty();
        }
        return Optional.of(response.get().getBody());
    }

    public List<RunDetailsDTO> getRunDetailsByUserId(final String userId, final String token) {

        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL + "/run/details/by-user-id/all")
                .queryParam("userId", userId)
                .queryParam("token", token);

        final Try<ResponseEntity<RunDetailsDTO[]>> response = Try.of(() -> this.restTemplate.exchange(builder.build().toUri(),
                HttpMethod.GET, HttpEntity.EMPTY, RunDetailsDTO[].class));
        if (response.isFailure()) {
            System.err.println("Run details generation failed: " + response.getCause().getLocalizedMessage());
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(response.get().getBody());
    }

    public Optional<PageDTO> getRunDetailsByUserIdPaginated(final String userId, final String token,
            final int pageSize, final int pageNumber, final String sortingOptions) {

        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL + "/run/details/by-user-id/paginated")
                .queryParam("userId", userId)
                .queryParam("token", token)
                .queryParam("sorting", sortingOptions)
                .queryParam("pageSize", pageSize)
                .queryParam("pageNumber", pageNumber);

        final Try<ResponseEntity<PageDTO>> response = Try.of(() -> this.restTemplate.exchange(builder.build().toUri(),
                HttpMethod.GET, HttpEntity.EMPTY, PageDTO.class));
        if (response.isFailure()) {
            System.err.println("Run details generation failed: " + response.getCause().getLocalizedMessage());
            return Optional.empty();
        }
        return Optional.of(response.get().getBody());
    }

    public Optional<PageDTO> getRunDetailsByQueryStringPaginated(final String queryJson, final String token,
            final int pageSize, final int pageNumber, final String sortingOptions) {

        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL + "/run/details/by-json-query/paginated")
                .queryParam("token", token)
                .queryParam("sorting", sortingOptions)
                .queryParam("pageSize", pageSize)
                .queryParam("pageNumber", pageNumber)
                .queryParam("queryJson", queryJson);
        final UriComponents uriComponents = builder.build().encode();

        final Try<ResponseEntity<PageDTO>> response = Try.of(() -> this.restTemplate.exchange(uriComponents.toUri(),
                HttpMethod.GET, HttpEntity.EMPTY, PageDTO.class));

        if (response.isFailure()) {
            System.err.println("Run details generation failed: " + response.getCause().getLocalizedMessage());
            return Optional.empty();
        }
            System.err.println("Run details generation failed: " + response.get().getBody().toString());
        return Optional.of(response.get().getBody());
    }
}
