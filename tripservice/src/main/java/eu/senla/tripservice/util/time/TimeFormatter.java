package eu.senla.tripservice.util.time;

import eu.senla.tripservice.exeption.ParseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeFormatter {
    private static final String pattern = "dd-MM-yyyy HH:mm";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

    public static LocalDateTime formatStringToDateTime(String dateTime) {
        int dateLengthWithoutHoursAndMinutes = 10;
        LocalDateTime localDateTime;

        if (dateTime.length() == dateLengthWithoutHoursAndMinutes) {
            dateTime += " 00:00";
        }

        try {
            localDateTime = LocalDateTime.parse(dateTime, formatter);
        } catch (DateTimeParseException exception) {
            throw new ParseException("Date and time must match the pattern: " + pattern);
        }

        return localDateTime;
    }

    public static String formatLocalDateTimeToString(LocalDateTime localDateTime) {
        return localDateTime.format(formatter);
    }
}
