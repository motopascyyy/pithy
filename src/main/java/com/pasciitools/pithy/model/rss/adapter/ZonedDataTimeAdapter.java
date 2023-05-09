package com.pasciitools.pithy.model.rss.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDataTimeAdapter extends XmlAdapter<String, ZonedDateTime> {

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ISO_INSTANT;
    @Override
    public String marshal(ZonedDateTime dateTime) {
        return dateTime.format(dateFormat);
    }

    @Override
    public ZonedDateTime unmarshal(String dateTime) {
        return ZonedDateTime.parse(dateTime, dateFormat);
    }

}
