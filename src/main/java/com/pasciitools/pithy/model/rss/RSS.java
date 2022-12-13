package com.pasciitools.pithy.model.rss;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement
@Data
public class RSS {
    private RSSChannel channel;
    private String rssVersionNumber = "2.0";
    private String version;

    @XmlAttribute(name = "version")
    public String getRssVersionNumber() {
        return rssVersionNumber;
    }
    public void setRssVersionNumber(String rssVersionNumber) {
        this.rssVersionNumber = rssVersionNumber;
    }


    public RSS () {
        this.channel = new RSSChannel();
    }
}
