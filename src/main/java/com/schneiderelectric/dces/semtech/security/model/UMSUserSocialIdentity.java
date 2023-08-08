package com.schneiderelectric.dces.semtech.security.model;

import lombok.Data;

@Data
public class UMSUserSocialIdentity {
    private String providerName;
    private String providerUserId;
    private String userPictureUrl;

    public UMSUserSocialIdentity() {
    }
}