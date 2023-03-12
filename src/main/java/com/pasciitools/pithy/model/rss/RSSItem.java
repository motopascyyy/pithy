package com.pasciitools.pithy.model.rss;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class RSSItem {
    private String title;
    private String author;
    private String link;
    private ZonedDateTime pubDate;
}
