package com.pasciitools.pithy.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;

@Data
public class Post implements Comparable<Post> {
    private String author;
    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdated;
    private String postName;

    private String content;

    private String lead;

    private String url;

    public Post(String author, LocalDateTime dateCreated, LocalDateTime lastUpdated, String postName) {
        this.author = author;
        this.dateCreated = dateCreated;
        this.lastUpdated = lastUpdated;
        this.postName = postName;
    }

    @Override
    public int compareTo(@NotNull Post o) {
        LocalDateTime comparedDate = o.getDateCreated() != null ? o.getDateCreated() : LocalDateTime.MIN;
        LocalDateTime thisCreatedDate = this.getDateCreated() != null ? this.getDateCreated() : LocalDateTime.MIN;
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
        return lastUpdated != null ? lastUpdated.atZone(ZoneId.systemDefault()).format(formatter): null;
    }
}
