package com.pasciitools.pithy.model.rss;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RSSChannel {

    private List<RSSItem> item; //sadly needs to be named singular so that the XML generation happens properly.
    private String title;
    private String link;
    private String description;
    private String language;

    public void addItem(RSSItem rssItem) {
        if (item == null) {
            item = new ArrayList<>();
        }
        item.add(rssItem);
    }
}

