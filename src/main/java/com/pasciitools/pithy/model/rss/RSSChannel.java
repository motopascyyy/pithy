package com.pasciitools.pithy.model.rss;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RSSChannel {

    private List<RSSItem> items;
    private String title;
    private String link;
    private String description;
    private String language;

    public void addItem(RSSItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
    }
}

