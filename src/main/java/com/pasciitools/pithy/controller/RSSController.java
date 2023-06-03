package com.pasciitools.pithy.controller;

import com.pasciitools.pithy.config.BlogConfiguration;
import com.pasciitools.pithy.data.PostRepository;
import com.pasciitools.pithy.model.Post;
import com.pasciitools.pithy.model.atom.AtomEntry;
import com.pasciitools.pithy.model.atom.AtomEntryAuthor;
import com.pasciitools.pithy.model.atom.AtomFeed;
import com.pasciitools.pithy.model.atom.AtomLink;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
public class RSSController {

    private final PostRepository postRepository;
    private final BlogConfiguration blogConfiguration;

    @GetMapping(value = "/feeds/main", produces = {MediaType.APPLICATION_ATOM_XML_VALUE})
    public AtomFeed getAtomFeed () {
        var atomFeed = new AtomFeed();
        try {
            var posts = postRepository.getListOfFiles();
            atomFeed.setId(blogConfiguration.getUrl());
            atomFeed.setTitle(blogConfiguration.getTitle());
            AtomLink self = AtomLink.builder().rel("self").href(blogConfiguration.getUrl() + "feeds/main").type(MediaType.APPLICATION_ATOM_XML_VALUE).build();
            AtomLink alternate = AtomLink.builder().rel("alternate").href(blogConfiguration.getUrl()).type(MediaType.TEXT_HTML_VALUE).build();
            atomFeed.setLink(Arrays.asList(self, alternate));
            atomFeed.setRights("Copyright " + ZonedDateTime.now().getYear() + " " + blogConfiguration.getTitle());
            atomFeed.setUpdated(posts.get(0).getLastUpdated());
            for (Post p : posts) {
                var entry = new AtomEntry();
                entry.setTitle(p.getPostName());
                AtomEntryAuthor entryAuthor = new AtomEntryAuthor();
                entryAuthor.setName(p.getAuthor());

                entry.setAuthor(List.of(entryAuthor));
                var link = blogConfiguration.getUrl().endsWith("/") ?
                        blogConfiguration.getUrl() + p.getEncodedUrl() :
                        blogConfiguration.getUrl() + "/" + p.getEncodedUrl();
                entry.setLink(link);
                entry.setPublished(p.getDateCreated());
                entry.setUpdated(p.getLastUpdated());
                entry.setSummary(p.getLead());
                entry.setContent(p.getContent());
                atomFeed.addItem(entry);
            }

            return atomFeed;
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
