package com.diasjoao.bolanatv.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    public static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yy");

    public static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("EEE\ndd MMM");

    public static boolean dateIsInThePast(Date date, String timeString) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime localTime = LocalTime.parse(timeString, formatter);

        LocalDateTime combinedDateTime = LocalDateTime.of(localDate, localTime);

        return combinedDateTime.isBefore(LocalDateTime.now());
    }
}
