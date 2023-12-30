package com.pasciitools.pithy.data;

import com.pasciitools.pithy.model.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppConfigRepo extends JpaRepository<AppConfig, String> {

    Optional<AppConfig> findByConfigKey(String configKey);

}
