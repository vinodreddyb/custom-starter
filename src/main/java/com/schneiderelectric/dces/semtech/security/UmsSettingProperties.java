package com.schneiderelectric.dces.semtech.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "ums.application")
@Data
public class UmsSettingProperties {

    private String[] securedUrls;
    private Map<String, String[]> allowedUrls;
    private String appId;
    private String groupName;
    private String tokenUrl;
}
