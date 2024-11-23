package backend.Entity.validation;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtil {

//Checks if the tickek can be canceled on the 72-hour prior rule

    public static boolean canCancelTicket(LocalDateTime showTime){
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilShow = ChronoUnit.HOURS.between(now, showTime);
        return hoursUntilShow >= 72;
    }

//Checks if the credit is still valid based on the four-year expiration rule

    public static boolean iscreditValid(LocalDateTime creditIssueDate){
        LocalDateTime now = LocalDateTime.now();
        long daysSinceIssued = ChronoUnit.DAYS.between(creditIssueDate, now);
        return daysSinceIssued <= 1461;
    }

//Formats a LocalDateTime object into a redable string

    public static String formatDateTime(LocalDateTime dateTime){
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));

    }

//Parses a date-time string into a LocalDateTime object
    public static LocalDateTime parseDateTime(String dateTimeStr){
        return LocalDateTime.parse(dateTimeStr, java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }
     
}
