package com.schneiderelectric.dces.semtech.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ums.application")
@Data
public class UmsSettingProperties {

    private String[] securedUrls;
    private String[] allowedUrls;
    private String appId;
    private String groupName;
    private String tokenUrl;
}
