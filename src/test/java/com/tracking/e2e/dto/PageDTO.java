package com.tracking.e2e.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to represent page details
 *
 * @author martin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageDTO {

    private int totalPages;

    private long totalElements;

    private int numberOfElements;

    private int size;

    private int number;

    private List<RunDetailsDTO> content;
}
