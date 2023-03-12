package com.pasciitools.pithy.model;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.HashSet;

@Data
public class FileMetaData {

    private ZonedDateTime initialDate;
    private ZonedDateTime latestTime;
    private HashSet<String> authors;

    public FileMetaData() {
        authors = new HashSet<>();
    }
}
