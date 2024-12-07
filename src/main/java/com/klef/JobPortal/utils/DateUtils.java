package com.klef.JobPortal.utils;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    /**
     * Generates a timestamp in the format "2024-08-22T15:33:22.999129088Z"
     *
     * @return formatted timestamp as String
     */
    public static String generateTimeStamp() {
        // Generate the current UTC timestamp with nanosecond precision
        Instant now = Instant.now();

        // Format it to ISO 8601 standard with nanoseconds and return
        return DateTimeFormatter.ISO_INSTANT.format(now);
    }
}

