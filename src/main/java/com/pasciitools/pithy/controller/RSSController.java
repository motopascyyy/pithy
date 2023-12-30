package com.pasciitools.pithy.controller;

import com.pasciitools.model.atom.AtomEntry;
import com.pasciitools.model.atom.AtomEntryAuthor;
import com.pasciitools.model.atom.AtomFeed;
import com.pasciitools.model.atom.AtomLink;
import com.pasciitools.pithy.data.AppConfigRepo;
import com.pasciitools.pithy.data.PostRepository;
import com.pasciitools.pithy.model.AppConfig;
import com.pasciitools.pithy.model.Post;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RestController
@AllArgsConstructor
public class RSSController {

    private final PostRepository postRepository;
    private final AppConfigRepo appConfigRepo;

    @GetMapping(value = "/feeds/main", produces = {MediaType.APPLICATION_ATOM_XML_VALUE})
    public AtomFeed getAtomFeed () {
        Optional<AppConfig> blogUrlOpt = appConfigRepo.findByConfigKey("blog.url");
        String blogUrl = blogUrlOpt.isEmpty() ?
                "UNDEFINED blog.url" :
                blogUrlOpt.get().getConfigValue();

        Optional<AppConfig> blogTitleOpt = appConfigRepo.findByConfigKey("blog.title");
        String blogTitle = blogTitleOpt.isEmpty() ?
                "UNDEFINED blog.title" :
                blogTitleOpt.get().getConfigValue();
        var atomFeed = new AtomFeed();
        try {
            var posts = postRepository.getListOfFiles();
            atomFeed.setId(blogUrl);
            atomFeed.setTitle(blogTitle);
            AtomLink self = AtomLink.builder().rel("self").href(blogUrl + "feeds/main").type(MediaType.APPLICATION_ATOM_XML_VALUE).build();
            AtomLink alternate = AtomLink.builder().rel("alternate").href(blogUrl).type(MediaType.TEXT_HTML_VALUE).build();
            atomFeed.setLink(Arrays.asList(self, alternate));
            atomFeed.setRights("Copyright " + ZonedDateTime.now().getYear() + " " + blogTitle);
            atomFeed.setUpdated(posts.get(0).getLastUpdated());
            Function<Post, AtomEntry> postToAtomEntryMapper = (Post p) -> {
                var entry = new AtomEntry();
                entry.setTitle(p.getPostName());
                AtomEntryAuthor entryAuthor = new AtomEntryAuthor();
                entryAuthor.setName(p.getAuthor());

                entry.setAuthor(List.of(entryAuthor));
                var link = blogUrl.endsWith("/") ?
                        blogUrl + p.getEncodedUrl() :
                        blogUrl + "/" + p.getEncodedUrl();
                entry.setLink(link);
                entry.setPublished(p.getDateCreated());
                entry.setUpdated(p.getLastUpdated());
                entry.setSummary(p.getLead());
                entry.setContent(p.getContent());
                return entry;
            };
            atomFeed.addItems(posts.stream().map(postToAtomEntryMapper).toList());


            return atomFeed;
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "IOException occurred while fetch RSS feed.", e);
        }
    }

}
