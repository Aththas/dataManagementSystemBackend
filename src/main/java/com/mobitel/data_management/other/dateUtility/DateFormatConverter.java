package com.mobitel.data_management.other.dateUtility;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
@Component
public class DateFormatConverter {

    public String convertDateFormat(String originalDateString) {
        // Define the formatter for the input date string
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

        // Define the formatter for the desired output format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");

        // Parse the input date string to a LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(originalDateString, inputFormatter);

        // Convert LocalDateTime to ZonedDateTime with the default time zone (IST in this case)
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Kolkata")); // IST time zone

        // Format the ZonedDateTime to the desired output format
        return outputFormatter.format(zonedDateTime);
    }
}
