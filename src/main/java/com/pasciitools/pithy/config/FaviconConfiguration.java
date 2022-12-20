package com.pasciitools.pithy.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "favicon")
@Data
@Validated
public class FaviconConfiguration {

    @NotNull
    private File faviconFile;

    private static final Logger log = LoggerFactory.getLogger(FaviconConfiguration.class);
    @Bean
    public SimpleUrlHandlerMapping customFaviconHandlerMapping() {
        if (faviconFile.exists() && faviconFile.isFile()) {
            SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
            mapping.setOrder(Integer.MIN_VALUE);  // to be first
            mapping.setUrlMap(Collections.singletonMap("/favicon.ico", faviconRequestHandler()));  // use the handler defined below

            return mapping;
        } else if (faviconFile.exists() && !faviconFile.isFile()){
            log.warn("favicon property \"favicon.faviconFile\": {} exists but does not point to a file (e.g it's a directory). " +
                    "No favicon will be served.", faviconFile.getAbsolutePath());
        } else {
            log.warn("favicon property \"favicon.faviconFile\": {} does not exist or isn't accessible. " +
                    "No favicon will be served.", faviconFile.getAbsolutePath());
        }
        return null;
    }

    @Bean
    protected ResourceHttpRequestHandler faviconRequestHandler() {
        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
        FileSystemResource faviconResource = new FileSystemResource(faviconFile);
        List<Resource> locations = List.of(faviconResource);
        requestHandler.setLocations(locations);
        return requestHandler;
    }
}
