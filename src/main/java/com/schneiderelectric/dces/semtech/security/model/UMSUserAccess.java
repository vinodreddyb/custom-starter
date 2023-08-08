package com.schneiderelectric.dces.semtech.security.model;

import lombok.Data;

@Data
public class UMSUserAccess {
    private String type;
    private String id;

    public UMSUserAccess() {
    }
}