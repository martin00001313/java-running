package com.tracking.services;

import com.tracking.data.TokenProperties;
import com.tracking.domain.RunDetails;
import com.tracking.domain.User;
import com.tracking.dto.NewRunDetailsDTO;
import com.tracking.dto.RunDetailsDTO;
import com.tracking.enums.ActionTypes;
import com.tracking.repositories.RunDetailsRepository;
import com.tracking.utils.ActionValidation;
import com.tracking.utils.Mapper;
import com.tracking.utils.PageRequestBuilder;
import com.tracking.utils.TokenGenerator;
import com.tracking.weather.dto.DailyWeatherDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A service to provide interfaces on the operation done on run details
 *
 * @author martin
 */
@Service
public class RunService {

    @Autowired
    private ActionValidation actionValidation;

    @Autowired
    private RunDetailsRepository runDetailsRepository;

    @Autowired
    private RunReportService runReportService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private WeatherService weatherService;

    /**
     * Create a run entity
     *
     * @param data
     * @param token
     * @return The generated entity details
     */
    @Transactional
    public Optional<RunDetails> create(final NewRunDetailsDTO data, final String token) {

        if (!this.validate(data, token)) {
            return Optional.empty();
        }

        final RunDetails newUser = Mapper.convertToEntity(data);

        final Optional<DailyWeatherDTO> weather = this.weatherService.getWeather(data.getDate(), data.getLocation());
        if (weather.isPresent()) {
            newUser.setWeather(weather.get());
        }

        final RunDetails details = this.runDetailsRepository.save(newUser);
        return Optional.of(details);
    }

    /**
     * Update the run entity
     *
     * @param runDetailsDTO
     * @param token
     * @return The generated entity details
     */
    @Transactional
    public Optional<RunDetails> update(final RunDetailsDTO runDetailsDTO, final String token) {

        if (!this.validate(runDetailsDTO, token)) {
            return Optional.empty();
        }

        final Optional<RunDetails> curData = this.runDetailsRepository.findById(runDetailsDTO.getId());
        if (!curData.isPresent()) {
            return Optional.empty();
        }

        final RunDetails newData = Mapper.convertToEntity(runDetailsDTO);
        final Optional<DailyWeatherDTO> weather = this.weatherService.getWeather(newData.getDate(), newData.getLocation());
        if (weather.isPresent()) {
            newData.setWeather(weather.get());
        }

        final RunDetails details = this.runDetailsRepository.save(newData);
        return Optional.of(details);
    }

    /**
     * Get all run details by user ID.
     *
     * @param userId
     * @return List of run details having the provided user ID
     */
    public List<RunDetails> getAllDetailsByUserId(final String userId) {
        return this.runDetailsRepository.findByUserId(userId);
    }

    /**
     * Validate data
     *
     * @param data
     * @param token
     * @return The state of the validation
     */
    private boolean validate(final NewRunDetailsDTO data, final String token) {

        if (!(data != null && data.getLocation() != null && data.getLocation().getLat() != null
                && data.getLocation().getLng() != null && !data.getDate().isBefore(LocalDate.now())
                && data.getDistance() != null && data.getSpeed() != null)) {
            return false;
        }

        final Optional<TokenProperties> tokenProps = TokenGenerator.fetchAllCredentialsFromToken(token);
        if (!tokenProps.isPresent()) {
            return false;
        }

        final Optional<User> userData = this.userService.getById(data.getUserId());
        if (!userData.isPresent()) {
            return false;
        }

        return tokenProps.get().getAdminId().equals(tokenProps.get().getAdminId());
    }

    /**
     * Return paginated version of run details for the specified user by
     * building appropriate page request.
     *
     * @param userId
     * @param pageSize
     * @param pageNumber
     * @param sortingCriteria String with comma separated fields
     * @return The appropriate page of the received details
     */
    public Page<RunDetails> getDetailsByUserIdPaginated(final String userId, final Integer pageSize, final Integer pageNumber, final String sortingCriteria) {

        final PageRequest pageRequest = PageRequestBuilder.getPageRequest(pageSize, pageNumber, sortingCriteria);
        return this.getDetailsByUserIdPaginated(userId, pageRequest);
    }

    /**
     * Return paginated version of run details for the specified user.
     *
     * @param userId
     * @param pageRequest The page request details
     * @return The appropriate page of the received details
     */
    private Page<RunDetails> getDetailsByUserIdPaginated(final String userId, final PageRequest pageRequest) {

        final Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));

        return new PageImpl<>(
                mongoTemplate.find(query.with(pageRequest), RunDetails.class),
                pageRequest,
                mongoTemplate.count(query, RunDetails.class));
    }

    @Transactional
    public void deleteByUserId(final List<String> userIds) {
        this.runReportService.deleteByUserId(userIds);
        this.runDetailsRepository.deleteByUserIdIn(userIds);
    }

    /**
     * Return paginated version of run details for the specified user by
     * building appropriate page request.
     *
     * @param queryJson
     * @param pageSize
     * @param pageNumber
     * @param sortingCriteria String with comma separated fields
     * @param token
     * @return The appropriate page of the received details
     */
    public Page<RunDetails> getDetailsByQueryJsonPaginated(final String queryJson, final Integer pageSize,
            final Integer pageNumber, final String sortingCriteria, final String token) {

        final PageRequest pageRequest = PageRequestBuilder.getPageRequest(pageSize, pageNumber, sortingCriteria);
        return this.getDetailsByQueryJsonPaginated(queryJson, pageRequest, token);
    }

    /**
     * Return paginated version of run details for the specified user.
     *
     * @param userId
     * @param pageRequest The page request details
     * @param token
     * @return The appropriate page of the received details
     */
    private Page<RunDetails> getDetailsByQueryJsonPaginated(final String queryJson, final PageRequest pageRequest, final String token) {

        final BasicQuery query = new BasicQuery(queryJson);
        query.addCriteria(this.actionValidation.generateCriteria(token, ActionTypes.CRUD_RUN));

        return new PageImpl<>(
                mongoTemplate.find(query.with(pageRequest), RunDetails.class),
                pageRequest,
                mongoTemplate.count(query, RunDetails.class));
    }
}
