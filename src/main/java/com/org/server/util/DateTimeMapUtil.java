package com.org.server.util;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;

public class DateTimeMapUtil {

    public static final DateTimeFormatter FLEXIBLE_NANO_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss") // 기본 날짜-시간 패턴
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true) // 소수점 초 (나노초)를 0~9자리까지 선택적으로 추가
            .toFormatter();
    public static final LocalDateTime parseClientTimetoServerFormat(String time){
        return LocalDateTime.parse(time,FLEXIBLE_NANO_FORMATTER);
    }
    public static final String parseServerTimeToClientFormat(LocalDateTime date){
        return FLEXIBLE_NANO_FORMATTER.format(date);
    }
    public static String dateFormat(Date date, String mysqlFormatPattern) {
        if (date == null) {
            return null;
        }
        // MySQL 패턴을 Java SimpleDateFormat 패턴으로 변환
        String javaFormatPattern = mysqlFormatPattern
                .replace("%Y", "yyyy")
                .replace("%m", "MM")
                .replace("%d", "dd")
                .replace("%H", "HH") // 시 (00-23)
                .replace("%i", "mm") // 분 (00-59)
                .replace("%s", "ss"); // 초 (00-59)

        SimpleDateFormat sdf = new SimpleDateFormat(javaFormatPattern);
        return sdf.format(date);
    }
}
