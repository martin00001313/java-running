package com.tracking.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 *
 * @author martin
 */
public class PageRequestBuilder {

    /**
     * Constructs PageRequest
     *
     * @param pageSize
     * @param pageNumber
     * @param sortingCriteria
     * @return The generated page request object
     */
    public static PageRequest getPageRequest(final int pageSize, final int pageNumber, final String sortingCriteria) {

        final List<String> fields = sortingCriteria.isEmpty() ? Collections.EMPTY_LIST : Arrays.asList(sortingCriteria.split(",")).stream().distinct().collect(Collectors.toList());

        final List<Sort.Order> sortingOrders = fields.stream().map(f -> f.startsWith("+")
                ? new Sort.Order(Sort.Direction.ASC, f.substring(1)) : new Sort.Order(Sort.Direction.DESC, f.substring(1)))
                .collect(Collectors.toList());

        final Sort sort = sortingOrders.isEmpty() ? Sort.unsorted() : Sort.by(sortingOrders);

        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
