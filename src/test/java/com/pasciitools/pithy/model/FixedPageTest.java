package com.pasciitools.pithy.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import static org.assertj.core.api.Assertions.assertThat;

class FixedPageTest {

    FixedPage.FixedPageBuilder pageBuilder;

    @BeforeEach
    void init() {
        pageBuilder = new FixedPage().toBuilder().
                dateCreated(LocalDateTime.now()).
                lastUpdated(LocalDateTime.now()).
                fileName("something").
                label("something").
                url("hello.world").
                content("something deep");
    }

    @Test
    void testSortingWithOrderSpecified () {

        var pageList = new ArrayList<FixedPage>();
        for (int i = 5; i >=0; i--) {
            pageBuilder.pageOrder(i);
            pageList.add(pageBuilder.build());
        }
        Collections.sort(pageList);
        for (int i = 0; i < pageList.size(); i++) {
            assertThat(pageList.get(i).getPageOrder()).isEqualTo(i);
        }
    }

    @Test
    void testSortingWithNoPageOrderSpecified(){

        var pageList = new ArrayList<FixedPage>();

        for (int i = 5; i >=0; i--) {
            pageBuilder.fileName(i + "something");
            pageList.add(pageBuilder.build());
        }

        Collections.sort(pageList);
        for (int i = 0; i < pageList.size(); i++) {
            var fixedPage = pageList.get(i);
            assertThat(fixedPage.getPageOrder()).isEqualTo(Integer.MAX_VALUE);
            assertThat(fixedPage.getFileName()).startsWith(Integer.toString(i));
        }
    }

    @Test
    void testWithSomeOrdersSpecified () {
        var page1 = pageBuilder.pageOrder(0).build();
        init();
        var page2 = pageBuilder.pageOrder(2).build();
        init();
        var page3 = pageBuilder.fileName("0abc").build();
        init();
        var page4 = pageBuilder.fileName("1abc").build();

        var pageList = new ArrayList<FixedPage>();

        pageList.add(page4);
        pageList.add(page3);
        pageList.add(page2);
        pageList.add(page1);

        Collections.sort(pageList);

        assertThat(pageList.get(0)).isEqualTo(page1);
        assertThat(pageList.get(1)).isEqualTo(page2);
        assertThat(pageList.get(2)).isEqualTo(page3);
        assertThat(pageList.get(3)).isEqualTo(page4);
    }
}
