package com.pasciitools.pithy.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;

@Data
public class FileMetaData {

    private LocalDateTime initialDate;
    private LocalDateTime latestTime;
    private HashSet<String> authors;

    public FileMetaData() {
        authors = new HashSet<>();
    }
}
