package com.pasciitools.pithy.config;

import com.pasciitools.pithy.data.AppConfigRepo;
import lombok.AllArgsConstructor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Not to be actively used in other classes. This is here to populate the AppConfigRepo on startup.
 *
 */
@AllArgsConstructor
public class ApplicationConfiguration implements EnvironmentAware {

    private AppConfigRepo appConfigRepo;
    @Override
    public void setEnvironment(Environment environment) {
        ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;

        Map<String, Object> propertySource = new HashMap<>();
        appConfigRepo.findAll().stream().forEach(config ->
                propertySource.put(config.getConfigKey(), config.getConfigValue()));
        configurableEnvironment.getPropertySources().addAfter("systemEnvironment",
                new MapPropertySource("app-config", propertySource));
    }



}
