package com.pasciitools.pithy.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;

@Data
public class Post implements Comparable<Post> {
    private String author;
    private ZonedDateTime dateCreated;
    private ZonedDateTime lastUpdated;
    private String postName;

    private static ZonedDateTime minDate = ZonedDateTime.of(LocalDateTime.MIN, ZoneId.of("UTC"));

    private String content;

    private String lead;

    private String url;
    private String encodedUrl;

    public Post(String author, ZonedDateTime dateCreated, ZonedDateTime lastUpdated, String postName) {
        this.author = author;
        this.dateCreated = dateCreated;
        this.lastUpdated = lastUpdated;
        this.postName = postName;
    }

    @Override
    public int compareTo(@NotNull Post o) {
        var comparedDate = o.getDateCreated() != null ? o.getDateCreated() : minDate;
        var thisCreatedDate = this.getDateCreated() != null ? this.getDateCreated() : minDate;
        return comparedDate.compareTo(thisCreatedDate);
    }


    public boolean isUpdated () {
        if (dateCreated != null && lastUpdated != null) {
            return !dateCreated.truncatedTo(ChronoUnit.SECONDS).isEqual(
                    lastUpdated.truncatedTo(ChronoUnit.SECONDS));
        } else {
            return false;
        }
    }

    public String getFormattedCreatedDate () {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
        return dateCreated != null ? dateCreated.format(formatter) : null;
    }

    public String getFormattedUpdatedDate () {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);
        return lastUpdated != null ? lastUpdated.format(formatter): null;
    }
}
