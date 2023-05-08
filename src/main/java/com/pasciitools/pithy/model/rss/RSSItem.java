package com.pasciitools.pithy.model.rss;

import com.pasciitools.pithy.model.rss.adapter.ZonedDataTimeAdapter;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

//@Data
public class RSSItem {
    @Getter @Setter
    private String title;
    @Getter @Setter
    private String author;
    @Getter @Setter
    private String link;
    private ZonedDateTime pubDate;

    @XmlElement
    @XmlJavaTypeAdapter(ZonedDataTimeAdapter.class)
    public ZonedDateTime getPubDate () {
        return pubDate;
    }

    public void setPubDate (ZonedDateTime pubDate) {
        this.pubDate = pubDate;
    }
}
