package com.pasciitools.pithy.model.atom;

import com.pasciitools.pithy.model.atom.adapter.ZonedDataTimeAdapter;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

public class AtomEntry {

    @Getter @Setter
    private String id;
    @Getter @Setter
    private String title;
    @Setter
    private ZonedDateTime updated;
    @Setter
    private ZonedDateTime published;
    @Getter @Setter
    private List<AtomEntryAuthor> author;
    @Getter @Setter
    private String content;
    @Getter @Setter
    private String link;
    @Getter @Setter
    private String summary;

    @XmlElement
    @XmlJavaTypeAdapter(ZonedDataTimeAdapter.class)
    public ZonedDateTime getPublished() {
        return published;
    }

    @XmlElement
    @XmlJavaTypeAdapter(ZonedDataTimeAdapter.class)
    public ZonedDateTime getUpdated() {
        return updated;
    }



}
