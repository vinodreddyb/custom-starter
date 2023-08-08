package com.schneiderelectric.dces.semtech.security.model;

import lombok.Data;

@Data
public class UMSUserApplicationAttribute {
    private String name;
    private String value;
    private String type;

    public UMSUserApplicationAttribute() {
    }
}