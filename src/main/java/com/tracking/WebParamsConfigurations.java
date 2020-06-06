package com.tracking;

import com.tracking.formatters.LocalDateFormatter;
import com.tracking.formatters.LocalTimeFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Apply defined configurations for LocalDate, LocalTime objects
 *
 * @author martin
 */
@Configuration
public class WebParamsConfigurations implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new LocalDateFormatter());
        registry.addFormatter(new LocalTimeFormatter());
    }
}
