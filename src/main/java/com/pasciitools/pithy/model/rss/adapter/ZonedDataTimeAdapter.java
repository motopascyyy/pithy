package com.pasciitools.pithy.model.rss.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDataTimeAdapter extends XmlAdapter<String, ZonedDateTime> {

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String marshal(ZonedDateTime dateTime) {
        return dateTime.format(dateFormat);
    }

    @Override
    public ZonedDateTime unmarshal(String dateTime) {
        return ZonedDateTime.parse(dateTime, dateFormat);
    }

}
