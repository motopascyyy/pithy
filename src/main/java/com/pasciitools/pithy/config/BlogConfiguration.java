package com.pasciitools.pithy.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "blog")
@Data
@Validated
public class BlogConfiguration {

    @NotEmpty
    private String url;
    private String title;
    private String description;
    private String language;
}
