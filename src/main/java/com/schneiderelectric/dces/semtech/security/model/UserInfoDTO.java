package com.schneiderelectric.dces.semtech.security.model;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> authorities;
}
