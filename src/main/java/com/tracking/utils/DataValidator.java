package com.tracking.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility structure for basic data form validations
 *
 * @author martin
 */
public class DataValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^.+@.+\\..+$");

    /**
     * Check is the provided email valid.
     *
     * @param email The email need to be checked
     * @return The state of the validation
     */
    public static boolean isValidEmail(final String email) {
        
        if (email == null || email.isEmpty()) {
            return false;
        }

        final Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
}
