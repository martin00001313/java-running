package com.tracking.utils;

import com.tracking.domain.RunDetails;
import com.tracking.domain.RunReport;
import com.tracking.domain.User;
import com.tracking.dto.NewRunDetailsDTO;
import com.tracking.dto.PageDTO;
import com.tracking.dto.RunDetailsDTO;
import com.tracking.dto.RunReportDTO;
import com.tracking.dto.UserDTO;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

/**
 * Mapper functionality
 *
 * @author martin
 */
public class Mapper {

    /**
     * DTO to entity converter
     *
     * @param dto
     * @return Generated entity
     */
    public static RunDetails convertToEntity(final RunDetailsDTO dto) {

        final RunDetails data = new RunDetails();
        data.setId(dto.getId());
        data.setDate(dto.getDate());
        data.setLocation(dto.getLocation());
        data.setDistance(dto.getDistance());
        data.setUserId(dto.getUserId());
        data.setWeather(dto.getWeather());

        return data;
    }

    /**
     * Entity to DTO mapper
     *
     * @param data
     * @return
     */
    public static RunDetailsDTO convertToDTO(final RunDetails data) {

        final RunDetailsDTO dto = new RunDetailsDTO();
        dto.setId(data.getId());
        dto.setDate(data.getDate());
        dto.setLocation(data.getLocation());
        dto.setDistance(data.getDistance());
        dto.setUserId(data.getUserId());
        dto.setWeather(data.getWeather());
        dto.setSpeed(data.getSpeed());

        return dto;
    }

    /**
     * DTO to entity converter
     *
     * @param dto
     * @return Generated entity
     */
    public static RunDetails convertToEntity(final NewRunDetailsDTO dto) {

        final RunDetails data = new RunDetails();
        data.setId(null);
        data.setDate(dto.getDate());
        data.setLocation(dto.getLocation());
        data.setDistance(dto.getDistance());
        data.setUserId(dto.getUserId());
        data.setSpeed(dto.getSpeed());
        data.setWeather(null);

        return data;
    }

    /**
     * Convert user data to DTO
     *
     * @param data
     * @return The generated DTO
     */
    public static UserDTO convertToDTO(final User data) {

        final UserDTO userDTO = new UserDTO();
        userDTO.setAdminId(data.getAdminId());
        userDTO.setDot(data.getDot());
        userDTO.setEmail(data.getEmail());
        userDTO.setFirstName(data.getFirstName());
        userDTO.setLastName(data.getLastName());
        userDTO.setId(data.getId());
        userDTO.setRole(data.getRole());
        userDTO.setToken(null);

        return userDTO;
    }

    /**
     * Create DTO object from page data
     *
     * @param page
     * @return The generated DTO
     */
    public static PageDTO convertToDTO(final Page<RunDetails> page) {

        final PageDTO dto = new PageDTO();
        dto.setContent(page.getContent().stream().map(d -> Mapper.convertToDTO(d)).collect(Collectors.toList()));
        dto.setNumber(page.getNumber());
        dto.setNumberOfElements(page.getNumberOfElements());
        dto.setSize(page.getSize());
        dto.setTotalElements(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());

        return dto;
    }

    /**
     * RunReport to DTO converter
     *
     * @param data
     * @return Generated DTO object
     */
    public static RunReportDTO convertToDTO(final RunReport data) {

        final RunReportDTO dto = new RunReportDTO();
        dto.setId(data.getId());
        dto.setUserId(data.getUserId());
        dto.setAvgDistance(data.getAvgDistance());
        dto.setAvgSpeed(data.getAvgSpeed());
        dto.setEndDate(data.getEndDate());

        return dto;
    }

    /**
     * Create DTO object from page data
     *
     * @param page
     * @return The generated DTO
     */
    public static PageDTO convertToReportDTO(final Page<RunReport> page) {

        final PageDTO dto = new PageDTO();
        dto.setContent(page.getContent().stream().map(d -> Mapper.convertToDTO(d)).collect(Collectors.toList()));
        dto.setNumber(page.getNumber());
        dto.setNumberOfElements(page.getNumberOfElements());
        dto.setSize(page.getSize());
        dto.setTotalElements(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());

        return dto;
    }

    /**
     * Create DTO object from page data
     *
     * @param page
     * @return The generated DTO
     */
    public static PageDTO convertToUsersPageDTO(final Page<User> page) {

        final PageDTO dto = new PageDTO();
        dto.setContent(page.getContent().stream().map(d -> Mapper.convertToDTO(d)).collect(Collectors.toList()));
        dto.setNumber(page.getNumber());
        dto.setNumberOfElements(page.getNumberOfElements());
        dto.setSize(page.getSize());
        dto.setTotalElements(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());

        return dto;
    }
}
