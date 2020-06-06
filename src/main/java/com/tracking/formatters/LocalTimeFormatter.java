package com.tracking.formatters;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.format.Formatter;

/**
 * LocalTime <-> String formatter for REST APIs
 *
 * @author martin
 * @example "15:00:00"
 */
public class LocalTimeFormatter implements Formatter<LocalTime> {

    @Override
    public LocalTime parse(String text, Locale locale) throws ParseException {
        return LocalTime.parse(text, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    @Override
    public String print(LocalTime object, Locale locale) {
        return DateTimeFormatter.ISO_LOCAL_TIME.format(object);
    }
}
