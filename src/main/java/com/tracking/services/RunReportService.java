package com.tracking.services;

import com.tracking.domain.RunDetails;
import com.tracking.domain.RunReport;
import com.tracking.enums.ActionTypes;
import com.tracking.repositories.RunDetailsRepository;
import com.tracking.repositories.RunReportRepository;
import com.tracking.utils.ActionValidation;
import com.tracking.utils.PageRequestBuilder;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
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
 * A service to manage report data preparation
 *
 * @author martin
 */
@Service
public class RunReportService {

    @Autowired
    private ActionValidation actionValidation;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RunDetailsRepository runDetalsRepository;

    @Autowired
    private RunReportRepository runReportRepository;

    /**
     * Generate report based on the provided run details, grouping by user. The
     * gap of the report is a week
     */
    @Transactional
    public void generate() {

        final LocalDate end = LocalDate.now();
        final LocalDate start = end.minusDays(7);

        final List<RunReport> reportData = this.runDetalsRepository.findAll().stream().filter(r
                -> r.getDate().isAfter(start) && r.getDate().isBefore(end)).collect(Collectors.groupingBy(RunDetails::getId)).entrySet().stream().map(r -> {
            final RunReport report = new RunReport();
            report.setUserId(r.getKey());
            report.setAvgDistance(r.getValue().stream().mapToDouble(RunDetails::getDistance).average().getAsDouble());
            report.setAvgSpeed(r.getValue().stream().mapToDouble(RunDetails::getSpeed).average().getAsDouble());
            report.setEndDate(end);
            return report;
        }).collect(Collectors.toList());

        this.runReportRepository.saveAll(reportData);
    }

    /**
     * Get all reports
     *
     * @return The list of generated reports
     */
    public List<RunReport> getAllReports() {
        return this.runReportRepository.findAll();
    }

    /**
     * Return paginated version of report data for the by building appropriate
     * page request.
     *
     * @param pageSize
     * @param pageNumber
     * @param sortingCriteria String with comma separated fields
     * @return The appropriate page of the received reports
     */
    public Page<RunReport> getDetailsPaginated(final Integer pageSize, final Integer pageNumber, final String sortingCriteria) {

        final PageRequest pageRequest = PageRequestBuilder.getPageRequest(pageSize, pageNumber, sortingCriteria);
        return this.getReportsPaginated(pageRequest, null);
    }

    /**
     * Return paginated version of report data for the by building appropriate
     * page request.
     *
     * @param userId
     * @param pageSize
     * @param pageNumber
     * @param sortingCriteria String with comma separated fields
     * @return The appropriate page of the received reports
     */
    public Page<RunReport> getDetailsPaginatedByUserId(final String userId, final Integer pageSize, final Integer pageNumber, final String sortingCriteria) {

        final PageRequest pageRequest = PageRequestBuilder.getPageRequest(pageSize, pageNumber, sortingCriteria);
        return this.getReportsPaginated(pageRequest, userId);
    }

    /**
     * Return paginated version of reports.
     *
     * @param pageRequest The page request details
     * @param userId
     * @return The appropriate page of the received reports
     */
    private Page<RunReport> getReportsPaginated(final PageRequest pageRequest, final String userId) {

        final Query query = new Query();
        if (userId != null) {
            query.addCriteria(Criteria.where("userId").is(userId));
        }

        return new PageImpl<>(
                mongoTemplate.find(query.with(pageRequest), RunReport.class),
                pageRequest,
                mongoTemplate.count(query, RunReport.class));
    }

    /**
     * Return paginated version of report data for the by building appropriate
     * page request.
     *
     * @param queryJson
     * @param pageSize
     * @param pageNumber
     * @param sortingCriteria String with comma separated fields
     * @param token
     * @return The appropriate page of the received reports
     */
    public Page<RunReport> getDetailsPaginatedByQString(final String queryJson, final Integer pageSize,
            final Integer pageNumber, final String sortingCriteria, final String token) {

        final PageRequest pageRequest = PageRequestBuilder.getPageRequest(pageSize, pageNumber, sortingCriteria);
        return this.getReportsPaginatedQString(pageRequest, queryJson, token);
    }

    /**
     * Return paginated version of reports.
     *
     * @param pageRequest The page request details
     * @param queryJson The JSON query
     * @return The appropriate page of the received reports
     */
    private Page<RunReport> getReportsPaginatedQString(final PageRequest pageRequest, final String queryJson, final String token) {

        final BasicQuery query = new BasicQuery(queryJson);
        query.addCriteria(this.actionValidation.generateCriteria(token, ActionTypes.READ_REPORT));

        return new PageImpl<>(
                mongoTemplate.find(query.with(pageRequest), RunReport.class),
                pageRequest,
                mongoTemplate.count(query, RunReport.class));
    }

    /**
     * Get all reports by user ID
     *
     * @param userId
     * @return The list of generated reports
     */
    public List<RunReport> getAllReportsByUserId(final String userId) {
        return this.runReportRepository.findByUserId(userId);
    }

    /**
     * Delete all reports by the user ID
     *
     * @param userIds
     */
    @Transactional
    public void deleteByUserId(final List<String> userIds) {
        this.runReportRepository.deleteByUserIdIn(userIds);
    }
}
