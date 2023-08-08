package com.schneiderelectric.dces.semtech.security.model;

import lombok.Data;

import java.util.List;

@Data
public class UMSUserApplicationGroup {
    private String name;
    private String id;
    private List<UMSUserApplicationAttribute> attributes;

}