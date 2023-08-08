package com.schneiderelectric.dces.semtech.security.model;

import lombok.Data;

import java.util.List;

@Data
public class UMSUserApplication {
    private String id;
    private List<UMSUserApplicationAttribute> attributes;
    private List<UMSUserApplicationGroup> groups;

    public UMSUserApplication() {
    }
}