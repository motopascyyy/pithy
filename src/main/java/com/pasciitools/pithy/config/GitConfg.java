package com.pasciitools.pithy.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;

@Configuration
@ConfigurationProperties(prefix = "git")
@Validated
@Data
public class GitConfg implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(GitConfg.class);

    @NotEmpty
    private String repoPath;

    @NotEmpty
    private String user;

    @NotEmpty
    private String password;


    @Bean
    public Git gitBean () throws IOException {
        try {
            return Git.open(new File(repoPath));
        } catch (IOException e) {
            var errMsg = String.format("Could not open Git repo at path %s", repoPath);
            throw new IOException(errMsg, e);
        }
    }

    @Bean
    public CredentialsProvider credProv () {
        return new UsernamePasswordCredentialsProvider(user, password);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        repoPath = repoPath.endsWith(File.separator) ? repoPath : repoPath + File.separator;
        var filePathRoot = "file:" + repoPath;
        registry.
                addResourceHandler("/blog-repo/**").
                addResourceLocations(filePathRoot).setCachePeriod(3600).resourceChain(true);
    }
}
