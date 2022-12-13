package com.pasciitools.pithy.data;

import com.pasciitools.pithy.git.GitHelper;
import com.pasciitools.pithy.model.FileMetaData;
import com.pasciitools.pithy.model.Post;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.util.UriEncoder;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class PostRepository extends PageRepository implements Serializable {
    @Serial
    private static final long serialVersionUID = -3432302854586266576L;
    private static final Logger log = LoggerFactory.getLogger(PostRepository.class);

    private final GitHelper gitHelper;

    public List<Post> getListOfFiles () throws IOException {
        File repoFolder = gitHelper.getRepoRootDirectory();
        if (!repoFolder.exists() || !repoFolder.isDirectory() || !repoFolder.canExecute()) {
            throw new IOException("Could not access blog repository folder. Please make sure the `defaultGitPath` is properly configured");
        }
        var listOfPosts = new ArrayList<Post>();
        FilenameFilter filenameFilter = (file, s) -> s.endsWith(".md");
        var fileList = repoFolder.listFiles(filenameFilter);
        if (fileList != null && fileList.length > 0) {
            for (File file : fileList) {
                try {
                    Post post = createPostObject(file, gitHelper);
                    String content = githubMarkdown(file);
                    post.setContent(content);
                    post.setLead(content.substring(0, Math.min(400, content.length() - 1)) + "...");
                    post.setUrl(UriEncoder.encode(post.getPostName()));
                    listOfPosts.add(post);
                } catch (GitAPIException e) {
                    log.error("Couldn't get metadata from file: " + file.getName(), e);
                }
            }
        }
        Collections.sort(listOfPosts);
        return listOfPosts;
    }

    public Post createPostObject (File file, GitHelper gitHelper) throws GitAPIException {
        FileMetaData metaData = gitHelper.getOriginDate(file.getName());
        var creationTime = metaData.getInitialDate();
        var updatedTime = metaData.getLatestTime();
        var authors = metaData.getAuthors().toString();
        String postName = file.getName().substring(0, file.getName().length() - 3);
        return new Post(authors,
                creationTime,
                updatedTime,
                postName);
    }

    public Post getPost (String postName) throws IOException {
        File repoFolder = gitHelper.getRepoRootDirectory();
        if (!repoFolder.exists() || !repoFolder.isDirectory() || !repoFolder.canExecute()) {
            throw new IOException("Could not access blog repository folder. " +
                    "Please make sure it exists at location: " + repoFolder.getAbsolutePath());
        }

        String fileName = postName + ".md";

        File file = new File (repoFolder.getAbsolutePath() + DELIM + fileName);
        Post post = null;
        if (file.exists()) {
            try {
                post = createPostObject(file, gitHelper);
                String content = githubMarkdown(file);
                post.setContent(content);
                post.setLead(content.substring(0, Math.min(400, content.length()-1)) + "...");
            } catch (GitAPIException e) {
                log.error("Couldn't create a Blog Post from input {} because of: {}", postName, e.getMessage());
            }
        }

        return post;
    }

    public PostRepository (GitHelper gitHelper) {
        this.gitHelper = gitHelper;
    }

}
