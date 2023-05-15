package com.pasciitools.pithy.model.atom;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Builder;
import lombok.Setter;

@Builder
public class AtomLink {

    @Setter
    private String rel;
    @Setter
    private String href;
    @Setter private String type;
    @XmlAttribute(name = "rel")
    public String getRel () {
        return rel;
    }

    @XmlAttribute(name = "href")
    public String getHref () {
        return href;
    }

    @XmlAttribute(name = "type")
    public String getType() {
        return type;
    }

}
