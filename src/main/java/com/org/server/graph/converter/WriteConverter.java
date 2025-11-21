package com.org.server.graph.converter;


import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Component
@WritingConverter
public class WriteConverter implements Converter<LocalDateTime, Date> {


    private final static int kst_offset=9;
    @Override
    public Date convert(LocalDateTime source) {
        return Timestamp.valueOf(source.plusHours(kst_offset));
    }
}
