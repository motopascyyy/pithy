package com.pasciitools.pithy.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AppConfigDTO {
    private List<AppConfig> appConfigList;

    public AppConfigDTO(List<AppConfig> appConfigs) {
        appConfigList = appConfigs;
    }

    public AppConfigDTO () {}

    public void addAppConfig(AppConfig appConfig) {
        if (appConfigList == null)
            appConfigList = new ArrayList<>();

        appConfigList.add(appConfig);
    }
}
