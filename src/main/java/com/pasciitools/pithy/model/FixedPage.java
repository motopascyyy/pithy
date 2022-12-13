package com.pasciitools.pithy.model;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class FixedPage implements Comparable<FixedPage>{

    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdated;
    private String content;
    private String url;
    private String fileName;
    private String label;

    @Builder.Default
    private Integer pageOrder = Integer.MIN_VALUE;

    /**
     * First compares if an order has been specified. If one was not provided, a default order of -1 is used for
     * that FixedPage.
     * If the orders match or -1 is used, then it reverts to comparing filenames since no 2 files in the same
     * directory can share the exact same name. No further comparison is needed at that point.
     * @param o the object to be compared.
     * @return -1 if the FixedPage provided has a higher order, or 1 if it's lower.
     */
    @Override
    public int compareTo(@NotNull FixedPage o) {
        var pageOrderComparison = this.getPageOrder().compareTo(o.getPageOrder());
        if (pageOrderComparison == 0)
            return this.getFileName().compareTo(o.getFileName());
        return pageOrderComparison;
    }


}
