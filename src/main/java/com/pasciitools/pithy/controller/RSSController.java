package com.pasciitools.pithy.controller;

import com.pasciitools.pithy.config.BlogConfiguration;
import com.pasciitools.pithy.data.PostRepository;
import com.pasciitools.pithy.model.Post;
import com.pasciitools.pithy.model.rss.RSS;
import com.pasciitools.pithy.model.rss.RSSChannel;
import com.pasciitools.pithy.model.rss.RSSItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
public class RSSController {

    private final PostRepository postRepository;
    private final BlogConfiguration blogConfiguration;

    @GetMapping(value = "/rss", produces = {MediaType.APPLICATION_RSS_XML_VALUE, MediaType.TEXT_XML_VALUE})
    public RSS getRSSFeed () {
        var feedContent = new RSS();
        try {
            var posts = postRepository.getListOfFiles();
            RSSChannel channel = feedContent.getChannel();
            channel.setTitle(blogConfiguration.getTitle());
            channel.setDescription(blogConfiguration.getDescription());
            channel.setLink(blogConfiguration.getUrl());
            channel.setLanguage(blogConfiguration.getLanguage());
            for (Post p : posts) {
                var entry = new RSSItem();
                entry.setTitle(p.getPostName());
                entry.setAuthor(p.getAuthor());
                entry.setLink(blogConfiguration.getUrl() + p.getUrl());
                entry.setPubDate(p.getDateCreated());
                channel.addItem(entry);
            }
            return feedContent;
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "IOException occurred while fetch RSS feed.", e);
        }
    }


    public RSSController (PostRepository postRepository, BlogConfiguration blogConfiguration) {
        this.postRepository = postRepository;
        this.blogConfiguration = blogConfiguration;
    }
}
