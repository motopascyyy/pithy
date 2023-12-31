package com.pasciitools.pithy.task;

import com.pasciitools.pithy.git.GitHelper;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;

@Configuration
@EnableScheduling
public class BlogRefresh {

    private final Logger log = LoggerFactory.getLogger(BlogRefresh.class);

    private final GitHelper githelper;


    @Scheduled (fixedRate = 60 * 1000L)
    public void refreshBlog () {
        try {
            var fetchResult = githelper.fetchUpdates();
            if (fetchResult != null && fetchResult.getTrackingRefUpdates() != null
                    && !fetchResult.getTrackingRefUpdates().isEmpty()) {
                ObjectId oldHead = githelper.getObjectId("HEAD^{tree}");
                githelper.pullUpdates();
                ObjectId newHead = githelper.getObjectId("HEAD^{tree}");
                var diffs = githelper.getDiffs(oldHead, newHead);
                if (diffs != null && !diffs.isEmpty()) {
                    var infoMsg = String.format("Blog entries updated:%n%s", diffs);
                    log.info(infoMsg);
                }
            }

        } catch (GitAPIException | IOException e) {
            log.error("Problem initializing Git", e);
        }
    }

    public BlogRefresh (GitHelper gitHelper) {
        this.githelper = gitHelper;
    }

}
