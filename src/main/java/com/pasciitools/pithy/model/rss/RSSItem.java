package com.pasciitools.pithy.model.rss;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RSSItem {
    private String title;
    private String author;
    private String link;
    private LocalDateTime pubDate;
}
