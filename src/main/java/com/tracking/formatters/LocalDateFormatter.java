package com.tracking.formatters;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.format.Formatter;

/**
 * LocalDate <-> String formatter for REST APIs
 *
 * @author martin
 * @example "1994-11-04"
 */
public class LocalDateFormatter implements Formatter<LocalDate> {

    @Override
    public LocalDate parse(String text, Locale locale) throws ParseException {
        return LocalDate.parse(text, DateTimeFormatter.ISO_DATE);
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        return DateTimeFormatter.ISO_DATE.format(object);
    }
}
