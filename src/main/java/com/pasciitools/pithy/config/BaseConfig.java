package com.pasciitools.pithy.config;

import com.pasciitools.pithy.data.AppConfigRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Component
@Data
@AllArgsConstructor
public class BaseConfig implements WebMvcConfigurer {

    private AppConfigRepo appConfigRepo;

    private static final Logger log = LoggerFactory.getLogger(BaseConfig.class);
    @Bean
    public Git gitBean () throws IOException {

        Optional<Git> gitOptional = Optional.empty();
        var repoPathOpt = appConfigRepo.findByConfigKey("git.repoPath");
        if (repoPathOpt.isPresent()){
            try {
                var git = Git.open(new File(repoPathOpt.get().getConfigValue()));
//                gitOptional = Optional.of(git);
                return git;
            } catch (IOException e) {
                log.warn("Could not open Git repo at path {}. Please make sure it is configured at /config and restart the service.", repoPathOpt.get().getConfigValue());
                return null;
            }
        } else {
            log.warn("Git repo path has not been configured. Please make sure it is configured at /config and restart the service.");
            return null;
        }
    }

    @Bean
    public CredentialsProvider credProv () {
        var user = appConfigRepo.findByConfigKey("git.user");
        var password = appConfigRepo.findByConfigKey("git.password");
        if (user.isPresent() && password.isPresent()) {
            var userCreds =  new UsernamePasswordCredentialsProvider(user.get().getConfigValue(), password.get().getConfigValue());
            return userCreds;
        } else {
            log.error("Git user or password has not been configured. Please ensure they are is set at /config and restart the service.");
        }
        return null;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        var repoPathOpt = appConfigRepo.findByConfigKey("git.repoPath");
        if (repoPathOpt.isPresent()) {
            var repoPath = repoPathOpt.get().getConfigValue();
            repoPath = repoPath.endsWith(File.separator) ? repoPath : repoPath + File.separator;
            var filePathRoot = "file:" + repoPath;
            registry.
                    addResourceHandler("/blog-repo/**").
                    addResourceLocations(filePathRoot).setCachePeriod(3600).resourceChain(true);
        }
    }
}
