package com.pasciitools.pithy.model.atom;

import com.pasciitools.pithy.model.atom.adapter.ZonedDataTimeAdapter;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement (name = "feed")
public class AtomFeed {
    private String xmlns = "http://www.w3.org/2005/Atom";

    @XmlAttribute(name = "xmlns")
    public String getXmlNS() {
        return xmlns;
    }
    public void setXmlNS(String xmlns) {
        this.xmlns = xmlns;
    }

    @Getter @Setter
    private List<AtomEntry> entry;

    @Getter @Setter
    private String id;
    @Getter @Setter
    private String title;
    @Getter @Setter
    private String subtitle;
    @Setter
    private ZonedDateTime updated;
    @Getter @Setter
    private List<AtomLink> link;
    @Getter @Setter
    private String rights;

    @XmlElement
    @XmlJavaTypeAdapter(ZonedDataTimeAdapter.class)
    public ZonedDateTime getUpdated() {
        return updated;
    }

    public void addItem(AtomEntry rssItem) {
        if (entry == null) {
            entry = new ArrayList<>();
        }
        entry.add(rssItem);
    }
}
