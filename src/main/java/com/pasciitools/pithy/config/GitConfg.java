package com.pasciitools.pithy.config;

import jakarta.validation.constraints.NotEmpty;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.io.IOException;

@Configuration
@Validated
public class GitConfg {

    @Value("${defaultGitPath}")
    @NotEmpty
    private String blogRepoRootPath;

    @Value("${gitUser}")
    @NotEmpty
    private String gitUserName;

    @Value("${gitPassword}")
    @NotEmpty
    private String gitPassword;


    @Bean
    public Git gitBean () throws IOException {
        try {
            return Git.open(new File(blogRepoRootPath));
        } catch (IOException e) {
            var errMsg = String.format("Could not open Git repo at path %s", blogRepoRootPath);
            throw new IOException(errMsg, e);
        }
    }

    @Bean
    public CredentialsProvider credProv () {
        return new UsernamePasswordCredentialsProvider(gitUserName, gitPassword);
    }

}
