package com.softwarelab.dataextractor.core.utilities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public class DateTimeUtil {
    public static String getDateTime(Instant instant){
        if(instant == null)return "";
        ZonedDateTime zd2 = instant.atZone(ZoneId.of("Asia/Tokyo"));
        //ZonedDateTime zd3 = now.atZone(ZoneId.systemDefault());
        return zd2.toString();
    }
    public static String isoDateTime(Instant instant){
        if(instant == null)return "";
        return LocalDateTime.ofInstant(instant,ZoneId.of("Asia/Tokyo")).format(DateTimeFormatter.ISO_DATE_TIME);
    }
    public static Instant getInstantTime(String isoTime){
        return Instant.parse(isoTime);
    }
}
