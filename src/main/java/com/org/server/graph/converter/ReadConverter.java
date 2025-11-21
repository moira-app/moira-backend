package com.org.server.graph.converter;

import org.springframework.core.convert.converter.Converter;

import org.bson.json.StrictJsonWriter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@ReadingConverter
public class ReadConverter implements Converter<Date,LocalDateTime> {

    private static final int kst_offset=9;

    @Override
    public LocalDateTime convert(Date source) {
        LocalDateTime localDateTime=source.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.minusHours(kst_offset);
    }
}
