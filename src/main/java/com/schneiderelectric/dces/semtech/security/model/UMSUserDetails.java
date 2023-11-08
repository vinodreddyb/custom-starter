package com.schneiderelectric.dces.semtech.security.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UMSUserDetails {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String companyName;
    private String country;
    private String jobFunction;
    private String preferredLanguage;
    private String creationDate;
    private List<UMSUserAttribute> userAttributes;
    private List<UMSUserApplication> applications;
    private List<UMSUserSocialIdentity> socialIdentities;
    private List<UMSUserAccess> userAccess;
    private UMSError umsError;

}