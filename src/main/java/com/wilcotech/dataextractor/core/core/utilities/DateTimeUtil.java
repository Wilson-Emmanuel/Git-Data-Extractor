package com.wilcotech.dataextractor.core.core.utilities;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
}
